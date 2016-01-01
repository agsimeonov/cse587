package sortcount;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import common.Common;
import common.DescendingComparator;

/** Sorts the word count output. */
public class SortCount {
	public static final String OUTPUT_PATH = "/wordcount/sorted";
	public static final String HASHTAGS_ONLY = "hashtags";
	public static final String NO_HASHTAGS = "words";
	public static final String AT_SIGNS = "atsign";
	
	/** Sorts the word count output. 
	 * 
	 * @param inputPath - the input path
	 * @param baseOutput - the root directory for the output
	 * @return true on success, otherwise false
	 */
	public static boolean sortcount(String inputPath, String baseOutput) throws Exception {
		Configuration conf = Common.setupConf();
		
		// Delete old output
		Common.deleteOld(conf, baseOutput + OUTPUT_PATH);

		// Job settings
		Job job = Job.getInstance(conf);
		job.setJarByClass(SortCount.class);
		job.setMapperClass(SortCountMapper.class);
		job.setSortComparatorClass(DescendingComparator.class);
		job.setReducerClass(SortCountReducer.class);
		job.setMapOutputKeyClass(IntWritable.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);

		// File settings
		FileInputFormat.addInputPath(job, new Path(inputPath));
		FileOutputFormat.setOutputPath(job, new Path(baseOutput + OUTPUT_PATH));
		MultipleOutputs.addNamedOutput(job, HASHTAGS_ONLY, TextOutputFormat.class, Text.class, IntWritable.class);
		MultipleOutputs.addNamedOutput(job, NO_HASHTAGS, TextOutputFormat.class, Text.class, IntWritable.class);
		MultipleOutputs.addNamedOutput(job, AT_SIGNS, TextOutputFormat.class, Text.class, IntWritable.class);

		return job.waitForCompletion(true) ? true : false;
	}
}
