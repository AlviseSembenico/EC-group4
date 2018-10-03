import org.vu.contest.ContestSubmission;
import org.vu.contest.ContestEvaluation;
import java.util.LinkedList;

import org.apache.commons.math3.*;

import java.util.Random;
import java.util.Properties;


public class Player4 implements ContestSubmission {

    Random rnd_;
    ContestEvaluation evaluation_;
    private int evaluations_limit_;
    private int populationSize = 100;
    private LinkedList<double[]> population;
    private int functionDimension=10;
    private double mutationProbability=0.1;


    private void popolationInitialization(){
        Random rand = new Random();
        population=new LinkedList<double[]>();
        for (int j = 0; j < populationSize; j++) {
            double child[] = new double[functionDimension];
            for(int i=0;i<functionDimension;i++)
                child[i]=rand.nextDouble()*10-5;
            population.add(child);
        }
    }

    public Player4() {
        rnd_ = new Random();
        popolationInitialization();
    }

    public void setSeed(long seed) {
        // Set seed of algortihms random process
        rnd_.setSeed(seed);
    }

    public void setEvaluation(ContestEvaluation evaluation) {
        // Set evaluation problem used in the run
        evaluation_ = evaluation;

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

    private Wrapper[] computeFitness() {
        Wrapper[] fitness = new Wrapper[populationSize];
        for (int i=0;i<population.size();i++) {
            double[] child=population.get(i);
            fitness[i]=new Wrapper((double) evaluation_.evaluate(child),child); 
        }
        return fitness;
    }

    public void run() {
        // Run your algorithm here
        int evals = 0;
        // initFitness
        Wrapper[] fitness;
        population = new LinkedList<>();
        for (int j = 0; j < populationSize; j++) {
            double child[] = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
            population.add(child);
        }
        fitness = computeFitness();
        // calculate fitness
        while (evals < 1) {
            // Select parents
            // Apply crossover / mutation operators
            // double child[] = {0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};
            // Check fitness of unknown fuction
            //double fitnesfs = (double) evaluation_.evaluate(child);        
            evals++;

            //System.out.println(fitness);
            // Select survivors
        }

    }
}