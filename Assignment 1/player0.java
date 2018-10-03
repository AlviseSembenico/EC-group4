import org.vu.contest.ContestSubmission;
import org.vu.contest.ContestEvaluation;
import java.util.LinkedList;

import java.util.Random;
import java.util.Properties;
import java.util.Arrays;


public class player0 implements ContestSubmission {

    Random rnd_;
    ContestEvaluation evaluation;
    private int evaluations_limit_;
    // Population size
    private final int INITIAL_P_SIZE = 20;
    private final int F_DIMENSIONS = 10;
    private LinkedList<double[]> population;

    public void populationInitialization(){
        population = new LinkedList<double[]>();
        for (int j = 0; j < INITIAL_P_SIZE; j++) {
            double child[] = new double[F_DIMENSIONS];
            for(int i = 0;i < F_DIMENSIONS; i++)
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

    /**
    * Crossover with crossover point in the middle
    */
    private double[] crossOver(double[] a, double[] b) {
        return new double[]{a[0], a[1], a[2], a[3], b[4], b[5], b[6], b[7]};
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

    private double[] computeFitness(LinkedList<double[]> population) {
        double[] fitness = new double[population.size()];
        for (int i = 0; i < population.size(); i++) {
            double[] child = population.get(i);
            fitness[i] = 1.0;
            fitness[i] = (double) evaluation.evaluate(child);
        }
        return fitness;
    }

    private double sumFitness(double[] fitness) {
        double total = 0.0;
        for (double childFitness : fitness) {
            total += childFitness;
        }
        return total;
    }

    public void run() {
        // Run your algorithm here
        int evals = 0;
        // initFitness
        double[] fitness = computeFitness(population);
        System.out.println(fitness);
        // calculate fitness
        while (evals++ < 1000) {
            double totalFitness = sumFitness(fitness);
            // Select parents
            double[] parent = population.get(evals % population.size());
            double[] bParent = parent.clone();

            // Apply crossover / mutation operators
            mutateChild(parent);

            // Check fitness of unknown fuction
            double[] newFitness = computeFitness(population);
            double newTotalFitness = sumFitness(newFitness);

            System.out.println("evaluation: " + evals + ". New fitness: " + newTotalFitness);
            if (newTotalFitness < totalFitness) {
                population.set(evals % population.size(), bParent);

                newFitness = computeFitness(population);
                newTotalFitness = sumFitness(newFitness);

                System.out.println("returned parent to old value because new mutation lowered fitness. New fitness: " + newTotalFitness);
            }
            fitness = newFitness;
        }
    }
}
