package combine;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import common.Common;

/** Combine input files. */
public class Combine {
	public static final String OUTPUT_PATH = "/combine";
	
	/**
	 * Combines input files.
	 * 
	 * @param inputPath - the input path
	 * @param baseOutput - the root directory for the output
	 * @return The Counters for the job on success, otherwise null
	 */
	public static boolean combine(String inputPath, String baseOutput) throws Exception {
		Configuration conf = Common.setupConf();
		
		// Delete old output
		Common.deleteOld(conf, baseOutput + OUTPUT_PATH);

		// Job settings
		Job job = Job.getInstance(conf);
		job.setJarByClass(Combine.class);
		job.setMapperClass(CombineMapper.class);
		job.setReducerClass(CombineReducer.class);
		job.setMapOutputKeyClass(LongWritable.class);
		job.setOutputKeyClass(NullWritable.class);
		job.setOutputValueClass(Text.class);
		job.setNumReduceTasks(1);

		// File settings
		FileInputFormat.addInputPath(job, new Path(inputPath));
		FileOutputFormat.setOutputPath(job, new Path(baseOutput + OUTPUT_PATH));

		return job.waitForCompletion(true) ? true : false;
	}
}
