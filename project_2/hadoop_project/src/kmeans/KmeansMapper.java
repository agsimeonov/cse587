package kmeans;

import java.io.IOException;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import common.Common;

public class KmeansMapper extends Mapper<Object, Text, Text, RecordsAveragePair> {
	private HashSet<String> usernames = new HashSet<String>();
	private long[] centroids = null;
	private long[] totalRecords = new long[] { 0, 0, 0 };
	private long[] totalCoordinates = new long[] {0, 0, 0};
	private Text clusterKey = new Text();
	private RecordsAveragePair clusterValue = new RecordsAveragePair();
	
	
	public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
		StringTokenizer tokens = new StringTokenizer(value.toString(), Common.INPUT_SEPARATOR);
		String username = null;
		Long followers = Long.MIN_VALUE;
		
		// Initialize with cluster centers from previous iteration
		if (centroids == null) {
			centroids = Common.getCentroids();
			context.getCounter(Common.Centroids.ITERATION).setValue(centroids[0]);
		}
		
		// Extract usersnames followers
		try {
			username = tokens.nextToken();
			followers = new Long(Integer.parseInt(tokens.nextToken().replaceAll("\\s", "")));
			tokens.nextToken();
			tokens.nextToken();
			tokens.nextToken();
		} catch (NoSuchElementException e) {
			return;
		} catch (NumberFormatException e) {
			return;
		}
		
		// Make sure we only count each user once
		if (usernames.contains(username)) return;
		usernames.add(username);
		
		// In-mapper combining
		if (followers != Long.MIN_VALUE) combiner(followers);
	}
	
	protected void cleanup(Context context) throws IOException, InterruptedException {
		if (totalRecords[0] != 0) {
			clusterKey.set(Common.Centroids.LOW.toString());
			clusterValue.set(totalRecords[0], totalCoordinates[0]/totalRecords[0]);
			context.write(clusterKey, clusterValue);
		}
		
		if (totalRecords[1] != 0) {
			clusterKey.set(Common.Centroids.MEDIUM.toString());
			clusterValue.set(totalRecords[1], totalCoordinates[1]/totalRecords[1]);
			context.write(clusterKey, clusterValue);
		}
		
		if (totalRecords[2] != 0) {
			clusterKey.set(Common.Centroids.HIGH.toString());
			clusterValue.set(totalRecords[2], totalCoordinates[2]/totalRecords[2]);
			context.write(clusterKey, clusterValue);
		}
	}
	
	/**
	 * In-mapper combiner.
	 * 
	 * @param followers - the number of followers for a user
	 */
	private void combiner(Long followers) {
		long distanceLow = Math.abs(centroids[1] - followers);
		long distanceMedium = Math.abs(centroids[2] - followers);
		long distanceHigh = Math.abs(centroids[3] - followers);
		
		if (distanceLow == distanceMedium) {
			combine(followers, 0);
		} else if (distanceMedium == distanceHigh) {
			combine(followers, 1);
		} else if (distanceLow < distanceHigh) {
			// Lower half
			if (distanceLow < distanceMedium) {
				combine(followers, 0);
			} else {
				combine(followers, 1);
			}
		} else {
			// Upper half
			if (distanceMedium < distanceHigh) {
				combine(followers, 1);
			} else {
				combine(followers, 2);
			}
		}
	}
	
	/**
	 * Combines at a certain index.
	 * 
	 * @param followers - the number of followers for a user
	 * @param index - the combination index (0 - low, 1 - medium, 2 - high)
	 */
	private void combine(Long followers, int index) {
		totalRecords[index] += 1;
		totalCoordinates[index] += followers;
	}
}
