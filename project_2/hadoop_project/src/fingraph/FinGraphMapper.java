package fingraph;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import common.Common;
import common.ShortestPathWritable;

public class FinGraphMapper extends Mapper<Object, Text, Text, ShortestPathWritable> {
	Text outKey = new Text();
	ShortestPathWritable outValue = new ShortestPathWritable();
	
	public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
		StringTokenizer tokens = new StringTokenizer(value.toString(), Common.SEPARATOR);
		StringTokenizer adjacencyTokens = null;
		TreeSet<String> adjacency = new TreeSet<String>();
		String username = null;
		String relations = null;
		int distance;
		
		try {
			username = tokens.nextToken();
			distance = Integer.parseInt(tokens.nextToken());
			relations = tokens.nextToken();
		} catch (NoSuchElementException e) {
			return;
		}
		
		// Ignore dangling nodes (for not-fully connected graphs)
		if (distance == ShortestPathWritable.INFINITY) return;
		
		// Set up the adjacency list
		adjacencyTokens = new StringTokenizer(relations, ShortestPathWritable.SEPARATOR);
		while (adjacencyTokens.hasMoreTokens()) adjacency.add(adjacencyTokens.nextToken());
		
		// Pass along the graph structure
		outKey.set(username);
		outValue.setAsNode(adjacency, distance);
		context.write(outKey, outValue);
	}
}
