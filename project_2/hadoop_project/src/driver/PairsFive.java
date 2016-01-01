package driver;

import common.Common;

/** Driver for the hashtag co-occurrence problem pairs only with 5 reducers. */
public class PairsFive {

	public static void main(String[] args) throws Exception {
		System.exit(driver.PairsFive.pairsFive(args) ? 0 : 1);
	}
	
	/**
	 * Runs through the hashtag co-occurrence problem pairs only with 5 reducers.
	 * 
	 * @param args - command land arguments
	 * @return true on success, otherwise false
	 */
	public static boolean pairsFive(String[] args) throws Exception {
		String baseOutput = Common.setBaseOutput(args);
		String inputPath = Common.setInput(args);
		
		if (!pairs.Pairs.pairsFive(inputPath, baseOutput)) return false;
		
		Common.cleanSuccess(baseOutput + pairs.Pairs.OUTPUT_PATH + "_five");
		
		return true;
	}
}
