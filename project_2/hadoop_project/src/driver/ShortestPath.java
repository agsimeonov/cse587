package driver;

import org.apache.hadoop.mapreduce.Counters;

import common.Common;

/** Driver for the shortest path problem */
public class ShortestPath {
	
	public static void main(String[] args) throws Exception {
		System.exit(driver.ShortestPath.shortestPath(args) ? 0 : 1);
	}

	/**
	 * Runs through the shortest path problem.
	 * 
	 * @param args - command land arguments
	 * @return true on success, otherwise false
	 */
	public static boolean shortestPath(String[] args) throws Exception {
		String inputPath = Common.setInput(args);
		String baseOutput = Common.setBaseOutput(args);
		Integer i;
		
		if (!combine.Combine.combine(inputPath, baseOutput)) return false;
		
		// Initialize the graph
		Common.setGraphRoot("cnnbrk");
		Counters counters = initgraph.InitGraph.initGraph(baseOutput + combine.Combine.OUTPUT_PATH, Common.SPATH_OUTPUT);
		if (counters == null) return false;
		long count = counters.findCounter(Common.Infinity.COUNT).getValue();
		
		// Iterate through shortest path
		for (i = 1; counters.findCounter(Common.Infinity.COUNT).getValue() != 0L; i = i + 1) {
			inputPath = Common.SPATH_OUTPUT + shortestpath.ShortestPath.OUTPUT_PATH + (i - 1);
			counters = shortestpath.ShortestPath.shortestPath(inputPath, Common.SPATH_OUTPUT, i);
			if (counters == null) return false;
			Common.deleteOld(Common.setupConf(), inputPath);
			
			// If the graph is not connected we are done
			if (count == counters.findCounter(Common.Infinity.COUNT).getValue()) {
				i = i + 1;
				break;
			}
			
			count = counters.findCounter(Common.Infinity.COUNT).getValue();
		}
		
		// Finalize the graph
		inputPath = Common.SPATH_OUTPUT + shortestpath.ShortestPath.OUTPUT_PATH + (i - 1);
		if (!fingraph.FinGraph.finGraph(inputPath, baseOutput)) return false;
		Common.deleteOld(Common.setupConf(), Common.SPATH_OUTPUT);
		Common.cleanSuccess(baseOutput + Common.SPATH_OUTPUT);
		Common.deleteOld(Common.setupConf(), baseOutput + combine.Combine.OUTPUT_PATH);
		
		return true;
	}
}
