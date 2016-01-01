package pairs;

import java.io.IOException;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import common.Common;

public class PairsMapper extends Mapper<Object, Text, Pair, IntWritable> {
	private HashMap<Pair, Integer> hashMap = new HashMap<Pair, Integer>();
	
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
		if (!hashtags.equals(Common.EMPTY)) combiner(new StringTokenizer(hashtags));
	}
	
	protected void cleanup(Context context) throws IOException, InterruptedException {
		Set<Pair> pairs = hashMap.keySet();
		Pair pair = new Pair();
		IntWritable sum = new IntWritable();
		
		for (Pair element : pairs) {
			pair.set(element.getFirst(), element.getSecond());
			sum.set(hashMap.get(element));
			context.write(pair, sum);
		}
	}
	
	/**
	 * In-mapper combiner.
	 * 
	 * @param hashtags - hashtags
	 */
	private void combiner(StringTokenizer hashtags) {
		TreeSet<String> hashtagSet = new TreeSet<String>();
		Pair pair = null;
		Pair marginal = null;
		Integer sum = null;
		Integer marginalSum = null;
		
		while (hashtags.hasMoreTokens()) hashtagSet.add(hashtags.nextToken());
		
		for (String hashtag = hashtagSet.first(); hashtagSet.size() > 1; hashtag = hashtagSet.first()) {
			marginal = new Pair(hashtag, Pair.SPECIAL);
			marginalSum = hashMap.get(marginal);
			marginalSum = marginalSum == null ? new Integer(0) : marginalSum;
			hashtagSet.remove(hashtag);
			for(String neighbor : hashtagSet) {
				marginalSum = marginalSum + 1;
				pair = new Pair(hashtag, neighbor);
				sum = hashMap.get(pair);
				sum = sum == null ? new Integer(1) : sum + 1;
				hashMap.put(pair, sum);
			}
			hashMap.put(marginal, marginalSum);
		}
	}
}
