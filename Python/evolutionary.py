import json
import random 
import math
import time
import struct
from functools import partial,reduce
import numpy as np
import pylab
'''
Main parts of an EC algorithm:
    -genotype (representation)
    -population (Exploiting vs exploring)
    -cross over
    -mutation
    -selection
    -fitness
    -initialization
    -termination
'''

parameters={}

def is_number(s):
    try:
        float(s)
        return True
    except ValueError:
        return False

class Parameters_loader:
    parameters=None
    file_name=None
    
    @staticmethod  
    def init(file_name):
        Parameters_loader.file_name=file_name
        with open(file_name) as f:
            data = json.load(f)
        Parameters_loader.parameters=data

    @staticmethod 
    def set_globals():
        if Parameters_loader.parameters is not None:
            global parameters
            parameters=Parameters_loader.parameters
            #for param in parameters:
            #    parameters[param]=Parameters_loader.parameters[param]
        else:
            raise Exception("Parameters' file name not initialized")

    @staticmethod
    def get_parameter(param_name):
        if Parameters_loader.parameters is not None:
            return Parameters_loader.parameters[param_name]
        else:
            raise Exception("Parameters' file name not initialized")
        
class Population:

    def __init__(self,population=[]):
        self.population=population

    def fittest_speciment(self):
        max=self.pop_eval[0]
        for e in self.pop_eval:
            if e[1]>max[1]:
                max=e
        return max

    def average_fit(self,fitness):
        l= [fitness(a) for a in self.population]
        return reduce(lambda x, y: x + y,l) / len(l)

    def merge_speciments(self,new):
        for e in new:
            self.population.append(e)

    def initialize_random_continue(self, size, dimension, min, max):
        self.population=[[random.random()*(abs(min)+abs(max))-abs(min) for i in range(0,dimension)] for j in range(0,size)]
        return self.population

    def assign_fitness(self,fitness):
        self.pop_eval=[(p,fitness(p)) for p in self.population]
        return self.pop_eval
    
    def reproduce_no_distance(self,crossover,mutation,mut_probability):
        offspring=[]
        for i,p1 in enumerate(self.population):
            p2=random.randint(0,len(self.population)-1)
            while p2==i:
                p2=random.randint(0,len(self.population)-1)
            p2=self.population[p2]
            child=crossover(p1,p2)
            if random.random()<mut_probability:
                child=mutation(child)
            offspring.append(child)
        return offspring

    def prune(self,function):
        self.population=function(self.pop_eval)

    def proximity(self,chr1,chr2):
        dist=0
        for x,y in zip(chr1,chr2):
            dist+=(x-y)**2
        return math.sqrt(dist)

    def fixed_radius_neighbors_dumm(self, chr, dist):
        res=[]
        for ne in self.population:
            if self.proximity(chr,ne)<=dist and ne!=chr:
                res.append(chr)
        return res

class Cross_over:
    @staticmethod
    def single_point(e1,e2):
        point=random.randint(0, len(e1))
        res=[]
        for counter,a,b in enumerate(zip(e1,e2)):
            if counter<=point:
                res.append(a)
            else:
                res.append(b)
    
    @staticmethod
    def multiple_point(e1,e2,n):
        points=[random.randint(int(i*(len(e1)-1)/n),int((i+1)*(len(e1)-1)/n)) for i in range(0,n)]
        divider=0
        element=e1
        res=[]
        overflow_condition=True
        for i,a in enumerate(zip(e1,e2)):
            if i>points[divider] :
                if i < len(points):
                    divider+=1
                if i < len(points) and overflow_condition:
                    if element==e2:
                        element=e1
                    else:
                        element=e2

            if element==e1:
                res.append(a[0])
            else:
                res.append(a[1])
        return res
       
class Mutation:
    
    @staticmethod
    def binary_mutation(chr):
        def float_to_bin(num):
            return bin(struct.unpack('!I', struct.pack('!f', num))[0])[2:].zfill(32)

        def bin_to_float(binary):
            return float(int(binary.replace('0b', binary), 2))

        def bin_to_int(binary):
            return int(binary, 2)

        child=[]
        for gene in chr:
            res=""
            #take into account every digit
            for number in str(gene):
                #if it is not a digit it is a sign
                if is_number(number):
                    new_nb=""
                    #convert the integer number in a bit string
                    for bin in '{0:b}'.format(int(number)):
                        value=int(bin)
                        if random.random()<parameters["binary_mutation_probability"]:
                            value=1-value
                        new_nb+=str(value)
                    res+=str(bin_to_int(new_nb))
                else:
                    #randomize the new sign
                    if random.random()>parameters["binary_mutation_probability"] or number=="-":    
                        res+=number
            try:
                child.append(float(res))
            except ValueError:
                print("Original number :{}, has been converted into {} reised an error".format(number,res))
                child.append(float(number))
        return child

    @staticmethod
    def random_rep_float(chr,min,max):
        res=[]
        for e in chr:
            step=np.random.normal(0, parameters["learning_rate"])
            if e>max-step or e<min-step:
                step=-step
            res.append(e+step)
        return res
             
class Selection:
    @staticmethod
    def cut_half(population):
        population.sort(key=lambda tup: tup[1],reverse=True) 
        return [a[0] for a in population[0:int((len(population)+1)/2)]]

    @staticmethod
    def cut_half_with_evaluation(population,fitness):
        fit_pop=[(e,fitness(e)) for e in population]
        fit_pop.sort(key=lambda tup: tup[1],reverse=True) 
        return [a[0] for a in fit_pop[0:int((len(fit_pop)+1)/2)]]

class Fitness:
    @staticmethod
    def pow(chromosome):
        return -(chromosome[0]**2)
    

    @staticmethod
    def Ackley(chromosome):
        """
        each component of the cromosome -32<=x<=32
        """
        firstSum = 0.0
        secondSum = 0.0
        for c in chromosome:
            firstSum += c**2.0
            secondSum += math.cos(2.0*math.pi*c)
        n = float(len(chromosome))
        return -(-20.0*math.exp(-0.2*math.sqrt(firstSum/n)) - math.exp(secondSum/n) + 20 + math.e)
    
class Compute_algorithm:

    @staticmethod
    def run():
        #creation of the population
        population=Population()
        population.initialize_random_continue(parameters["population_size"],parameters["speciment_size"],-32,32)
        iteration=0
        #conditio for satisfaction grade
        cond=False
        fittest=[]
        avg_fitness=[]
        while iteration<parameters["iter_limit"] and not cond:
            iteration+=1
            #reproduction
            #offspring=population.reproduce_no_distance(partial(Cross_over.multiple_point,n=2),partial(Mutation.random_rep_float,min=-32,max=32),parameters["mutation_probability"])
            offspring=[Mutation.binary_mutation(p) for p in population.population]
            population.merge_speciments(offspring)
            #fitness of the population
            population.assign_fitness(getattr(Fitness,parameters["fitness_function"]))
            #selection
            population.prune(Selection.cut_half)

            fittest.append(population.fittest_speciment()[1])
            avg_fitness.append(population.average_fit(getattr(Fitness,parameters["fitness_function"])))

            #graph part
            if iteration%parameters["graph_frequency"]==0:
                #print of the point on the function
                """ 
                r_function=np.linspace(-32,32,parameters["population_size"])
                points=np.array([getattr(Fitness,parameters["fitness_function"])(p) for p in population.population])
                arr=[p[0] for p in population.population]
                plot_scatter((np.array(arr),points))
                """
                #plotting the average fitness and the fittest element during the generations
                range=np.linspace(0,iteration,iteration)
                graph1=np.array(fittest)
                graph2=np.array(avg_fitness)
                plot_graph(range,graph1[-1],graph2[-1],(graph1,"fittest"))

            if parameters["waiting_time"]!=0:
                time.sleep(parameters["waiting_time"]/1000)

def plot_Ackley(e):
    '''
    function equal to the other one, but used for plotting it
    '''
    def f(c):
        """
        each component of the cromosome -32<=x<=32
        """
        firstSum = 0.0
        secondSum = 0.0
        firstSum += c**2.0
        secondSum += math.cos(2.0*math.pi*c)
        n = 1.0
        return -(-20.0*math.exp(-0.2*math.sqrt(firstSum/n)) - math.exp(secondSum/n) + 20 + math.e)
    return np.array([f(l) for l in e])

def plot_scatter(*arg):
    pylab.cla()
    pylab.ion()
    pylab.show()
    for range,p in arg:
        pylab.scatter(range, p, s=10,color='red')
    r=np.linspace(-32,32,1000)
    pylab.plot(r,np.array([getattr(Fitness,parameters["fitness_function"])([p]) for p in r]))
    pylab.draw()
    pylab.pause(0.001)

def plot_graph(range,fittest,average,*arg):
    font = {'family': 'serif',
        'color':  'darkred',
        'weight': 'normal',
        'size': 16,
        }
    pylab.cla()
    pylab.ion()
    pylab.show()
    for p,label in arg:
        pylab.plot(range,p,label=label)
    pylab.legend(loc='bottom right')
    pylab.xlabel('Generation', fontdict=font)
    pylab.ylabel('Fitness', fontdict=font)
    pylab.text(15, -13,"fittest: "+str(fittest),size=10)
    pylab.text(15,-10,"average: "+str(average),size=10)
    pylab.draw()
    pylab.pause(0.001)

def main():
    print(Fitness.Ackley([0]))
    Parameters_loader.init("parameters.pyt")
    Parameters_loader.set_globals()
    Compute_algorithm.run()
    pass

if __name__== "__main__":
    main()
