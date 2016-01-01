package sortco;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import common.Common;

public class SortCoReducer extends Reducer<IntWritable, Text, Text, Text> {
	private Text writableWords = new Text();
	private Text writableNumbers = new Text();
	
	public void reduce(IntWritable count, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		
		for (Text word : values) {
			StringTokenizer tokens = new StringTokenizer(word.toString(), Common.SEPARATOR);
			String words = null;
			String numbers = null;
			
			try {
				words = tokens.nextToken() + Common.SEPARATOR;
				words += tokens.nextToken();
				numbers = count.toString() + Common.SEPARATOR;
				numbers += tokens.nextToken();
			} catch (NoSuchElementException e) {
				continue;
			}
			
			writableWords.set(words);
			writableNumbers.set(numbers);
			context.write(writableWords, writableNumbers);
		}
	}
}
