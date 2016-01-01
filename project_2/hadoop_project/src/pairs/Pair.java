package pairs;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

import common.Common;

/** Contains a pair of strings. */
public class Pair implements WritableComparable<Pair> {
	public static final String SPECIAL = "*";
	private String a;
	private String b;

	/** Initializes the pair to two empty strings. */
	public Pair() {
		a = "";
		b = "";
	}
	
	/**
	 * Initializes the pair.
	 * 
	 * @param first - one of the elements in the pair
	 * @param second - the remaining element in the pair
	 */
	public Pair(String first, String second) {
		a = first;
		b = second;
	}
	
	@Override
	public void readFields(DataInput in) throws IOException {
		a = in.readUTF();
		b = in.readUTF();
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeUTF(this.getFirst());
		out.writeUTF(this.getSecond());
	}

	@Override
	public int compareTo(Pair pair) {
		if (this.getFirst().equals(pair.getFirst())) {
			if (this.getSecond().equals(SPECIAL)) return -1;
			if (pair.getSecond().equals(SPECIAL)) return 1;
			if (this.getSecond().compareTo(pair.getSecond()) > 0) {
				return 1;
			} else if (this.getSecond().compareTo(pair.getSecond()) < 0) {
				return -1;
			} else {
				return 0;
			}
		} else {
			if (this.getFirst().compareTo(pair.getFirst()) < 0) {
				return -1;
			} else {
				return 1;
			}
		}
	}
	
	@Override
	public boolean equals(Object pair) {
		Pair p = (Pair) pair;
		if (this.getFirst().equals(p.getFirst()) && this.getSecond().equals(p.getSecond())) return true;
		return false;
	}
	
	@Override
	public int hashCode() {
		return a.hashCode() + b.hashCode();
	}
	
	@Override
	public String toString() {
		return this.getFirst() + Common.SEPARATOR + this.getSecond();
	}

	/**
	 * The first string in the pair.
	 * 
	 * @return First lexicographically ordered pair element
	 */
	public String getFirst() {
		if (a.compareTo(b) <= 0 && !a.equals(SPECIAL)) return a;
		return b;
	}
	
	/**
	 * The second string in the pair.
	 * 
	 * @return Second lexicographically ordered pair element
	 */
	public String getSecond() {
		if (a.compareTo(b) >= 0 || a.equals(SPECIAL)) return a;
		return b;
	}
	
	/**
	 * Sets the values in the pair.
	 * 
	 * @param first - one of the elements in the pair
	 * @param second - the remaining element in the pair
	 */
	public void set(String first, String second) {
		a = first;
		b = second;
	}
}
