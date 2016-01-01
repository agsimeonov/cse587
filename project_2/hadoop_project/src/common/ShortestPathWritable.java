package common;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.TreeSet;

import org.apache.hadoop.io.Writable;

/** Contains a node or a distance. */
public class ShortestPathWritable implements Writable {
	public static final Integer INFINITY = 1000000000;
	public static final String SEPARATOR = ":";
	private TreeSet<String> treeSet = null;
	private int distance;
	private boolean node;

	@Override
	public void readFields(DataInput in) throws IOException {
		node = in.readBoolean();
		distance = in.readInt();
		if (this.isNode()) {
			treeSet = new TreeSet<String>();
			for (int i = in.readInt(); i != 0; i = i - 1) treeSet.add(in.readUTF());
		}
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeBoolean(node);
		out.writeInt(distance);
		if (this.isNode()) {
			out.writeInt(treeSet.size());
			for (String element : treeSet) out.writeUTF(element);
		}
	}
	
	@Override
	public String toString() {
		String string = Integer.toString(distance);
		if (this.isNode()) {
			string += Common.SEPARATOR;
			for (String element : treeSet) string += element + SEPARATOR;
		} else {
			System.out.println("SDHFSKDFJKSJDFHKSDJHF");
		}
		return string;
	}
	
	/**
	 * Checks whether this instance is a node or just a distance.
	 * 
	 * @return true if this instance is a node, otherwise false
	 */
	public boolean isNode() {
		return node;
	}
	
	/**
	 * Retrieves the distance.
	 * 
	 * @return the distance
	 */
	public int getDistance() {
		return distance;
	}
	
	/**
	 * Sets the distance.
	 * 
	 * @param dist - the new distance
	 */
	public void setDistance(int dist) {
		distance = dist;
	}
	
	/**
	 * Retrieves the graph structure.
	 * 
	 * @return the set.
	 */
	public TreeSet<String> getSet() {
		return treeSet;
	}
	
	/**
	 * Sets this instance as a node.
	 * 
	 * @param set - the graph structure
	 * @param dist - the distance
	 */
	public void setAsNode(TreeSet<String> set, int dist) {
		treeSet = set;
		distance = dist;
		node = true;
	}
	
	/**
	 * Sets this instance as a distance.
	 * 
	 * @param dist - the distance
	 */
	public void setAsDistance(int dist) {
		distance = dist;
		node = false;
	}
}
