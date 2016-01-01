package combine;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class CombineMapper extends Mapper<Object, Text, LongWritable, Text> {
	long i = 0;
	LongWritable j = new LongWritable();
	
	public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
		j.set(i);
		context.write(j, value);
		i = i + 1;
	} 
}
