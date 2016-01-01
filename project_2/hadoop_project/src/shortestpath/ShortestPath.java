package shortestpath;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import common.Common;
import common.ShortestPathReducer;
import common.ShortestPathWritable;

/** Runs through shortest path algorithm */
public class ShortestPath {
	public static final String OUTPUT_PATH = Common.SPATH_OUTPUT + "/";

	/**
	 * Runs through the shortest path algorithm.
	 * 
	 * @param inputPath - the input path
	 * @param baseOutput - the root directory for the output
	 * @param iteration - the iteration number (appended to the output path)
	 * @return The Counters for the job on success, otherwise null
	 * @throws Exception
	 */
	public static Counters shortestPath(String inputPath, String baseOutput, Integer iteration) throws Exception {
		String outputPath = baseOutput + OUTPUT_PATH + iteration.toString();
		Configuration conf = Common.setupConf();
		
		// Delete old output
		Common.deleteOld(conf, outputPath);
				
		// Job settings
		Job job = Job.getInstance(conf);
		job.setJarByClass(ShortestPath.class);
		job.setMapperClass(ShortestPathMapper.class);
		job.setReducerClass(ShortestPathReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(ShortestPathWritable.class);
		job.setNumReduceTasks(2);
		
		// File settings
		FileInputFormat.addInputPath(job, new Path(inputPath));
		FileOutputFormat.setOutputPath(job, new Path(outputPath));
		
		return job.waitForCompletion(true) ? job.getCounters() : null;
	}
}
