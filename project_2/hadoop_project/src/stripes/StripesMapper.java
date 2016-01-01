package stripes;

import java.io.IOException;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SortedMapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import common.Common;

public class StripesMapper extends Mapper<Object, Text, Text, SortedMapWritable> {
	private static final String EMPTY = "  ";
	private HashMap<String, HashMap<String, Integer>> hashMap = new HashMap<String, HashMap<String, Integer>>();
	
	public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
		StringTokenizer tokens = new StringTokenizer(value.toString(), Common.INPUT_SEPARATOR);
		String hashtags = null;
		
		// Extract hashtags
		try {
			tokens.nextToken();
			tokens.nextToken();
			tokens.nextToken();
			hashtags = tokens.nextToken();
			tokens.nextToken();
		} catch (NoSuchElementException e) {
			return;
		}
		
		// In-mapper combining
		if (!hashtags.equals(EMPTY)) combiner(new StringTokenizer(hashtags));
	}
	
	protected void cleanup(Context context) throws IOException, InterruptedException {
		Set<String> hashtags = hashMap.keySet();
		Text writableHashtag = new Text();
		
		for (String hashtag : hashtags) {
			SortedMapWritable writableStripe = new SortedMapWritable();
			HashMap<String, Integer> stripe = hashMap.get(hashtag);
			Set<String> neighbors = stripe.keySet();
			writableHashtag.set(hashtag);
			
			for (String neighbor : neighbors) {
				writableStripe.put(new Text(neighbor), new IntWritable(stripe.get(neighbor)));
			}
			context.write(writableHashtag, writableStripe);
		}
	}
	
	/**
	 * In-mapper combiner.
	 * 
	 * @param hashtags - hashtags
	 */
	private void combiner(StringTokenizer hashtags) {
		TreeSet<String> hashtagSet = new TreeSet<String>();
		Integer sum = null;
		
		while (hashtags.hasMoreTokens()) hashtagSet.add(hashtags.nextToken());
		
		for (String hashtag = hashtagSet.first(); hashtagSet.size() > 1; hashtag = hashtagSet.first()) {
			HashMap<String, Integer> stripe = null;
			hashtagSet.remove(hashtag);
			for(String neighbor : hashtagSet) {
				if (stripe == null) {
					if ((stripe = hashMap.get(hashtag)) == null) {
						stripe = new HashMap<String, Integer>();
					}
				}
				sum = stripe.get(neighbor);
				sum = sum == null ? new Integer(1) : sum + 1;
				stripe.put(neighbor, sum);
			}	
			hashMap.put(hashtag, stripe);
		}
	}
}
