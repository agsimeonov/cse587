package stripes;

import java.io.IOException;
import java.util.TreeMap;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SortedMapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import common.Common;
import common.CountRelative;

public class StripesReducer extends Reducer<Text, SortedMapWritable, Text, CountRelative> {
	private CountRelative countRelative = new CountRelative();
	private Text writableNeighbor = new Text();

	public void reduce(Text hashtag, Iterable<SortedMapWritable> stripes, Context context) throws IOException, InterruptedException {
		TreeMap<String, Integer> stripe = new TreeMap<String, Integer>();
		Integer marginalSum = new Integer(0);
		
		// Combine the stripes into a single stripe
		for (SortedMapWritable element : stripes) {
			while (element.size() != 0) {
				Text neighbor = (Text) element.firstKey();
				IntWritable elementSum = (IntWritable) element.get(neighbor);
				Integer sum = stripe.get(neighbor.toString());
				sum = sum == null ? elementSum.get() : sum + elementSum.get();
				marginalSum += elementSum.get();
				stripe.put(neighbor.toString(), sum);
				element.remove(neighbor);
			}
		}
		
		// Emit the stripe
		while (stripe.size() != 0) {
			String neighbor = stripe.firstKey();
			Integer sum = stripe.get(neighbor);
			countRelative.set(sum, sum.doubleValue() / marginalSum.doubleValue());
			writableNeighbor.set(hashtag.toString() + Common.SEPARATOR + neighbor);
			context.write(writableNeighbor, countRelative);
			stripe.remove(neighbor);
		}
	}
}
