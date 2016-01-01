package sortco;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import common.Common;

public class SortCoMapper extends Mapper<Object, Text, IntWritable, Text> {
	private Text word = new Text();
	private IntWritable count = new IntWritable();
	
	public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
		StringTokenizer tokens = new StringTokenizer(value.toString(), Common.SEPARATOR);
		
		try {
			word.set(tokens.nextToken() + Common.SEPARATOR);
			word.set(word.toString() + tokens.nextToken() + Common.SEPARATOR);
			count.set(Integer.parseInt(tokens.nextToken()));
			word.set(word.toString() + tokens.nextToken());
		} catch (NoSuchElementException e) {
			return;
		}
		
		context.write(count, word);
	}
}
