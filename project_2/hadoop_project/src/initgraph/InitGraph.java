package initgraph;

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

/** Initializes the shortest path graph. */
public class InitGraph {
	public static final String OUTPUT_PATH = Common.SPATH_OUTPUT + "/0";

	/**
	 * Initializes the shortest path graph.
	 * 
	 * @param inputPath - the input path
	 * @param baseOutput - the root directory for the output
	 * @return The Counters for the job on success, otherwise null
	 */
	public static Counters initGraph(String inputPath, String baseOutput) throws Exception {
		Configuration conf = Common.setupConf();
		
		// Delete old output
		Common.deleteOld(conf, baseOutput + OUTPUT_PATH);
				
		// Job settings
		Job job = Job.getInstance(conf);
		job.setJarByClass(InitGraph.class);
		job.setMapperClass(InitGraphMapper.class);
		job.setReducerClass(ShortestPathReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(ShortestPathWritable.class);
		
		// File settings
		FileInputFormat.addInputPath(job, new Path(inputPath));
		FileOutputFormat.setOutputPath(job, new Path(baseOutput + OUTPUT_PATH));
		
		return job.waitForCompletion(true) ? job.getCounters() : null;
	}
}
