package driver;

import common.Common;

/** Driver for the hashtag co-occurrence problem pairs only. */
public class Pairs {

	public static void main(String[] args) throws Exception {
		System.exit(driver.Pairs.pairs(args) ? 0 : 1);
	}
	
	/**
	 * Runs through the hashtag co-occurrence problem pairs only.
	 * 
	 * @param args - command land arguments
	 * @return true on success, otherwise false
	 */
	public static boolean pairs(String[] args) throws Exception {
		String baseOutput = Common.setBaseOutput(args);
		String inputPath = Common.setInput(args);
		
		if (!pairs.Pairs.pairs(inputPath, baseOutput)) return false;
		
		Common.cleanSuccess(baseOutput + pairs.Pairs.OUTPUT_PATH);
		
		return true;
	}
}
