import subprocess
import re
import sys
from collections.abc import Iterable  
import numpy as np

file_name="C:\\Users\\alvis\\OneDrive\\University\\UVA\\EC\\EC-group4\\Assignment 1\\EC4\\src\\properties.txt"
run_times=int(sys.argv[1])

opt = {
    "populationSize": 100,
    "nExec": 10000,
    "selectivePressure": 1.8,
    "tournamentSize": 7,
    "mutationRate": 0.4,
    "mutationVariability": 0.8,
    "elitismElements": range(0,20,5),    
    "ageing":False,
    "ageingFactor":0.0, 
    "generationBound":range(1,10,1),
    "discardBound":range(1,5,1),
    "clusterDistance":np.arange(0,5,0.5),
    "alphaStepSize":range(1,6,1),
}

topScore=0
topDict={}
image_counter=0

def write_file(filename,imagename,dicte):
    file=open(filename,'w')
    for c in dicte.keys():
        file.write("{}={}\n".format(c,dicte[c]))
    file.write("imageName={}\n".format(imagename))
    file.close()

def run(param,counter=0,current={}):
    global topScore
    global topDict
    global image_counter
    image_counter+=1
    if counter==len(param):  
        img=str(image_counter)+'.png'
        
        write_file(str(image_counter)+'.txt',str(image_counter),current)
        tot=0
        for i in range(0,run_times):
            write_file(file_name,str(image_counter)+'_'+str(i)+'.png',current)
            result=subprocess.run(['java','-jar','dist\EC4.jar','-submission=player4','-evaluation=BentCigarFunction','-seed=1'], stderr=subprocess.PIPE, stdout=subprocess.PIPE, universal_newlines=True)
            score=re.match('Score:([0-9]+\.[0-9]+)',str(result.stdout)).group(1)
            tot+=float(score)
        tot/=run_times
        if tot>topScore:
            topScore=tot
            topDict=current.copy()
        print('Mean score '+str(tot))
        print(current)
    else:            
        p=param[counter]
        if isinstance(opt[p], Iterable):
            for e in opt[p]:
                current[p]=e
                run(param,counter+1,current)
        else:
            current[p]=opt[p]
            run(param,counter+1,current)

run([*opt])

print('--------------------')
print('TOP SCORE:'+str(topScore))
print(topDict)
