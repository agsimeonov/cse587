package combine;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class CombineReducer extends Reducer<LongWritable, Text, NullWritable, Text> {
	
	public void reduce(LongWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		for (Text value : values) context.write(NullWritable.get(), value);
	}
}
