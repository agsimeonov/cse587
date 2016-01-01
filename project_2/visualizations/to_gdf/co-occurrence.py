# Formats co-occurrence output to a gdf graph file for Gephi

import os
import sys
import colorsys

# Heat map red (lowest) to blue (highest)
def heatmap(minimum, maximum, value):
    if (minimum == maximum):
        maximum = maximum + 1
    hsv = (float(value-minimum) / (maximum-minimum)) * 240
    r, g, b = colorsys.hsv_to_rgb(hsv/360, 1, 1)
    r = str(int(r*255))
    g = str(int(g*255))
    b = str(int(b*255))
    return '\'' + r + ',' + g + ',' +  b + '\''

if len(sys.argv) != 2:
    print 'Please specify the input file as the first (and only) argument!'
    sys.exit()

# Set up files
inputName = sys.argv[1]
inFile = open(inputName, 'r')
fileName = './co-occurrence.gdf'
gdf = open(fileName, 'w')

# Find the maximum word count
elements = inFile.readline().split(',')
maxCount = int(elements[2])

# Initialize the node header
gdf.write('nodedef> name VARCHAR,x DOUBLE,width DOUBLE,')
gdf.write('count INT,relative DOUBLE,color VARCHAR\n')

inFile.seek(0)

# Initialize the nodes
for line in inFile:
    elements = line.split(',')
    name = elements[0] + ' ' + elements[1]
    count = int(elements[2])
    
    gdf.write(name + ',' + str(float(count)) + ',' + str(float(count)))
    gdf.write(',' + str(count) + ',' + elements[3].rstrip())
    gdf.write(',' + heatmap(1, maxCount, count) + '\n')

# Initialize the edge header
gdf.write('edgedef> node1 VARCHAR,node2 VARCHAR,weight DOUBLE\n')

# Close files
inFile.close()
gdf.close()
