package fingraph;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import common.Common;
import common.ShortestPathReducer;
import common.ShortestPathWritable;

/** Finalizes the shortest path graph */
public class FinGraph {

	/**
	 * Finalizes the shortest path graph.
	 * 
	 * @param inputPath - the input path
	 * @param baseOutput - the root directory for the output
	 * @return true on success, otherwise false
	 * @throws Exception
	 */
	public static boolean finGraph(String inputPath, String baseOutput) throws Exception {
		String outputPath = baseOutput + Common.SPATH_OUTPUT;
		Configuration conf = Common.setupConf();
		
		// Delete old output
		Common.deleteOld(conf, outputPath);
				
		// Job settings
		Job job = Job.getInstance(conf);
		job.setJarByClass(FinGraph.class);
		job.setMapperClass(FinGraphMapper.class);
		job.setReducerClass(ShortestPathReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(ShortestPathWritable.class);
		
		// File settings
		FileInputFormat.addInputPath(job, new Path(inputPath));
		FileOutputFormat.setOutputPath(job, new Path(outputPath));
		
		return job.waitForCompletion(true) ? true : false;
	}

}
