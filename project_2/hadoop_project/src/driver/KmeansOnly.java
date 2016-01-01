package driver;

/** Driver for the followers K-means problem which doesn't pass the output to the word count and co-occurrence problem. */
public class KmeansOnly {
	
	public static void main(String[] args) throws Exception {
		System.exit(driver.Kmeans.kmeansOnly(args) ? 0 : 1);
	}
}
