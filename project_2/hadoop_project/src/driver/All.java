package driver;

/** Driver for the whole project */
public class All {

	public static void main(String[] args) throws Exception {
		if (!driver.WordCount.wordcount(args)) System.exit(1);
		if (!driver.CoOccurrence.cooccurence(args)) System.exit(1);
		if (!driver.Kmeans.kmeans(args)) System.exit(1);
		if (!driver.ShortestPath.shortestPath(args)) System.exit(1);
		
		System.exit(0);
	}
}
