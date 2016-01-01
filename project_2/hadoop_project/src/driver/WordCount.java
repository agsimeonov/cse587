package driver;

import common.Common;

/** Driver for the word count problem. */
public class WordCount {

	public static void main(String[] args) throws Exception {
		System.exit(driver.WordCount.wordcount(args) ? 0 : 1);
	}
	
	/**
	 * Runs through the word count problem.
	 * 
	 * @param args - command land arguments
	 * @return true on success, otherwise false
	 */
	public static boolean wordcount(String[] args) throws Exception {
		String baseOutput = Common.setBaseOutput(args);
		
		if (!wordcount.WordCount.wordcount(Common.setInput(args), baseOutput)) return false;
		if (!sortcount.SortCount.sortcount(baseOutput + wordcount.WordCount.OUTPUT_PATH, baseOutput)) return false;
		
		Common.cleanSuccess(baseOutput + wordcount.WordCount.OUTPUT_PATH);
		Common.cleanSuccess(baseOutput + sortcount.SortCount.OUTPUT_PATH);
		
		return true;
	}
}
