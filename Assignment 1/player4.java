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
    private final int populationSize = 20;
    private final int F_DIMENSIONS = 10;
    private LinkedList<double[]> population;
    private double selectivePressure = 1.5;
    private int tournamentSize = 5;
    private double mutationRate=0.1;

    private void populationInitialization() {
        population = new LinkedList<double[]>();
        for (int j = 0; j < populationSize; j++) {
            double child[] = new double[F_DIMENSIONS];
            for (int i = 0; i < F_DIMENSIONS; i++)
                child[i] = rnd_.nextDouble() * 10 - 5;
            population.add(child);
        }
    }

    public player0() {
        rnd_ = new Random();
        populationInitialization();
    }

    private void mutateChild(double premuChild[]) {
        for (int i = 0; i < premuChild.length; i++) {
            double coinFlip = rnd_.nextDouble();
            if (coinFlip > 0.5) {
                premuChild[i] = (rnd_.nextDouble() * 10) - 5;
            }
        }
    }

    /**
     * Crossover with crossover point in the middle
     */
    private double[] crossover(double[] a, double[] b) {
        return new double[] { a[0], a[1], a[2], a[3], b[4], b[5], b[6], b[7] };
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
    /*
     * private double[] computeFitness(LinkedList<double[]> population) { double[]
     * fitness = new double[population.size()]; for (int i = 0; i <
     * population.size(); i++) { double[] child = population.get(i); fitness[i] =
     * 1.0; fitness[i] = (double) evaluation.evaluate(child); } return fitness; }
     */

    private Wrapper[] computeFitness() {
        Wrapper[] fitness = new Wrapper[population.size()];
        int j = 0;
        population.forEach(child -> {
            fitness[j++] = new Wrapper(child, (double) evaluation.evaluate(child));
        });
        return fitness;
    }

    private double sumFitness(double[] fitness) {
        double total = 0.0;
        for (double childFitness : fitness) {
            total += childFitness;
        }
        return total;
    }

    private double linearRanking(int position) {
        int u = population.size();
        return ((2 - selectivePressure) / u) + ((2 * position * (selectivePressure - 1)) / (u * (u - 1)));
    }

    // using Wrapper class
    private double sumFitness(Wrapper[] popEval) {
        double totFitness = 0.0;
        for (Wrapper child : popEval)
            totFitness += (double) child.c;
        return totFitness;
    }

    private int[] random(int n, int min, int max) {
        int[] res = new int[n];
        Random random = new Random();
        int i = 0;
        random.ints(n, min, max).forEach(rn -> {
            res[i++] = rn;
        });
        return res;
    }

    public void run() {
        // Run your algorithm here
        int evals = 0;
        while (evals++ < 1000) {
            // calculate fitness
            Wrapper[] popFitness = computeFitness();
            double totalFitness = sumFitness(popFitness);

            
            for (int counter=0;counter<populationSize;counter++) {
                //select randomly the index of 5 parents
                int[] parents = random(tournamentSize, 0, population.size());
                //calculate the probability for every parent to be chosen, the sum is 1.0
                double[] probability = new double[tournamentSize];
                for (int i : parents) {
                    probability[i] = linearRanking(i);
                }
                Wrapper p1, p2;
                double amount = 0.0;
                //randomize a number between 0 and 1
                double extract = Math.random();
                int i = 0;
                //select the parents according to the random number and the probability of the parents
                for (double p : probability)
                    if (extract < amount) {
                        p1 = (Wrapper)popFitness[parents[i]].t;
                        break;
                    } else {
                        amount += p;
                        i++;
                    }
                //repeat the procedure for the second parent
                extract = Math.random();
                i = 0;
                for (double p : probability)
                    if (extract < amount) {
                        p2 = (Wrapper)popFitness[parents[i]].t;
                        break;
                    } else {
                        amount += p;
                        i++;
                    }

                //generation of the new child from the parents
                double[] newChild=crossover((double[])p1.t,(double[])p1.t);
                //apply the mutation if it necessary
                if(Math.random()<mutationRate)
                    mutateChild(newChild);
                //add the new child to the population
                population.add(newChild);
            }

            //population selection, half of the individuals must be killed

            //compute the fitness mean




            // Apply crossover / mutation operators
            /*mutateChild(parent);

            // Check fitness of unknown fuction
            double[] newFitness = computeFitness();
            double newTotalFitness = sumFitness(newFitness);

            System.out.println("evaluation: " + evals + ". New fitness: " + newTotalFitness);
            if (newTotalFitness < totalFitness) {
                population.set(evals % population.size(), bParent);

                newFitness = computeFitness();
                newTotalFitness = sumFitness(newFitness);

                System.out.println("returned parent to old value because new mutation lowered fitness. New fitness: "
                        + newTotalFitness);
            }
            fitness = newFitness;
            */
        }
    }
}