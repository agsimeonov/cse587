package pairs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import common.Common;
import common.CountRelative;

/** Hashtag co-occurrence using pairs approach. */
public class Pairs {
	public static final String OUTPUT_PATH = "/coocurrence/pairs";
	
	/**
	 * Hashtag co-occurrence using pairs approach.
	 * 
	 * @param inputPath - the input path
	 * @param baseOutput - the root directory for the output
	 * @return true on success, otherwise false
	 */
	public static boolean pairs(String inputPath, String baseOutput) throws Exception {
		Configuration conf = Common.setupConf();
		
		// Delete old output
		Common.deleteOld(conf, baseOutput + OUTPUT_PATH);

		// Job settings
		Job job = Job.getInstance(conf);
		job.setJarByClass(Pairs.class);
		job.setMapperClass(PairsMapper.class);
		job.setPartitionerClass(PairsPartitioner.class);
		job.setReducerClass(PairsReducer.class);
		job.setOutputKeyClass(Pair.class);
		job.setMapOutputValueClass(IntWritable.class);
		job.setOutputValueClass(CountRelative.class);
		job.setNumReduceTasks(2);

		// File settings
		FileInputFormat.addInputPath(job, new Path(inputPath));
		FileOutputFormat.setOutputPath(job, new Path(baseOutput + OUTPUT_PATH));

		return job.waitForCompletion(true) ? true : false;
	}
	
	/**
	 * Hashtag co-occurrence using pairs approach 5 reducers.
	 * 
	 * @param inputPath - the input path
	 * @param baseOutput - the root directory for the output
	 * @return true on success, otherwise false
	 */
	public static boolean pairsFive(String inputPath, String baseOutput) throws Exception {
		Configuration conf = Common.setupConf();
		
		// Delete old output
		Common.deleteOld(conf, baseOutput + OUTPUT_PATH + "_five");

		// Job settings
		Job job = Job.getInstance(conf);
		job.setJarByClass(Pairs.class);
		job.setMapperClass(PairsMapper.class);
		job.setPartitionerClass(PairsPartitioner.class);
		job.setReducerClass(PairsReducer.class);
		job.setOutputKeyClass(Pair.class);
		job.setMapOutputValueClass(IntWritable.class);
		job.setOutputValueClass(CountRelative.class);
		job.setNumReduceTasks(5);

		// File settings
		FileInputFormat.addInputPath(job, new Path(inputPath));
		FileOutputFormat.setOutputPath(job, new Path(baseOutput + OUTPUT_PATH + "_five"));

		return job.waitForCompletion(true) ? true : false;
	}
}
