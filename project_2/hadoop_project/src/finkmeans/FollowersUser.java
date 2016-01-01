package finkmeans;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

/** Contains a followers count and a username. */
public class FollowersUser implements WritableComparable<FollowersUser> {
	Long l;
	String s;

	@Override
	public void readFields(DataInput in) throws IOException {
		l = in.readLong();
		s = in.readUTF();
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeLong(l);
		out.writeUTF(s);
	}

	@Override
	public int compareTo(FollowersUser o) {
		if (l.compareTo(o.getLong()) == 0) {
			return s.compareTo(o.getText());
		} else {
			return l.compareTo(o.getLong()) * -1;
		}
	}

	/**
	 * Sets the followers count and username.
	 * 
	 * @param followers - the followers count
	 * @param user - the username
	 */
	public void set(Long followers, String user) {
		l = followers;
		s = user;
	}
	
	/**
	 * Acquires the username.
	 * 
	 * @return the text
	 */
	public String getText() {
		return s;
	}
	
	/** 
	 * Acquires the followers count.
	 * 
	 * @return the long.
	 */
	public Long getLong() {
		return l;
	}
}
