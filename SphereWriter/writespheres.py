# Author: Tracy Medcalf
# Takes an integer as input and writes that many spheres to the file

import random

fileName = input("What file do you want to write to?")
numSpheres = int(input("How many spheres do you want to write to file?"))
# Open file
file = open(fileName,'w')
# Write the spheres
for i in range(0,numSpheres) :
    output = 'sphere:'
    for i in range(3) :
        output += " " + '%.3f'%(random.uniform(-10,10))
    output += " " + '%.3f'%(random.uniform(1,4))
    for i in range(3) :
        output += ' ' + '%.3f'%random.random()
    file.write(output + '\n')
    
file.close()
print("Finished printing...")
