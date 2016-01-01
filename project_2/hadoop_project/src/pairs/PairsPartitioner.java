package pairs;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Partitioner;

public class PairsPartitioner extends Partitioner<Pair, IntWritable> {

	@Override
	public int getPartition(Pair pair, IntWritable count, int numPartitions) {
		return Math.abs(pair.getFirst().hashCode()) % numPartitions;
	}
}
