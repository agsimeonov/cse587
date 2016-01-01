# Formats shortest path output to a gdf graph file for Gephi

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
fileName = './shortest_path.gdf'
gdf = open(fileName, 'w')

# Find maxDistance for the heat map
for line in inFile:
    pass

elements = line.split(',')
maxDst = int(elements[1])

# Initialize the node header
gdf.write('nodedef> name VARCHAR,width DOUBLE,distance INT,color VARCHAR\n')

inFile.seek(0)

# Initialize the nodes
for line in inFile:
    elements = line.split(',')
    name = elements[0]
    distance = int(elements[1])
    width = float(distance + 1)
    gdf.write(name + ',' + str(width) + ',')
    gdf.write(str(distance) + ',' + heatmap(0, maxDst, distance) + '\n')

# Initialize the edge header
gdf.write('edgedef> node1 VARCHAR,node2 VARCHAR,weight DOUBLE\n')

inFile.seek(0)

# Initialize the edges
for line in inFile:
    elements = line.split(',')
    node1 = elements[0]
    weight = float(elements[1]) / 100
    neighbors = elements[2].split(':')
    for node2 in neighbors:
        if node2 != '\n':
            gdf.write(node1 + ',' + node2 + ',' + str(weight) + '\n')

# Close files
inFile.close()
gdf.close()
