package finkmeans;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import common.Common;

public class FinKmeansMapper extends Mapper<Object, Text, FollowersUser, Text> {
	//0 for high, 1 for medium, 2 for low (used for ordering)
	FollowersUser followersUserOut = new FollowersUser();
	
	public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
		StringTokenizer tokens = new StringTokenizer(value.toString(), Common.INPUT_SEPARATOR);
		Long followers = Long.MIN_VALUE;
		String username = null;
		
		// Extract followers
		try {
			username = tokens.nextToken().replaceAll("\\s", "");
			followers = new Long(Integer.parseInt(tokens.nextToken().replaceAll("\\s", "")));
			tokens.nextToken();
			tokens.nextToken();
			tokens.nextToken();
		} catch (NoSuchElementException e) {
			return;
		} catch (NumberFormatException e) {
			return;
		}
		
		if (followers != Long.MIN_VALUE) {
			followersUserOut.set(followers, username);;
			context.write(followersUserOut, value);
		}
	}
}
