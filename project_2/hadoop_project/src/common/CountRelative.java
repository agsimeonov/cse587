package common;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.text.DecimalFormat;

import org.apache.hadoop.io.Writable;

/** Contains count and relative frequency. */
public class CountRelative implements Writable {
	Integer i;
	Double d;
	
	/** Initializes the count and relative frequency to 0. */
	public CountRelative() {
		i = new Integer(0);
		d = new Double(0.0);
	}

	@Override
	public String toString() {
		DecimalFormat decimalFormat = new DecimalFormat("#.#####");	
		return i.toString() + Common.SEPARATOR + decimalFormat.format(d);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		i = in.readInt();
		d = in.readDouble();
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeInt(i);
		out.writeDouble(d);
	}
	
	/**
	 * Sets the count and relative frequency.
	 * 
	 * @param count - the count
	 * @param relativeFrequency - the relative frequency
	 */
	public void set(Integer count, Double relativeFrequency) {
		i = count;
		d = relativeFrequency;
	}
}
