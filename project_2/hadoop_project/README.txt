--------------------------------------------------------------------------------
This folder contains the following files:
--------------------------------------------------------------------------------
project2.tar - bundle containing my MR project for hadoop
project2.jar - a jar file of the above project

--------------------------------------------------------------------------------
To run this project you need the have the following installed and configured:
--------------------------------------------------------------------------------
Java 1.6
Hadoop 2.2.0
Eclipse Kepler

--------------------------------------------------------------------------------
Tested on the following environment:
--------------------------------------------------------------------------------
OS X 10.8.5
Java 1.6.0_65 (http://support.apple.com/kb/DL1573)
Hadoop 2.2.0

Setup using the following guide:
hadoop_install_with_eclipse_plugin_and_code_changes.docx
https://piazza.com/class_profile/get_resource/hog616iewln1p/hsf9hbalzal3f9

Hadoop installation directory:
/Applications/hadoop-2.2.0

On Mac OS X you may want to run the following command if the jar isn't working:
sudo ln -s /usr/bin/java /bin/java

--------------------------------------------------------------------------------
(Optional) Edit the source code to reflect for your own environment:
--------------------------------------------------------------------------------
You can change the default input path changing the DEFAULT_INPUT variable in
'src/common/Common.java'.  This variable defaults to the "/input" directory.

In order tor run this project directly from Eclipse you may want to change lines
63 and 64 in 'src/common/Common.java' to contain the path to your core-site.xml
and hdfs-site.xml files in your hadoop deployment directory.

--------------------------------------------------------------------------------
Initialize the hadoop:
--------------------------------------------------------------------------------
1. Run: 'start-all.sh'

Alternatively run:
1. start-dfs.sh
2. start-yarn.sh

--------------------------------------------------------------------------------
DFS locations setup:
--------------------------------------------------------------------------------
The default input directory which is going to be used is "/input". 

Please perform the following steps:
1. Run: hadoop fs -mkdir /input
2. Run: hadoop fs -put <location_of_input> /input

You can use the input file provided (363000 tweets) in data/input/data.txt

--------------------------------------------------------------------------------
Input format:
--------------------------------------------------------------------------------
The only input format you will use is the one specified by the aggregator. All
data needed for the project parts can be contained and/or derived from it.

Format:
----------
Username | Followers_count | Mentions | Hashtags | Tweet

--------------------------------------------------------------------------------
Import the project in Eclipse:
--------------------------------------------------------------------------------
1. In Eclipse go to File -> Import -> General -> Existing Project into Workspace
2. Browse and select project2.tar in the 'Select archive file' field
3. Click on Finish

--------------------------------------------------------------------------------
To run the project directly from Eclipse:
--------------------------------------------------------------------------------
1. Set up the hadoop plugin for eclipse:
   https://github.com/winghc/hadoop2x-eclipse-plugin
2. You may use the above mentioned guide for configurations:
   https://piazza.com/class_profile/get_resource/hog616iewln1p/hsf9hbalzal3f9
3. Set up the optional step above by setting lines 63 and 64 for 'Common.java'
4. Set the following VM arguments in Eclipse: -Xms512M -Xmx1524M
   They need to be set in the project's Run Configurations -> Arguments
5. To run everything run: 'src/driver/All.java'
6. For wordcount run: 'src/driver/WordCount.java'
7. For co-occurrence run: 'src/driver/CoOccurrence.java'
8. For pairs only run: 'src/driver/Pairs.java'
9. For stripes only run: 'src/driver/Stripes.java'
10. For k-means with addition outputs run: 'src/driver/Kmeans.java'
11. For k-means only run: 'src/driver/KmeansOnly.java'
12. For shortest path run: 'src/driver/ShortestPath.java'

--------------------------------------------------------------------------------
To run the project using a jar file:
--------------------------------------------------------------------------------
1. Set the following VM arguments in Eclipse: -Xms512M -Xmx1524M
   They need to be set in the project's Run Configurations -> Arguments
2. In eclipse go to File -> Export -> Java -> JAR file
3. Select project2
4. Make sure 'Export generated class files and resources' is selected
5. In the Jar file: filed enter <desired_location>/project2.jar
6. Click on Finish
7. With your terminal navigate to: cd <desired_location>
8. To run everything run: hadoop jar project2.jar driver.All
9. For wordcount run: hadoop jar project2.jar driver.WordCount
10. For co-occurrence run: hadoop jar project2.jar driver.CoOccurrence
11. For pairs only run: hadoop jar project2.jar driver.Pairs
12. For pairs with 5 reducers run: hadoop jar project2.jar driver.PairsFive
13. For stripes run: hadoop jar project2.jar driver.Stripes
14. For k-means with addition outputs run: hadoop jar project2.jar driver.Kmeans
15. For k-means only run: hadoop jar project2.jar driver.KmeansOnly
16. For shortest path run: hadoop jar project2.jar driver.ShortestPath

--------------------------------------------------------------------------------
(Optional) Additional arguments:
--------------------------------------------------------------------------------
It is possible to specify input arguments for the tests.
args[0] -> Input path (default is "/input")
args[1] -> Output path (default is "/output")
