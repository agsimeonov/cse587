package initcentroids;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;

import common.Common;

/** Initializes the centroids for K-means. */
public class InitCentroids {
	
	/**
	 * Initializes the centroids for K-means.
	 * 
	 * @param inputPath - the input path
	 * @param baseOutput - the root directory for the output
	 * @return The Counters for the job on success, otherwise null
	 */
	public static Counters initcentroids(String inputPath, String baseOutput) throws Exception {
		Configuration conf = Common.setupConf();
		
		// Delete old output
		Common.deleteFile(Common.CENTROID_FILE, Common.BASE_OUTPUT);

		// Job settings
		Job job = Job.getInstance(conf);
		job.setJarByClass(InitCentroids.class);
		job.setMapperClass(InitCentroidsMapper.class);
		job.setOutputFormatClass(NullOutputFormat.class);
		job.setNumReduceTasks(0);

		// File settings
		FileInputFormat.addInputPath(job, new Path(inputPath));

		return job.waitForCompletion(true) ? job.getCounters() : null;
	}
}
