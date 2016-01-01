package sortcount;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

public class SortCountReducer extends Reducer<IntWritable, Text, Text, IntWritable> {
	private MultipleOutputs<Text, IntWritable> multipleOutputs;
	
	protected void setup(Context context) throws IOException, InterruptedException {
		multipleOutputs = new MultipleOutputs<Text, IntWritable>(context);
	}

	public void reduce(IntWritable count, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		for (Text word : values) {
			if (word.charAt(0) == '#') {
				multipleOutputs.write(SortCount.HASHTAGS_ONLY, word, count);
			} else if (word.charAt(0) == '@') {
				multipleOutputs.write(SortCount.AT_SIGNS, word, count);
			} else {
				multipleOutputs.write(SortCount.NO_HASHTAGS, word, count);
			}
			context.write(word, count);
		}
	}
	
	protected void cleanup(Context context) throws IOException, InterruptedException {
		multipleOutputs.close();
    }
}
