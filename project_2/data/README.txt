================================================================================
This folder contains the following:
================================================================================
input/ - folder contains the input data used for map reduce through this project
         363000 tweets in total
output/ - the contents of this folder are the map reduce output for this project

================================================================================
Input details:
================================================================================
Files:
----------
input/data_1.txt - Ukraine crisis data acquired from twitter with an aggregator
input/data_2.txt   these files contain 363000 tweets. 1 being the oldest, and 3
input/data_3.txt   the newest.

Format:
----------
Username | Followers_count | Mentions | Hashtags | Tweet

================================================================================
Wordcount output details:
================================================================================
Files:
----------
output/wordcount/part-r-00000 - unsorted wordcount output hashtags, words, & @
output/wordcount/part-r-00001
output/wordcount/sorted/part-r-00000 - sorted wordcount output full
output/wordcount/sorted/words-r-00000 - sorted wordcount output words only
output/wordcount/sorted/hashtags-r-00000 - sorted wordcount output hashtags only
output/wordcount/sorted/atsign-r-00000 - sorted wordcount output @ only

Format:
----------
word,count

================================================================================
Co-occurrence output details:
================================================================================
Files:
----------
output/coocurrence/pairs/part-r-00000 - alphabetical pairs output
output/coocurrence/pairs/part-r-00001
output/coocurrence/stripes/part-r-00000 - alphabetical stripes output
output/coocurrence/stripes/part-r-00001
output/coocurrence/sorted/part-r-00000 - sorted by count co-occurrence output
output/coocurrence/pairs_five/part-r-00000 - alphabetical pairs with 5 reducers
output/coocurrence/pairs_five/part-r-00001
output/coocurrence/pairs_five/part-r-00002
output/coocurrence/pairs_five/part-r-00003
output/coocurrence/pairs_five/part-r-00004

Format:
----------
hashtag_1,hashtag_2,count,relative_frequency

================================================================================
K-means output details:
================================================================================
Files:
----------
output/centroids.csv - contains K-means iterations begining with the most recent

Format:
----------
iteration_#,cluster_id(low),cluster_coordinates(low),rows_count(low),
cluster_id(medium),cluster_coordinates(medium),rows_count(medium),
cluster_id(high),cluster_coordinates(high),rows_count(high)
--------------------------------------------------------------------------------
Files:
----------
output/kmeans/low-r-00000 - input/data.txt split for the low cluster
output/kmeans/medium-r-00000 - input/data.txt split for the medium cluster
output/kmeans/high-r-00000 - input/data.txt split for the high cluster

Format:
----------
Username | Followers_count | Mentions | Hashtags | Tweet
--------------------------------------------------------------------------------
Files:
----------
output/kmeans/part-r-00000 - sorted list of all users by cluster
output/kmeans/userslow-r-00000 - sorted list of users in the low cluster
output/kmeans/usersmedium-r-00000 - sorted list of users in the medium cluster
output/kmeans/usershigh-r-00000 - sorted list of users in the high cluster

Format:
----------
username,cluster_id,followers_count
--------------------------------------------------------------------------------
Files:
----------
output/kmeans/low/ - wordcount & co-occurrence output for the low cluster
output/kmeans/medium/ - wordcount & co-occurrence output for the medium cluster
output/kmeans//high/ - wordcount & co-occurrence output for the high cluster

Format:
----------
Please refer to wordcount and/or co-occurrence output details.

================================================================================
Shortest path output details:
================================================================================
Files:
----------
output/spath/part-r-00000 - graph data shortest path network for cnnbrk (CNN
                            breaking news) on twitter. Connections are mentions,
                            replies, and retweets bearing a certain username.

Format:
----------
username,distance,connection_1:conntection_2:...:connection_n: