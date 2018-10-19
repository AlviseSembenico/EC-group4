package ec4;

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
import java.util.ArrayList;
import java.util.Properties;
import org.jfree.ui.RefineryUtilities;

public class player4 implements ContestSubmission {

    public static Random rnd_;
    public static ContestEvaluation evaluation;
    private int evaluations_limit_= 1000000;
    // Population size
    private int populationSize;
    public static double[][] distantMatrix;
    public static int individualPosition=0;
    private final int F_DIMENSIONS = 10;
    private LinkedList<Individual> population;
    private int tournamentSize = 7;
    private double mutationRate = 0.4;
    private double mutationVariability = 0.8;
    private boolean ageing = false;
    private int elitismElements = 0;
    private double ageingFactor = 0.3;
    private final double clusterRadius = 1;
    private final double arithmeticCrossoverStep=0.5;
    public static LineChart demo;
    public static String imageName ;
    
    /**
     * Initialize the popoulation randomly
     */
    private void populationInitialization() {
        population = new LinkedList<Individual>();
        for (int j = 0; j < populationSize; j++) {
            population.add(new Individual());
        }
    }

    private void setParameters() {

        Properties prop = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream("src/properties.txt");
            // load a properties file
            prop.load(input);
            // get the property value and print it out
            populationSize = Integer.valueOf(prop.getProperty("populationSize"));
            tournamentSize = Integer.valueOf(prop.getProperty("tournamentSize"));
            mutationRate = Double.valueOf(prop.getProperty("mutationRate"));
            mutationVariability = Double.valueOf(prop.getProperty("mutationVariability"));
            elitismElements = Integer.valueOf(prop.getProperty("elitismElements"));
            ageing = Boolean.valueOf(prop.getProperty("ageing"));
            ageingFactor = Double.valueOf(prop.getProperty("ageingFactor"));
            evaluations_limit_ = Integer.valueOf(prop.getProperty("nExec"));
            imageName = prop.getProperty("imageName");
            Cluster.generationBound=Integer.valueOf(prop.getProperty("generationBound"));
            Cluster.discardBound=Integer.valueOf(prop.getProperty("discardBound"));
            Cluster.clusterDistance=Double.valueOf(prop.getProperty("clusterDistance"));
            Cluster.alphaStepSize=Integer.valueOf(prop.getProperty("alphaStepSize"));
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
        demo = new LineChart("Dynamic Data Demo");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
        
        rnd_ = new Random();
        setParameters();
        Individual.nDimension = F_DIMENSIONS;
        Individual.nEval=evaluations_limit_;
        populationInitialization();
    }

    @Override
    public void setSeed(long seed) {
        // Set seed of algortihms random process
        rnd_.setSeed(seed);
    }

    @Override
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

    /**
     *
     * @param n # of random number generated
     * @param min min for every number
     * @param max max for every number
     * @return n integer random number between min and max
     */
    private static int[] random(int n, int min, int max) {
        if(n>max-min)
            n=max-min;

        List<Integer> solution = new ArrayList<>();
        int[] res = new int[n];
        for(int i=min;i<=max;i++)
            solution.add(i);
        Collections.shuffle(solution);
        //solution.subList(0, n-1);
        for(int i=0;i<n;i++)
            res[i]=solution.get(i);
        return res;
        //return solution.toArray(new Integer[0]);
        //return solution.stream().toArray(Integer[]::new);
//        int[] res = new int[n];
//        int[] i = new int[]{0};
//        rnd_.ints(n, min, max).forEach(rn -> {
//            res[i[0]++] = rn;
//        });
//        return res;
    }

    private List<Individual> tournament(LinkedList<Individual> source, int tournamentSize, int winners, int rounds) {
        List<Individual> res = new LinkedList<Individual>();
        List<Individual> candidates = (List) source.clone();

        for (int r = 0; r < rounds; r++) {
            // select randomly the index of tS parents
            List<Integer> parents = IntStream.of(random(tournamentSize, 0, candidates.size() - 1)).boxed()
                    .collect(Collectors.toList());

            HashMap<Individual, Double> tournament = new HashMap<Individual, Double>();
            Collections.sort(parents);

            // add all selected parents to tournament
            parents.forEach(p -> {
                tournament.put(candidates.get(p), 0.0);
            });

            //System.out.println(tournament.keySet().size());
            for (int w = 0; w < winners; w++) {
                // calculate the total fitness of the tournament
                double totalFitness = 0;
                for (Individual candidate : tournament.keySet())
                    totalFitness += candidate.getFitness();
                
                if (totalFitness == 0) {
                    for (int c = 0; c <= winners * rounds - res.size(); c++) 
                        res.add(candidates.get(c));
                    return res;
                }
                // calculate the probability for every parent to be chosen, the sum is 1.0
                for (Individual candidate : tournament.keySet()) {
                    tournament.replace(candidate, candidate.getFitness() / totalFitness);
                }

                double amount = 0.0;
                // randomize a number between 0 and 1
                double extract = rnd_.nextDouble();
                
                //System.out.println(tournament);
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

    private double[][] distanceMatrix() {
        double[][] matrix = new double[populationSize][populationSize];
        Iterator<Individual> c1 = population.iterator();
        while (c1.hasNext()) {
            Individual ch1 = c1.next();
            Iterator<Individual> c2 = population.iterator();
            while (c2.hasNext()) {
                Individual ch2 = c2.next();
                matrix[ch1.position][ch2.position] = ch1.computeDistance(ch2);
            }

        }
        distantMatrix=matrix;
        return matrix;
    }

    private List<Cluster> agglomerativeClustering(List<Individual> list) {
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
    
    private List<Individual> reproduceList(List<Individual> l, int children){
        return reproduceList(l, children,1);
    }
    
    private List<Individual> reproduceCluster(Cluster c, int children){
        return reproduceList(c.components, children, c.getAlphaDynamicStepSize());
    }

    private double getFittest(){
        double max=0;
        for(Individual i:population)
            if(i.getFitness()>max)
                max=i.getFitness();
        return max;
    }
    
    private List<Individual> reproduceList(List<Individual> l, int children,double clusterScale) {
        if (children == 0 || children > l.size()) 
            children = l.size();
        
        List<Individual> copy=new LinkedList<Individual>();
        copy.addAll(l);
        for(int i=3-l.size();i>0;i--)
            copy.add(population.get(rnd_.nextInt(population.size()-1)));

        List<Individual> res = new LinkedList<Individual>();
        for (int i = 0; i < children; i++) {

            List<Individual> parents = tournament((LinkedList<Individual>) copy, tournamentSize, 2, 1);
            Individual child =null;
            
            if(parents.size()<2)
                parents = tournament((LinkedList<Individual>) copy, tournamentSize, 2, 1);
            child = parents.get(0).arithmeticCrossover(parents.get(1), arithmeticCrossoverStep);
            if (rnd_.nextDouble() < mutationRate) {
                child.mutateFromNormal(mutationVariability,clusterScale);
            }
            res.add(child);
        }
        return res;
    }

    @Override
    public void run() {
        List<Individual> global = new LinkedList<Individual>();
        global.addAll(population);
        List<Cluster> clusters = new LinkedList<Cluster>();
        
        while (true) {
            demo.addData(getFittest());
            distanceMatrix();
            individualPosition = 0;
            slideWindow();
            clusters.addAll(agglomerativeClustering(global));
                        
            //remove clusters with dimension < 3
            for (Iterator<Cluster> iterator = clusters.iterator(); iterator.hasNext();) {
                Cluster c = iterator.next();
                //purgeing of the population surplus 
                if (c.components.size() < 3 ) 
                    iterator.remove();
                else if(c.nonProductive()){
                    //check if the cluster has already analized the area for a while and has not discover good points
                    Cluster.discartedCentroid.add(c.gravityCenter());
                    iterator.remove();
                }
                else{
                    //remove the final components of the cluster from the global population
                    global.removeAll(c.components);
                }                    
            }
            List<Individual> offspringCluster = new LinkedList<Individual>();
            //breeding within the clusters
            for (Cluster c : clusters) 
                offspringCluster.addAll(reproduceCluster(c, c.getDynamicPopSize()));
            
            
            //reproduction of the individuals that do not belog to any cluster
            if(elitismElements!=0){
                Collections.sort(population);
                offspringCluster.addAll(population.subList(0, elitismElements));
            }
            
            offspringCluster.addAll(reproduceList(global, global.size()-elitismElements));

            if (offspringCluster.size() != populationSize) 
                offspringCluster.addAll(reproduceList(population, populationSize - offspringCluster.size()));

            population = (LinkedList<Individual>) offspringCluster;
            global = (List<Individual>) population.clone(); 
            //classification of the offspring, trying to add them to one cluster
       
            for (Iterator<Cluster> iterator = clusters.iterator(); iterator.hasNext();) {
                Cluster c=iterator.next();
                List<Individual> newGen = new LinkedList<Individual>();
                for (Individual i : global) 
                    if (c.contains(i))
                        newGen.add(i);
                if(newGen.isEmpty())
                    iterator.remove();
                 
                else{
                    c.newGeneration(newGen);
                    global.removeAll(newGen);
                }
            }
        }
    }
}
