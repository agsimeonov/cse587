#!/bin/bash
# Creates the graph files for Gephi

mkdir ../gdf

# Wordcount
mkdir ../gdf/wordcount
python wordcount.py ../../data/output/wordcount/sorted/part-r-00000
mv wordcount.gdf ../gdf/wordcount/wordcount.gdf
python wordcount.py ../../data/output/wordcount/sorted/words-r-00000
mv wordcount.gdf ../gdf/wordcount/words.gdf
python wordcount.py ../../data/output/wordcount/sorted/hashtags-r-00000
mv wordcount.gdf ../gdf/wordcount/hashtags.gdf
python wordcount.py ../../data/output/wordcount/sorted/atsign-r-00000
mv wordcount.gdf ../gdf/wordcount/atsign.gdf

# Co-occurence
python co-occurrence.py ../../data/output/coocurrence/sorted/part-r-00000
mv co-occurrence.gdf ../gdf/co-occurrence.gdf

# K-means
mkdir ../gdf/kmeans
python k-means.py ../../data/output/kmeans/part-r-00000
mv k-means.gdf ../gdf/kmeans/k-means.gdf

mkdir ../gdf/kmeans/wordcount
mkdir ../gdf/kmeans/wordcount/low
python wordcount.py ../../data/output/kmeans/low/wordcount/sorted/part-r-00000
mv wordcount.gdf ../gdf/kmeans/wordcount/low/wordcount.gdf
python wordcount.py ../../data/output/kmeans/low/wordcount/sorted/words-r-00000
mv wordcount.gdf ../gdf/kmeans/wordcount/low/words.gdf
python wordcount.py ../../data/output/kmeans/low/wordcount/sorted/hashtags-r-00000
mv wordcount.gdf ../gdf/kmeans/wordcount/low/hashtags.gdf
python wordcount.py ../../data/output/kmeans/low/wordcount/sorted/atsign-r-00000
mv wordcount.gdf ../gdf/kmeans/wordcount/low/atsign.gdf

mkdir ../gdf/kmeans/wordcount/medium
python wordcount.py ../../data/output/kmeans/medium/wordcount/sorted/part-r-00000
mv wordcount.gdf ../gdf/kmeans/wordcount/medium/wordcount.gdf
python wordcount.py ../../data/output/kmeans/medium/wordcount/sorted/words-r-00000
mv wordcount.gdf ../gdf/kmeans/wordcount/medium/words.gdf
python wordcount.py ../../data/output/kmeans/medium/wordcount/sorted/hashtags-r-00000
mv wordcount.gdf ../gdf/kmeans/wordcount/medium/hashtags.gdf
python wordcount.py ../../data/output/kmeans/medium/wordcount/sorted/atsign-r-00000
mv wordcount.gdf ../gdf/kmeans/wordcount/medium/atsign.gdf

mkdir ../gdf/kmeans/wordcount/high
python wordcount.py ../../data/output/kmeans/high/wordcount/sorted/part-r-00000
mv wordcount.gdf ../gdf/kmeans/wordcount/high/wordcount.gdf
python wordcount.py ../../data/output/kmeans/high/wordcount/sorted/words-r-00000
mv wordcount.gdf ../gdf/kmeans/wordcount/high/words.gdf
python wordcount.py ../../data/output/kmeans/high/wordcount/sorted/hashtags-r-00000
mv wordcount.gdf ../gdf/kmeans/wordcount/high/hashtags.gdf
python wordcount.py ../../data/output/kmeans/high/wordcount/sorted/atsign-r-00000
mv wordcount.gdf ../gdf/kmeans/wordcount/high/atsign.gdf

mkdir ../gdf/kmeans/co-occurrence
python co-occurrence.py ../../data/output/kmeans/low/coocurrence/sorted/part-r-00000
mv co-occurrence.gdf ../gdf/kmeans/co-occurrence/low_co-occurrence.gdf
python co-occurrence.py ../../data/output/kmeans/medium/coocurrence/sorted/part-r-00000
mv co-occurrence.gdf ../gdf/kmeans/co-occurrence/medium_co-occurrence.gdf
python co-occurrence.py ../../data/output/kmeans/high/coocurrence/sorted/part-r-00000
mv co-occurrence.gdf ../gdf/kmeans/co-occurrence/high_co-occurrence.gdf

# Shortest path
python shortest_path.py ../../data/output/spath/part-r-00000
mv shortest_path.gdf ../gdf/shortest_path.gdf
