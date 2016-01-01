package stripes;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SortedMapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import common.Common;
import common.CountRelative;

/** Hashtag co-occurrence using stripes approach. */
public class Stripes {
	public static final String OUTPUT_PATH = "/coocurrence/stripes";

	/**
	 * Hashtag co-occurrence using stripes approach.
	 * 
	 * @param inputPath - the input path
	 * @param baseOutput - the root directory for the output
	 * @return true on success, otherwise false
	 */
	public static boolean stripes(String inputPath, String baseOutput) throws Exception {
		Configuration conf = Common.setupConf();
		
		// Delete old output
		Common.deleteOld(conf, baseOutput + OUTPUT_PATH);

		// Job settings
		Job job = Job.getInstance(conf);
		job.setJarByClass(Stripes.class);
		job.setMapperClass(StripesMapper.class);
		job.setReducerClass(StripesReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setMapOutputValueClass(SortedMapWritable.class);
		job.setOutputValueClass(CountRelative.class);
		job.setNumReduceTasks(2);

		// File settings
		FileInputFormat.addInputPath(job, new Path(inputPath));
		FileOutputFormat.setOutputPath(job, new Path(baseOutput + OUTPUT_PATH));

		return job.waitForCompletion(true) ? true : false;
	}
}
