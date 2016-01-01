package common;

import java.io.IOException;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class ShortestPathReducer extends Reducer<Text, ShortestPathWritable, Text, ShortestPathWritable> {
	TreeMap<Integer, TreeMap<String, TreeSet<String>>> sortedByDistance = new TreeMap<Integer, TreeMap<String, TreeSet<String>>>();

	public void reduce(Text key, Iterable<ShortestPathWritable> values, Context context) throws IOException, InterruptedException {
		int min = ShortestPathWritable.INFINITY;
		ShortestPathWritable node = new ShortestPathWritable();
		TreeMap<String, TreeSet<String>> sortedByKey = null;
		
		for (ShortestPathWritable value : values) {
			if (value.isNode()) {
				node.setAsNode(value.getSet(), value.getDistance());
				if (node.getDistance() < min) min = node.getDistance();
			} else if (value.getDistance() < min) {
				min = value.getDistance();
			}
		}
		
		node.setDistance(min);
		
		// Set the infinity counter so that we know if we need to continue iterating
		if (min == ShortestPathWritable.INFINITY) context.getCounter(Common.Infinity.COUNT).increment(1L);
		
		// Make sure our final output will be sorted by distance first, then by lexicographic order
		if (context.getCounter(Common.Infinity.COUNT).getValue() == 0L) {
			sortedByKey = sortedByDistance.get(min);
			if (sortedByKey == null) sortedByKey = new TreeMap<String, TreeSet<String>>();
			sortedByKey.put(key.toString(), node.getSet());
			sortedByDistance.put(min, sortedByKey);
		} else {
			context.write(key, node);
		}
	}
	
	protected void cleanup(Context context) throws IOException, InterruptedException {
		Text key = new Text();
		ShortestPathWritable value = new ShortestPathWritable();
		
		while (!sortedByDistance.isEmpty()) {
			TreeMap<String, TreeSet<String>> sortedByKey = sortedByDistance.get(sortedByDistance.firstKey());
			
			while (!sortedByKey.isEmpty()) {
				key.set(sortedByKey.firstKey());
				value.setAsNode(sortedByKey.get(sortedByKey.firstKey()), sortedByDistance.firstKey());
				context.write(key, value);
				sortedByKey.remove(sortedByKey.firstKey());
			}
			
			sortedByDistance.remove(sortedByDistance.firstKey());
		}
	}
}
