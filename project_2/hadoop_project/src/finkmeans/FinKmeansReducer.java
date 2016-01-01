package finkmeans;

import java.io.IOException;
import java.util.HashSet;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

import common.Common;

public class FinKmeansReducer extends Reducer<FollowersUser, Text, NullWritable, Text> {
	private long[] centroids = null;
	private HashSet<String> usernames = new HashSet<String>();
	private MultipleOutputs<NullWritable, Text> multipleOutputs;
	private Text out = new Text();
	
	protected void setup(Context context) throws IOException, InterruptedException {
		multipleOutputs = new MultipleOutputs<NullWritable, Text>(context);
	}

	public void reduce(FollowersUser followersUserKey, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		for (Text value : values) {
			String username = followersUserKey.getText();;
			Long followers = followersUserKey.getLong();
			
			// Initialize with cluster centers from previous iteration
			if (centroids == null) centroids = Common.getCentroids();
			
			// Emit with appropriate labels
			long distanceLow = Math.abs(centroids[1] - followers);
			long distanceMedium = Math.abs(centroids[2] - followers);
			long distanceHigh = Math.abs(centroids[3] - followers);

			if (distanceLow == distanceMedium) {
				multipleOutputs.write(FinKmeans.LOW, null, value);
				out.set(username + Common.SEPARATOR + Common.Centroids.LOW.toString() + Common.SEPARATOR + followers.toString());
				if (!usernames.contains(username)) multipleOutputs.write(FinKmeans.USERS_LOW, null, out);
			} else if (distanceMedium == distanceHigh) {
				multipleOutputs.write(FinKmeans.MEDIUM, null, value);
				out.set(username + Common.SEPARATOR + Common.Centroids.MEDIUM.toString() + Common.SEPARATOR + followers.toString());
				if (!usernames.contains(username)) multipleOutputs.write(FinKmeans.USERS_MEDIUM, null, out);
			} else if (distanceLow < distanceHigh) {
				// Lower half
				if (distanceLow < distanceMedium) {
					multipleOutputs.write(FinKmeans.LOW, null, value);
					out.set(username + Common.SEPARATOR + Common.Centroids.LOW.toString() + Common.SEPARATOR + followers.toString());
					if (!usernames.contains(username)) multipleOutputs.write(FinKmeans.USERS_LOW, null, out);
				} else {
					multipleOutputs.write(FinKmeans.MEDIUM, null, value);
					out.set(username + Common.SEPARATOR + Common.Centroids.MEDIUM.toString() + Common.SEPARATOR + followers.toString());
					if (!usernames.contains(username)) multipleOutputs.write(FinKmeans.USERS_MEDIUM, null, out);
				}
			} else {
				// Upper half
				if (distanceMedium < distanceHigh) {
					multipleOutputs.write(FinKmeans.MEDIUM, null, value);
					out.set(username + Common.SEPARATOR + Common.Centroids.MEDIUM.toString() + Common.SEPARATOR + followers.toString());
					if (!usernames.contains(username)) multipleOutputs.write(FinKmeans.USERS_MEDIUM, null, out);
				} else {
					multipleOutputs.write(FinKmeans.HIGH, null, value);
					out.set(username + Common.SEPARATOR + Common.Centroids.HIGH.toString() + Common.SEPARATOR + followers.toString());
					if (!usernames.contains(username)) multipleOutputs.write(FinKmeans.USERS_HIGH, null, out);
				}
			}

			// Make sure we count each user only once
			if (!usernames.contains(username)) {
				context.write(null, out);
				usernames.add(username);
			}
		}
	}
	
	protected void cleanup(Context context) throws IOException, InterruptedException {
		multipleOutputs.close();
    }
}
