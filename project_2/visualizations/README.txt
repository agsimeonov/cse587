--------------------------------------------------------------------------------
This folder contains the following files:
--------------------------------------------------------------------------------
to_gdf/ - python scripts for converting outputs to Gephi graphs
to_gdf/wordcount.py - transforms wordcount output to gdf
to_gdf/co-occurrence.py - transforms co-occurrence output to gdf
to_gdf/k-means.py - transforms k-means output to gdf
to_gdf/shortest_path.py - transforms shortest path output to gdf
to_gdf/to_gdf.sh - Creates all of the Gephi graph files for this project
gdf/ - Gephi graphs (result of the scripts in to_gdf)
sigmajs/ - Sigmajs Exporter output from Gephi
screenshots/ - select screenshots from the Sigma.js visualizations

--------------------------------------------------------------------------------
To fully utilize the visualizations you need to have the following installed:
--------------------------------------------------------------------------------
Python 2.7.x
Gephi
Sigmajs Exporter

--------------------------------------------------------------------------------
To install the above you need to do the following:
--------------------------------------------------------------------------------
1. Download and install Python 2.7.x from: https://www.python.org/downloads
2. Download and install Gephi from: https://gephi.org/
3. Download Sigmajs Exporter from:
   https://marketplace.gephi.org/plugin/sigmajs-exporter/
4. Open Gephi
5. Go to Tools -> Plugins -> Downloaded
6. Click on Add Plugins and add Sigmajs Exporter
7. Click on Install

--------------------------------------------------------------------------------
To run the reproduce the Graph file output in gdf/ using the scripts:
--------------------------------------------------------------------------------
Make sure the directory structure is not changed for this package.

1. Change your directory to: to_gdf/
2. Run: chmod 777 to_gdf.sh
3. Run: ./to_gdf

--------------------------------------------------------------------------------
To import/export graphs from Gephi:
--------------------------------------------------------------------------------
1. Open Gephi
2. Click on File -> Open
3. Select the desired graph file
4. Click OK
5. Go to on File -> Export -> Sigma.js template
6. Click on Browse and select a folder to export to
7. Click OK

--------------------------------------------------------------------------------
IMPORTANT:
--------------------------------------------------------------------------------
It seems Google Chrome has trouble displaying the Sigma.js visualizations
however I have tested it and confirmed it to work in Safari and Firefox.
