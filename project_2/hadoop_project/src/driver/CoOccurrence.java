package driver;

import common.Common;

/** Driver for the hashtag co-occurrence problem. */
public class CoOccurrence {

	public static void main(String[] args) throws Exception {
		System.exit(driver.CoOccurrence.cooccurence(args) ? 0 : 1);
	}
	
	/**
	 * Runs through the hashtag co-occurrence problem.
	 * 
	 * @param args - command land arguments
	 * @return true on success, otherwise false
	 */
	public static boolean cooccurence(String[] args) throws Exception {
		String baseOutput = Common.setBaseOutput(args);
		String inputPath = Common.setInput(args);
		
		if (!pairs.Pairs.pairs(inputPath, baseOutput)) return false;
		if (!stripes.Stripes.stripes(inputPath, baseOutput)) return false;
		if (!sortco.SortCo.sortco(baseOutput + pairs.Pairs.OUTPUT_PATH, baseOutput)) return false;
		
		Common.cleanSuccess(baseOutput + pairs.Pairs.OUTPUT_PATH);
		Common.cleanSuccess(baseOutput + stripes.Stripes.OUTPUT_PATH);
		Common.cleanSuccess(baseOutput + sortco.SortCo.OUTPUT_PATH);
		
		return true;
	}
}
