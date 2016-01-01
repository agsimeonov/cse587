package wordcount;

import java.io.IOException;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import common.Common;

public class WordCountMapper extends Mapper<Object, Text, Text, IntWritable> {
	private HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
	
	public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
		StringTokenizer tokens = new StringTokenizer(value.toString(), Common.INPUT_SEPARATOR);
		String hashtags = null;
		String tweet = null;
		String mentions = null;
		
		// Extract hashtags and tweet
		try {
			tokens.nextToken();
			tokens.nextToken();
			mentions = tokens.nextToken();
			hashtags = tokens.nextToken();
			tweet = tokens.nextToken();
		} catch (NoSuchElementException e) {
			return;
		}
		
		// In-mapper combining
		if (!hashtags.equals(Common.EMPTY)) combiner(new StringTokenizer(hashtags), false);
		if (!tweet.equals(Common.EMPTY)) combiner(new StringTokenizer(tweet), false);
		if (!mentions.equals(Common.EMPTY)) combiner(new StringTokenizer(mentions), true);
	}
	
	protected void cleanup(Context context) throws IOException, InterruptedException {
		Set<String> words = hashMap.keySet();
		Text word = new Text();
		IntWritable sum = new IntWritable();
		
		for (String element : words) {
			word.set(element);
			sum.set(hashMap.get(element));
			context.write(word, sum);
		}
	}
	
	/**
	 * In-mapper combiner.
	 * 
	 * @param tokens - hashtags or words in a tweet
	 * @param isMentiones - true if tokens derived from mentions, otherwise false
	 */
	private void combiner(StringTokenizer tokens, boolean isMentions) {
		Integer sum = null;
		String word = null;
		
		while (tokens.hasMoreTokens()) {
			word = tokens.nextToken();
			sum = hashMap.get(word);
			sum = sum == null ? new Integer(1) : sum + 1;
			if (isMentions) {
				hashMap.put("@" + word, sum);
			} else {
				hashMap.put(word, sum);
			}
		}
	}
}
