package kmeans;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

/** Contains a total number of records and an average used during K-means. */
public class RecordsAveragePair implements Writable {
	private long r;
	private long a;
	
	@Override
	public void readFields(DataInput in) throws IOException {
		r = in.readLong();
		a = in.readLong();
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeLong(r);
		out.writeLong(a);
	}
	
	/**
	 * Sets the records and average.
	 * 
	 * @param records - the total number of records in a cluster
	 * @param average - the average values of coordinates in a cluster
	 */
	public void set(long records, long average) {
		r = records;
		a = average;
	}
	
	/**
	 * Acquires the total number of records for the pair.
	 * 
	 * @return the total number of records
	 */
	public long getRecords() {
		return r;
	}
	
	/**
	 * Acquires the average for the pair.
	 * 
	 * @return the average values of coordinates in a cluster
	 */
	public long getAverage() {
		return a;
	}
}
