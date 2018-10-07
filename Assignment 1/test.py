import subprocess
import re
import sys

n_exec=int(sys.argv[1])
tot=0
for i in range(0,n_exec):
    result = subprocess.run(['java','-jar','testrun.jar','-submission=player4','-evaluation=BentCigarFunction','-seed=1'], stdout=subprocess.PIPE,stderr=subprocess.PIPE,shell=True)
    r=re.search('Score: ([0-9]+\.[0-9]+)',str(result))
    tot+=float(r.group(1))

print('Mean after {} executions is :{}'.format(sys.argv[1],tot/n_exec))
