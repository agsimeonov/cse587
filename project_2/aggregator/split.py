# Split data.txt into input partitions

import os
import sys

# Set up
partitions = int(sys.argv[1])
f = open('./data.txt', 'r')

# Get number of lines
lines = sum(1 for line in f)
f.seek(0)

size = int(lines/partitions)

i = 0
j = 1
inFile = open('./data_' + str(j) + '.txt', 'w')
for line in f:
    if i < size:
        inFile.write(line)
        i = i + 1
        lines = lines - 1
    elif lines != 0:
        inFile.close()
        j = j + 1
        inFile = open('./data_' + str(j) + '.txt', 'w')
        inFile.write(line)
        i = 1

inFile.close()
f.close()
