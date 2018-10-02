import org.vu.contest.ContestSubmission;
import org.vu.contest.ContestEvaluation;
import java.util.LinkedList;

import java.util.Random;
import java.util.Properties;


public class player0 implements ContestSubmission {

    Random rnd_;
    ContestEvaluation evaluation_;
    private int evaluations_limit_;
    private int populationSize = 100;
    LinkedList<double[]> population;
    private int functionDimension=10;

    public void populationInitialization(){
        for (int j = 0; j < populationSize; j++) {
            double child[] = new double[functionDimension];
            for(int i = 0;i < functionDimension; i++)
                child[i] = rnd_.nextDouble() * 10 - 5;
            population.add(child);
        }
    }

    public player0() {
        rnd_ = new Random();
        populationInitialization();
    }

    private void mutateChild(double premuChild[]) {
        for(int i = 0; i < premuChild.length; i++) {
            double coinFlip = rnd_.nextDouble();
            if(coinFlip > 0.5){
                premuChild[i] = (rnd_.nextDouble() * 10) - 5;
            }
        }
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

    private Wrapper[] computeFitness(LinkedList<double[]> population) {
        Wrapper[] fitness = new Wrapper[populationSize];
        for (int i = 0; i < population.size(); i++) {
            double[] child = population.get(i);
            fitness[i] = new Wrapper((double) evaluation_.evaluate(child), child); 
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
        fitness = computeFitness(population);
        System.out.println(fitness);
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
