package initgraph;

import java.io.IOException;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import common.Common;
import common.ShortestPathWritable;

public class InitGraphMapper extends Mapper<Object, Text, Text, ShortestPathWritable> {
	HashMap<String, TreeSet<String>> connections = new HashMap<String, TreeSet<String>>();
	String graphRoot = null;

	public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
		StringTokenizer tokens = new StringTokenizer(value.toString(), Common.INPUT_SEPARATOR);
		String username = null;
		String relations = null;
		
		if (graphRoot == null) graphRoot = Common.getGraphRoot();
		
		// Extract usersnames and relations
		try {
			username = tokens.nextToken().replaceAll("\\s", "");
			tokens.nextToken();
			relations = tokens.nextToken();
			tokens.nextToken();
			tokens.nextToken();
		} catch (NoSuchElementException e) {
			return;
		}
		
		// In-mapper combining
		if (!relations.equals(Common.EMPTY)) combiner(username, new StringTokenizer(relations));
	}
	
	protected void cleanup(Context context) throws IOException, InterruptedException {
		Text key = new Text();
		ShortestPathWritable value = new ShortestPathWritable();
		Set<String> usernames = connections.keySet();
		
		for (String username : usernames) {
			key.set(username);
			int distance = ShortestPathWritable.INFINITY;
			if (username.equals(graphRoot)) {
				distance = 0;
				value.setAsDistance(distance);
				context.write(key, value);
			}
			value.setAsNode(connections.get(username), distance);
			context.write(key, value);
		}
	}
	
	/**
	 * In-mapper combiner.
	 * 
	 * @param username - the username extracted from the input file
	 * @param relations - the relations extracted from the input file
	 */
	private void combiner(String username, StringTokenizer relations) {
		TreeSet<String> set = connections.get(username);
		if (set == null) set = new TreeSet<String>();
		TreeSet<String> reverseSet = null;
		String relation = null;
		
		while (relations.hasMoreTokens()) {
			relation = relations.nextToken();
			set.add(relation);
			reverseSet = connections.get(relation);
			if (reverseSet == null) reverseSet = new TreeSet<String>();
			reverseSet.add(username);
			connections.put(relation, reverseSet);
		}
		connections.put(username, set);
	}
}
