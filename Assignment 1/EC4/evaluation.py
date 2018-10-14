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
    "selectivePressure": 1.8,#np.arange(1.0,2.1,0.1),
    "tournamentSize": range(3,15,1),
    "mutationRate": np.arange(0.3,0.9,0.1),
    "mutationVariability": np.arange(0,1,0.1),
    "crossoverPoints":range(1,6,1),
    "elitismElements": 0,#range(1,20,1),
    "ageing":False,
    "ageingFactor":0.0
}

topScore=0
topDict={}

def run(param,counter=0,current={}):
    global topScore
    global topDict
    
    if counter==len(param):
        
        file=open(file_name,'w')
        for c in current.keys():
            file.write("{}={}\n".format(c,current[c]))
        file.close()
        tot=0
        for i in range(0,run_times):
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
try:
    run([*opt])
except:
    pass
print('--------------------')
print('TOP SCORE:'+str(topScore))
print(topDict)
