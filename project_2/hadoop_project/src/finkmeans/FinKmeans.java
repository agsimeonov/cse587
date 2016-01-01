package finkmeans;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import common.Common;

/** Provides a more useful output for K-means. */
public class FinKmeans {
	public static final String OUTPUT_PATH = "/kmeans";
	public static final String LOW = "low";
	public static final String MEDIUM = "medium";
	public static final String HIGH = "high";
	public static final String USERS_LOW = "userslow";
	public static final String USERS_MEDIUM = "usersmedium";
	public static final String USERS_HIGH = "usershigh";
	
	/** Provides a more useful output for K-means. 
	 * 
	 * @param inputPath - the input path
	 * @param baseOutput - the root directory for the output
	 * @return true on success, otherwise false
	 */
	public static boolean finkmeans(String inputPath, String baseOutput) throws Exception {
		Configuration conf = Common.setupConf();
		
		// Delete old output
		Common.deleteOld(conf, baseOutput + OUTPUT_PATH);

		// Job settings
		Job job = Job.getInstance(conf);
		job.setJarByClass(FinKmeans.class);
		job.setMapperClass(FinKmeansMapper.class);
		job.setReducerClass(FinKmeansReducer.class);
		job.setMapOutputKeyClass(FollowersUser.class);
		job.setOutputKeyClass(NullWritable.class);
		job.setOutputValueClass(Text.class);

		// File settings
		FileInputFormat.addInputPath(job, new Path(inputPath));
		FileOutputFormat.setOutputPath(job, new Path(baseOutput + OUTPUT_PATH));
		MultipleOutputs.addNamedOutput(job, LOW, TextOutputFormat.class, Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job, MEDIUM, TextOutputFormat.class, Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job, HIGH, TextOutputFormat.class, Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job, USERS_LOW, TextOutputFormat.class, Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job, USERS_MEDIUM, TextOutputFormat.class, Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job, USERS_HIGH, TextOutputFormat.class, Text.class, Text.class);


		return job.waitForCompletion(true) ? true : false;
	}
}
