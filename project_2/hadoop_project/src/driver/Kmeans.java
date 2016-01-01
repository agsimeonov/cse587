package driver;

import org.apache.hadoop.mapreduce.Counters;

import common.Common;

/** Driver for the followers K-means problem and passes the output to the word count and co-occurrence problem. */
public class Kmeans {
	
	public static void main(String[] args) throws Exception {
		System.exit(driver.Kmeans.kmeans(args) ? 0 : 1);
	}
	
	/**
	 * Runs through the followers K-means problem only.
	 * 
	 * @param args - command land arguments
	 * @return true on success, otherwise false
	 */
	public static boolean kmeansOnly(String[] args) throws Exception {
		String inputPath = Common.setInput(args);
		String baseOutput = Common.setBaseOutput(args);
		long low, medium, high;
		
		if (!combine.Combine.combine(inputPath, baseOutput)) return false;
		
		// Initialize centroids
		Counters counters = initcentroids.InitCentroids.initcentroids(baseOutput + combine.Combine.OUTPUT_PATH, baseOutput);
		if (counters == null) return false;
		if(counters.findCounter(Common.Centroids.HIGH).getValue() == 0) {
			System.err.println("Not enough entries in the input file(s) to initialize the centroids!");
			return false;
		} else {
			Common.setCentroids(counters);
			low = counters.findCounter(Common.Centroids.LOW).getValue();
			medium = counters.findCounter(Common.Centroids.LOW).getValue();
			high = counters.findCounter(Common.Centroids.LOW).getValue();
		}
		
		// General K-means driver
		while (true) {
			counters = kmeans.Kmeans.kmeans(baseOutput + combine.Combine.OUTPUT_PATH, baseOutput);
			if (counters == null) return false;
			Common.setCentroids(counters);
			
			long nextLow = counters.findCounter(Common.Centroids.LOW).getValue();
			long nextMedium = counters.findCounter(Common.Centroids.MEDIUM).getValue();
			long nextHigh = counters.findCounter(Common.Centroids.HIGH).getValue();
			
			if (nextLow == low && nextMedium == medium && nextHigh == high) {
				break;
			} else {
				low = nextLow;
				medium = nextMedium;
				high = nextHigh;
			}
		}
		
		// Finalize by providing more useful output for K-Means
		finkmeans.FinKmeans.finkmeans(inputPath, baseOutput);
		Common.cleanSuccess(baseOutput + finkmeans.FinKmeans.OUTPUT_PATH);

		Common.deleteOld(Common.setupConf(), baseOutput + combine.Combine.OUTPUT_PATH);
		
		return true;
	}
	
	/**
	 * Runs through the followers K-means problem and passes the output to the word count and co-occurrence problem.
	 * 
	 * @param args - command land arguments
	 * @return true on success, otherwise false
	 */
	public static boolean kmeans(String[] args) throws Exception {
		String baseOutput = Common.setBaseOutput(args);
		String[] inOut = new String[2];
		
		if (!kmeansOnly(args)) return false;
		
		// Wordcount and Co-occurrence for each cluster
		inOut[0] = baseOutput + finkmeans.FinKmeans.OUTPUT_PATH + "/low-*";
		inOut[1] = baseOutput + finkmeans.FinKmeans.OUTPUT_PATH + "/low";
		if (!KmeansWcCo(inOut)) return false;
		
		inOut[0] = baseOutput + finkmeans.FinKmeans.OUTPUT_PATH + "/medium-*";
		inOut[1] = baseOutput + finkmeans.FinKmeans.OUTPUT_PATH + "/medium";
		if (!KmeansWcCo(inOut)) return false;
		
		inOut[0] = baseOutput + finkmeans.FinKmeans.OUTPUT_PATH + "/high-*";
		inOut[1] = baseOutput + finkmeans.FinKmeans.OUTPUT_PATH + "/high";
		if (!KmeansWcCo(inOut)) return false;
		
		return true;
	}
	
	/**
	 * Runs through word count and co-occurrence for K-means.
	 * 
	 * @param args - args[0] = input path, args[1] = output path
	 * @return true on success, otherwise false
	 */
	public static boolean KmeansWcCo(String[] inOut) throws Exception {
		if (!driver.WordCount.wordcount(inOut)) return false;
		if (!driver.CoOccurrence.cooccurence(inOut)) return false;
		
		return true;
	}
}
