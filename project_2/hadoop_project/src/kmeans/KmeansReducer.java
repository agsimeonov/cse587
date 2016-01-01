package kmeans;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import common.Common;

public class KmeansReducer extends Reducer<Text, RecordsAveragePair, Object, Object> {

	public void reduce(Text clusterKey, Iterable<RecordsAveragePair> pairs, Context context) throws IOException, InterruptedException {
		long totals = 0;
		long records = 0;
		
		for (RecordsAveragePair pair : pairs) {
			totals += pair.getAverage() * pair.getRecords();
			records += pair.getRecords();
		}
		
		if (clusterKey.toString().equals(Common.Centroids.LOW.toString())) {
			context.getCounter(Common.Centroids.LOW).setValue(totals / records);
			context.getCounter(Common.Centroids.COUNT_LOW).setValue(records);
		} else if (clusterKey.toString().equals(Common.Centroids.MEDIUM.toString())) {
			context.getCounter(Common.Centroids.MEDIUM).setValue(totals / records);
			context.getCounter(Common.Centroids.COUNT_MEDIUM).setValue(records);
		} else {
			context.getCounter(Common.Centroids.HIGH).setValue(totals / records);
			context.getCounter(Common.Centroids.COUNT_HIGH).setValue(records);
		}
	}
	
	protected void cleanup(Context context) throws IOException, InterruptedException {
		context.getCounter(Common.Centroids.ITERATION).increment(1L);
    }
}
