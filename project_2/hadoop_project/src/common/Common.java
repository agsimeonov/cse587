package common;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Counters;

/** Common variables and functions. */
public class Common {
	public static final String DEFAULT_INPUT = "/input";
	public static final String BASE_OUTPUT = "/output";
	public static final String SPATH_OUTPUT = "/spath";
	public static final String SPATH_ROOTFILE = "/root.txt";
	public static final String INPUT_SEPARATOR = "|";
	public static final String SEPARATOR = ",";
	public static final String EMPTY = "  ";
	public static final String SUCCESS_FILE = "/_SUCCESS";
	public static final String CENTROID_FILE = "/centroids.csv";
	public static final String TEMP_FILE = "/temp.txt";
	public static final long INVALID_CENTROID = Long.MIN_VALUE;
	
	public static enum Centroids {
		ITERATION, LOW, MEDIUM, HIGH, COUNT_LOW, COUNT_MEDIUM, COUNT_HIGH
	}
	
	public enum Infinity {
		COUNT
	}
	
	/**
	 * Sets up the input path.
	 * 
	 * @param args - command land arguments
	 * @return args[0], defaults to {@value #DEFAULT_INPUT}
	 */
	public static String setInput(String[] args) {
		return args.length > 0 ? args[0] : DEFAULT_INPUT;
	}
	
	/**
	 * Sets up the root output directory.
	 * 
	 * @param args - command land arguments
	 * @return args[1], defaults to {@value #BASE_OUTPUT}
	 */
	public static String setBaseOutput(String[] args) {
		return args.length > 1 ? args[1] : BASE_OUTPUT;
	}
	
	/**
	 * Sets up the default Configuration.
	 * 
	 * @return The default Configuration
	 */
	public static Configuration setupConf() {
		Configuration conf = new Configuration();

		// Get HDFS configuration parameters from Hadoop XML files
		conf.addResource(new Path("/Applications/hadoop-2.2.0/etc/hadoop/core-site.xml"));
		conf.addResource(new Path("/Applications/hadoop-2.2.0/etc/hadoop/hdfs-site.xml"));
		
		// Output a CSV file
		conf.set("mapreduce.output.textoutputformat.separator", SEPARATOR);
		
		return conf;
	}
	
	/**
	 * Deletes old output.
	 * 
	 * @param conf - the Configuration used
	 * @param outputPath - the output path
	 */
	public static void deleteOld(Configuration conf, String outputPath) throws Exception {
		FileSystem fs = FileSystem.get(conf);
		Path path = new Path(outputPath);
		if (fs.exists(path)) fs.delete(path, true);	
	}
	
	/**
	 * Deletes file from the output path.
	 * 
	 * @param file - the file name
	 * @param outputPath - the output path
	 */
	public static void deleteFile(String file, String outputPath) throws Exception {
		Configuration conf = setupConf();
		FileSystem fs = FileSystem.get(conf);

		if (fs.exists(new Path(outputPath))) {
			fs.delete(new Path(outputPath + file), false);
		}
	}
	
	/**
	 * Deletes {@value #SUCCESS_FILE} from the output path.
	 * 
	 * @param outputPath - the output path
	 */
	public static void cleanSuccess(String outputPath) throws Exception {
		deleteFile(SUCCESS_FILE, outputPath);
	}
	
	/**
	 * Sets the graph root username.
	 * 
	 * @param username - the graph root username
	 * @throws Exception
	 */
	public static void setGraphRoot(String username) throws Exception {
		Configuration conf = setupConf();
		FileSystem fs = FileSystem.get(conf);
		Path path = new Path(SPATH_OUTPUT);
		FSDataOutputStream out = null;
		
		if (!fs.exists(path)) fs.mkdirs(path);
		path = new Path(SPATH_OUTPUT + SPATH_ROOTFILE);
		
		if (fs.exists(path)) fs.delete(path, false);
		out = fs.create(path);
		out.writeUTF(username);
		out.close();
	}
	
	/**
	 * Retrieves the graph root.
	 * 
	 * @return the graph root
	 * @throws IOException
	 */
	public static String getGraphRoot() throws IOException {
		Configuration conf = setupConf();
		FileSystem fs = FileSystem.get(conf);
		FSDataInputStream in = fs.open(new Path(SPATH_OUTPUT + SPATH_ROOTFILE));
		String username = in.readUTF();
		in.close();
		return username;
	}
	
	/**
	 * Saves the centroids between K-means iterations.
	 * 
	 * @param counters - the counters containing the centroids
	 */
	public static void setCentroids(Counters counters) throws Exception {
		Configuration conf = setupConf();
		FileSystem fs = FileSystem.get(conf);
		Path path = new Path(BASE_OUTPUT);
		Path tempPath = new Path(BASE_OUTPUT + TEMP_FILE);
		FSDataOutputStream out = null;
		
		if (!fs.exists(path)) fs.mkdirs(path);
		path = new Path(BASE_OUTPUT + CENTROID_FILE);

		if (fs.exists(path)) fs.rename(path, tempPath);
		out = fs.create(path);
		out.writeUTF(new Long(counters.findCounter(Common.Centroids.ITERATION).getValue()).toString());
	    out.writeChar(Common.SEPARATOR.charAt(0));
	    
	    out.writeUTF(Common.Centroids.LOW.toString());
	    out.writeChar(Common.SEPARATOR.charAt(0));
	    out.writeUTF(new Long(counters.findCounter(Common.Centroids.LOW).getValue()).toString());
	    out.writeChar(Common.SEPARATOR.charAt(0));
	    out.writeUTF(new Long(counters.findCounter(Common.Centroids.COUNT_LOW).getValue()).toString());
	    out.writeChar(Common.SEPARATOR.charAt(0));
	    
	    out.writeUTF(Common.Centroids.MEDIUM.toString());
	    out.writeChar(Common.SEPARATOR.charAt(0));
	    out.writeUTF(new Long(counters.findCounter(Common.Centroids.MEDIUM).getValue()).toString());
	    out.writeChar(Common.SEPARATOR.charAt(0));
	    out.writeUTF(new Long(counters.findCounter(Common.Centroids.COUNT_MEDIUM).getValue()).toString());
	    out.writeChar(Common.SEPARATOR.charAt(0));
	    
	    out.writeUTF(Common.Centroids.HIGH.toString());
	    out.writeChar(Common.SEPARATOR.charAt(0));
	    out.writeUTF(new Long(counters.findCounter(Common.Centroids.HIGH).getValue()).toString());
	    out.writeChar(Common.SEPARATOR.charAt(0));
	    out.writeUTF(new Long(counters.findCounter(Common.Centroids.COUNT_HIGH).getValue()).toString());
	    out.writeChar('\n');
	    
	    if (fs.exists(tempPath)) {
	    	FSDataInputStream in = fs.open(tempPath);
	    	int i = 0;
	    	while ((i = in.read()) != -1) out.write(i);
	    	in.close();
	    	fs.delete(tempPath, false);
	    }
	    
	    out.close();
	}
	
	/**
	 * Retrieves the centroids between K-means iterations.
	 * 
	 * @return the centroids
	 */
	public static long[] getCentroids() throws IOException {
		Configuration conf = setupConf();
		FileSystem fs = FileSystem.get(conf);
		Path path = new Path(BASE_OUTPUT + CENTROID_FILE);
		long[] centroids = new long[4];
		
		FSDataInputStream in = fs.open(path);
		centroids[0] = Long.parseLong(in.readUTF());
		in.readChar();
		
		in.readUTF();
		in.readChar();
		centroids[1] = Long.parseLong(in.readUTF());
		in.readChar();
		in.readUTF();
		in.readChar();
		
		in.readUTF();
		in.readChar();
		centroids[2] = Long.parseLong(in.readUTF());
		in.readChar();
		in.readUTF();
		in.readChar();
		
		in.readUTF();
		in.readChar();
		centroids[3] = Long.parseLong(in.readUTF());
		
		in.close();
		
		return centroids;
	}
}
