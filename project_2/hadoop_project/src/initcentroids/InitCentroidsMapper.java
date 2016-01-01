package initcentroids;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import common.Common;

public class InitCentroidsMapper extends Mapper<Object, Text, Object, Object> {
	private TreeSet<Integer> followersSet = new TreeSet<Integer>();
	
	public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
		StringTokenizer tokens = new StringTokenizer(value.toString(), Common.INPUT_SEPARATOR);
		Integer followers = null;
		
		// Extract followers
		try {
			tokens.nextToken();
			followers = new Integer(Integer.parseInt(tokens.nextToken().replaceAll("\\s", "")));
			tokens.nextToken();
			tokens.nextToken();
			tokens.nextToken();
		} catch (NoSuchElementException e) {
			return;
		} catch (NumberFormatException e) {
			return;
		}
		
		followersSet.add(followers);
	}
	
	protected void cleanup(Context context) throws IOException, InterruptedException {
		if (followersSet.size() >= 3) {
			int i = 0;
			int lowIndex = followersSet.size() / 6;
			int middleIndex = lowIndex == 0 ? 1 : lowIndex * 3;
			int highIndex = lowIndex == 0 ? 2 : lowIndex * 5;
			
			// Assign each centroid to relatively equidistant points in the followers set
			for (Integer followers : followersSet) {
				if (i == lowIndex) context.getCounter(Common.Centroids.LOW).setValue(followers);
				if (i == middleIndex) context.getCounter(Common.Centroids.MEDIUM).setValue(followers);
				if (i == highIndex) context.getCounter(Common.Centroids.HIGH).setValue(followers);
				i = i + 1;
			}
		}
	}
}
