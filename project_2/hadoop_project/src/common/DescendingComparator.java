package common;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class DescendingComparator extends WritableComparator {
	
	protected DescendingComparator() {
		super(IntWritable.class, true);
    }
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public int compare(WritableComparable a, WritableComparable b) {
		return -1 * a.compareTo(b);
    }
}
