package kmeans;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;

import common.Common;

/** K-means for number of followers. */
public class Kmeans {
	public static final String OUTPUT_PATH = "/kmeans";
	
	/**
	 * K-means for number of followers.
	 * 
	 * @param inputPath - the input path
	 * @param baseOutput - the root directory for the output
	 * @return The Counters for the job on success, otherwise null
	 */
	public static Counters kmeans(String inputPath, String baseOutput) throws Exception {
		Configuration conf = Common.setupConf();

		// Delete old output
		Common.deleteOld(conf, baseOutput + OUTPUT_PATH);
		
		// Job settings
		Job job = Job.getInstance(conf);
		job.setJarByClass(Kmeans.class);
		job.setMapperClass(KmeansMapper.class);
		job.setReducerClass(KmeansReducer.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(RecordsAveragePair.class);
		job.setOutputFormatClass(NullOutputFormat.class);
		// Single reducer as per algorithm description

		// File settings
		FileInputFormat.addInputPath(job, new Path(inputPath));
		FileOutputFormat.setOutputPath(job, new Path(baseOutput + OUTPUT_PATH));

		return job.waitForCompletion(true) ? job.getCounters() : null;
	}
}
