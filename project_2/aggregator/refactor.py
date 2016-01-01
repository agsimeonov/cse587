# Run this script to combine and refactor data files

import os

directory = './data/'
complete = 1000

combineFileName = './data.txt'
combineFile = open(combineFileName, 'w')

for file in os.listdir(directory):
    f = open(directory + file)
    lines = sum(1 for line in f)
    print directory + file + ' contains ' + str(lines) + ' lines:',
    if lines != complete:
        print 'File is incomplete - deleting!'
        f.close()
        os.remove(directory + file)
    else:
        print 'File is complete!'
        f.seek(0)
        for line in f:
            combineFile.write(line)
        f.close()

print 'Data files combined into ' + combineFileName
combineFile.close()
