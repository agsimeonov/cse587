package driver;

import common.Common;

/** Driver for the hashtag co-occurrence problem stripes only. */
public class Stripes {

	public static void main(String[] args) throws Exception {
		System.exit(driver.Stripes.stripes(args) ? 0 : 1);
	}
	
	/**
	 * Runs through the hashtag co-occurrence problem stripes only.
	 * 
	 * @param args - command land arguments
	 * @return true on success, otherwise false
	 */
	public static boolean stripes(String[] args) throws Exception {
		String baseOutput = Common.setBaseOutput(args);
		String inputPath = Common.setInput(args);
		
		if (!stripes.Stripes.stripes(inputPath, baseOutput)) return false;
		
		Common.cleanSuccess(baseOutput + stripes.Stripes.OUTPUT_PATH);
		
		return true;
	}
}
