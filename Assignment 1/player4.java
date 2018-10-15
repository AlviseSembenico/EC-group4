
import org.vu.contest.ContestSubmission;
import org.vu.contest.ContestEvaluation;

import java.util.LinkedList;
import java.util.Random;
import java.util.Properties;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Collections;
import java.util.Iterator;
import java.util.stream.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class player4 implements ContestSubmission {

    public static Random rnd_;
    public static ContestEvaluation evaluation;
    private int evaluations_limit_=100000;
    // Population size
    private int populationSize = 100;
    private final int F_DIMENSIONS = 10;
    private LinkedList<Individual> population;
    private double selectivePressure = 1.8;
    private int tournamentSize = 7;
    private double mutationRate = 0.4;
    private double mutationVariability = 0.8;
    private int crossoverPoints = 2;
    private boolean ageing = false;
    private int elitismElements = 0;
    private double ageingFactor = 0.3;
    private double clusterRadius = 0.5;
    private double arithmeticCrossoverStep=0.5;
    
    /**
     * Initialize the popoulation randomly
     */
    private void populationInitialization() {
        population = new LinkedList<Individual>();
        for (int j = 0; j < populationSize; j++) {
            population.add(new Individual(j));
        }
    }

    private void setParameters() {

        Properties prop = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream("C:\\Users\\alvis\\OneDrive\\University\\UVA\\EC\\EC-group4\\Assignment 1\\EC4\\src\\properties.txt");
            // load a properties file
            prop.load(input);
            // get the property value and print it out
            populationSize = Integer.valueOf(prop.getProperty("populationSize"));
            selectivePressure = Double.valueOf(prop.getProperty("selectivePressure"));
            tournamentSize = Integer.valueOf(prop.getProperty("tournamentSize"));
            mutationRate = Double.valueOf(prop.getProperty("mutationRate"));
            mutationVariability = Double.valueOf(prop.getProperty("mutationVariability"));
            crossoverPoints = Integer.valueOf(prop.getProperty("crossoverPoints"));
            elitismElements = Integer.valueOf(prop.getProperty("elitismElements"));
            ageing = Boolean.valueOf(prop.getProperty("ageing"));
            ageingFactor = Double.valueOf(prop.getProperty("ageingFactor"));
            Individual.nEval = Integer.valueOf(prop.getProperty("nExec"));

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public player4() {
        rnd_ = new Random();
        Individual.nDimension = 10;
        Individual.nEval=evaluations_limit_;
        populationInitialization();
        //setParameters();
    }

    private void mutateChild2(double premuChild[]) {
        // Mutation that will move shortly in the 10D space (Low variation)
        double p1 = 0.8 * mutationVariability + 0.1; // p1 from 0.1 to 0.9
        // Move some random values a random % distance towards one of the sides (+ or -)
        for (int i = 0; i < premuChild.length; i++) {
            double coinFlip = rnd_.nextDouble();
            boolean movePositive = coinFlip > 0.5;
            coinFlip = rnd_.nextDouble();
            if (coinFlip < p1) {
                double maxDistance = 0.0;
                if (movePositive) {
                    maxDistance = 5.0 - premuChild[i];
                } else {
                    maxDistance = -Math.abs(-5.0 - premuChild[i]);
                }

                premuChild[i] = premuChild[i] + (rnd_.nextDouble() * maxDistance);
            }
        }
    }

    /**
     * t Crossover with crossover point in the middle
     */
    private Individual crossover(List<Individual> parents, int numPoints, int position) {
        // Using 3 parents, make a 3 point crossover
        // Choose the 3 points
        List<Integer> crossovers = new LinkedList<Integer>();
        int randomCrossover;
        for (int i = 0; i < numPoints; i++) {
            randomCrossover = rnd_.nextInt(F_DIMENSIONS - 1);
            if (!crossovers.contains(randomCrossover)) {
                crossovers.add(randomCrossover);
            } else {
                i--;
            }
        }

        Collections.sort(crossovers);
        double[] newChild = new double[10];
        Iterator p = parents.iterator(), c = crossovers.iterator();
        Individual currentParent = (Individual) p.next();
        int point = (Integer) c.next();

        for (int i = 0; i < F_DIMENSIONS; i++) {
            if (i == point) {
                if (!p.hasNext()) {
                    p = parents.iterator();
                }
                currentParent = (Individual) p.next();

                if (!c.hasNext()) {
                    point = -1;
                }
            }
            newChild[i] = currentParent.points[i];
        }
        return new Individual(newChild, position);
    }
    
    private Individual arithmeticCrossover(List<Individual> parents,int position){
        double[] res=new double[F_DIMENSIONS];
        Individual p1,p2;
        p1=parents.get(0);
        p2=parents.get(1);
        for(int i=0;i<F_DIMENSIONS;i++)
                res[i]=arithmeticCrossoverStep*p1.points[i]+(1-arithmeticCrossoverStep)*p2.points[i];
        return new Individual(res, position);
    }
    
    private Individual uniformCrossover(List<Individual> parents,int position){
        double[] res=new double[F_DIMENSIONS];
        Individual p1,p2;
        p1=parents.get(0);
        p2=parents.get(1);
        for(int i=0;i<F_DIMENSIONS;i++)
            if(rnd_.nextDouble()>0.5)
                res[i]=p1.points[i];
            else
                res[i]=p2.points[i];
        return new Individual(res, position);
    }
    

    public void setSeed(long seed) {
        // Set seed of algortihms random process
        rnd_.setSeed(seed);
    }

    public void setEvaluation(ContestEvaluation evaluation) {
        // Set evaluation problem used in the run
        this.evaluation = evaluation;

        // Get evaluation properties
        Properties props = evaluation.getProperties();
        // Get evaluation limit
        evaluations_limit_ = Integer.parseInt(props.getProperty("Evaluations"));
        // Property keys depend on specific evaluation
        // E.g. double param = Double.parseDouble(props.getProperty("property_name"));
        boolean isMultimodal = Boolean.parseBoolean(props.getProperty("Multimodal"));
        boolean hasStructure = Boolean.parseBoolean(props.getProperty("Regular"));
        boolean isSeparable = Boolean.parseBoolean(props.getProperty("Separable"));

        // Do sth with property values, e.g. specify relevant settings of your algorithm
        if (isMultimodal) {
            // Do sth
        } else {
            // Do sth else
        }
    }

    private double sumFitness() {
        double total = 0.0;
        for (Individual individual : population) {
            total += individual.getFitness();
        }
        return total;
    }

    /**
     *
     * @param position order of arrival of the parent within the tournament
     * @param u the dimension of the tournament
     * @return the probability for the parent to be chosen
     */
    private double linearRanking(int position, int u) {
        return ((2 - selectivePressure) / u) + ((2 * position * (selectivePressure - 1)) / (u * (u - 1)));
    }

    /**
     *
     * @param n # of random number generated
     * @param min min for every number
     * @param max max for every number
     * @return n integer random number between min and max
     */
    private static int[] random(int n, int min, int max) {
        int[] res = new int[n];
        int[] i = new int[]{0};
        rnd_.ints(n, min, max).forEach(rn -> {
            res[i[0]++] = rn;
        });
        return res;
    }

    private List<Individual> tournament(LinkedList<Individual> source, int tournamentSize, int winners, int rounds) {
        List<Individual> res = new LinkedList<Individual>();
        List<Individual> candidates = (List) source.clone();

        for (int r = 0; r < rounds; r++) {
            // select randomly the index of 5 parents
            List<Integer> parents = IntStream.of(random(tournamentSize, 0, candidates.size() - 1)).boxed()
                    .collect(Collectors.toList());

            HashMap<Individual, Double> tournament = new HashMap<Individual, Double>();
            Collections.sort(parents);

            // add all selected parents to tournament
            int iteration = 0;
            parents.forEach(p -> {
                tournament.put(candidates.get(p), 0.0);
            });

            for (int w = 0; w < winners; w++) {
                // calculate the total fitness of the tournament
                double totalFitness = 0;
                for (Individual candidate : tournament.keySet()) {
                    totalFitness += candidate.getFitness();
                }
                if (totalFitness == 0) {
                    for (int c = 0; c < winners * rounds - res.size(); c++) {
                        res.add(candidates.get(c));
                    }
                }

                // calculate the probability for every parent to be chosen, the sum is 1.0
                iteration = 0;
                for (Individual candidate : tournament.keySet()) {
                    tournament.replace(candidate, candidate.getFitness() / totalFitness);
                }

                double amount = 0.0;
                // randomize a number between 0 and 1
                double extract = rnd_.nextDouble();
                for (Individual element : tournament.keySet()) {
                    amount += tournament.get(element);
                    if (extract <= amount) {
                        // add the winner to the outcome
                        res.add(element);
                        // remove the winner for next round within the same tournament
                        tournament.remove(element, tournament.get(element));
                        // remove also to general candidates
                        candidates.remove(element);
                        break;
                    }
                }

            }
        }

        return res;
    }

    private List<Individual> topIndividual(int n) {
        List<Individual> top = new LinkedList<Individual>();
        Collections.sort(population);
        for (n--; n >= 0; n--) {
            top.add(population.get(n));
        }
        return top;
    }

    private void slideWindow() {
        double minFitness = 0.0;
        for (Individual ind : population) {
            if (ind.getFitness() < minFitness) {
                minFitness = ind.getFitness();
            }
        }
        for (Individual ind : population) {
            ind.fitness -= minFitness;
        }
    }

    private void ageing() {
        for (Individual ind : population) {
            ind.fitness -= ageingFactor;
        }
    }

    private double[][] distanceMatrix() {
        double[][] matrix = new double[populationSize][populationSize];
        Iterator<Individual> c1 = population.iterator();
        while (c1.hasNext()) {
            Individual ch1 = c1.next();
            Iterator<Individual> c2 = population.iterator();
            while (c2.hasNext()) {
                Individual ch2 = c2.next();
                matrix[ch1.position][ch2.position] = ch1.distance(ch2);
            }

        }
        return matrix;
    }

    public List<Cluster> agglomerativeClustering(List<Individual> list) {
        List<Cluster> c = new LinkedList<Cluster>();
        for (Individual i : list) {
            c.add(new Cluster(i));
        }
        while (c.size() > 1) {
            double minDist = Double.POSITIVE_INFINITY;;
            Cluster min1 = null, min2 = null;
            for (Cluster c1 : c) {
                for (Cluster c2 : c) {
                    double tmp = c1.averageDistance(c2);
                    if (tmp != 0 && tmp < minDist && c1.maxDistance(c2) < clusterRadius) {
                        minDist = tmp;
                        min1 = c1;
                        min2 = c2;
                    }
                }
            }
            if (min1 == null || min2 == null) {
                break;
            }
            c.remove(min2);
            min1.components.addAll(min2.components);
        }
//        System.out.print(c.size() + " ");
//        for (Cluster cl : c) {
//            System.out.print("(" + cl.components.size() + "," + cl.fitnessVariance() + "," + cl.fitnessMean() + ")");
//        }
//        System.out.println("");
        return c;
    }
    
    private List<Individual> reproduceList(List<Individual> l, int children, int position){
        return reproduceList(l, children, position,1);
    }
    
    private List<Individual> reproduceCluster(Cluster c, int children,int position){
        return reproduceList(c.components, children, position, c.getAlphaDynamicStepSize());
    }

    private List<Individual> reproduceList(List<Individual> l, int children, int position,double clusterScale) {
        if (children == 0 || children > l.size()) {
            children = l.size();
        }
        List<Individual> copy=new LinkedList<Individual>();
        copy.addAll(l);
        for(int i=3-l.size();i>0;i--)
            copy.add(population.get(rnd_.nextInt(population.size()-1)));

        List<Individual> res = new LinkedList<Individual>();
        for (int i = 0; i < children; i++) {

            List<Individual> parents = tournament((LinkedList<Individual>) copy, tournamentSize, 3, 1);
            Individual child = arithmeticCrossover(parents, position++);
            if (rnd_.nextDouble() < mutationRate) {
                child.mutateFromNormal(mutationVariability,clusterScale);
            }
            res.add(child);
        }
        return res;
    }

    public void run() {
        List<Individual> global = new LinkedList<Individual>();
        global.addAll(population);
        List<Cluster> clusters = new LinkedList<Cluster>();
        while (true) {
            
            int individualPosition = 0;
            clusters.addAll(agglomerativeClustering(global));
            
            
            //remove clusters with dimension 1
            for (Iterator<Cluster> iterator = clusters.iterator(); iterator.hasNext();) {
                Cluster c = iterator.next();
                //purging of the population surplus 
                c.purge();
                if (c.components.size() < 3) {
                    //global.addAll(c.components);
                    iterator.remove();
                }
                else
                    //remove the final components of the cluster from the global population
                    global.removeAll(c.components);
            }
            
            
            List<Individual> offspringCluster = new LinkedList<Individual>();

            //System.out.println(population.size());
            //breeding within the clusters
            for (Cluster c : clusters) 
                offspringCluster.addAll(reproduceCluster(c, c.getDynamicPopSize(), individualPosition));
            
            //reproduction of the individuals that do not belog to any cluster
 
            offspringCluster.addAll(reproduceList(global, 0, individualPosition));

            if (offspringCluster.size() != populationSize) 
                offspringCluster.addAll(reproduceList(population, populationSize - offspringCluster.size(), individualPosition));

            population = (LinkedList<Individual>) offspringCluster;
            global = new LinkedList<Individual>();
            global.addAll(offspringCluster);
            
            //classification of the offspring, trying to add them to one cluster
            for (Cluster c : clusters) {
                List<Individual> newGen = new LinkedList<Individual>();
                for (Individual i : offspringCluster) 
                    if (c.contains(i))
                        newGen.add(i);
                global.removeAll(newGen);
                c.newGeneration(newGen);
            }
        }
    }

}
