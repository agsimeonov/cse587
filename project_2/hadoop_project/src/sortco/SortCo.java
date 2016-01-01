package sortco;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import common.Common;
import common.DescendingComparator;

/** Sorts hashtag co-occurrence output. */
public class SortCo {
	public static final String OUTPUT_PATH = "/coocurrence/sorted";

	/**
	 * Sorts hashtag co-occurrence output.
	 * 
	 * @param inputPath - the input path
	 * @param baseOutput - the root directory for the output
	 * @return true on success, otherwise false
	 */
	public static boolean sortco(String inputPath, String baseOutput) throws Exception {
		Configuration conf = Common.setupConf();
		
		// Delete old output
		Common.deleteOld(conf, baseOutput + OUTPUT_PATH);

		// Job settings
		Job job = Job.getInstance(conf);
		job.setJarByClass(SortCo.class);
		job.setMapperClass(SortCoMapper.class);
		job.setSortComparatorClass(DescendingComparator.class);
		job.setReducerClass(SortCoReducer.class);
		job.setMapOutputKeyClass(IntWritable.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		// File settings
		FileInputFormat.addInputPath(job, new Path(inputPath));
		FileOutputFormat.setOutputPath(job, new Path(baseOutput + OUTPUT_PATH));

		return job.waitForCompletion(true) ? true : false;
	}
}
