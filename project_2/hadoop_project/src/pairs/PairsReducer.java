package pairs;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Reducer;

import common.CountRelative;

public class PairsReducer extends Reducer<Pair, IntWritable, Pair, CountRelative> {
	private CountRelative countRelative = new CountRelative();
	private String first = null;
	private Integer marginalSum = 0;

	public void reduce(Pair pair, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
		Integer sum = 0;
		
		if (!pair.getFirst().equals(first)) {
			first = pair.getFirst();
			marginalSum = 0;
			for (IntWritable value : values) marginalSum += value.get();
		} else if (pair.getSecond().equals(Pair.SPECIAL)) {
			for (IntWritable value : values) marginalSum += value.get();
		} else {
			for (IntWritable value : values) sum += value.get();
			countRelative.set(sum, sum.doubleValue() / marginalSum.doubleValue());
			context.write(pair, countRelative);
		}
	}
}
