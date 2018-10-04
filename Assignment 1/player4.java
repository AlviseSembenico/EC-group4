import org.vu.contest.ContestSubmission;
import org.vu.contest.ContestEvaluation;
import java.util.LinkedList;

import java.util.Random;
import java.util.Properties;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Collections;


public class player4 implements ContestSubmission {

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
    private double mutationVariability = 0.5;
    private int hardElitismN = 1;

    private void populationInitialization() {
        population = new LinkedList<double[]>();
        for (int j = 0; j < populationSize; j++) {
            double child[] = new double[F_DIMENSIONS];
            for (int i = 0; i < F_DIMENSIONS; i++)
                child[i] = rnd_.nextDouble() * 10 - 5;
            population.add(child);
        }
    }

    public player4() {
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

    private void mutateChild2(double premuChild[]){
        // Mutation that will move shortly in the 10D space (Low variation)
        double p1 = 0.8 * mutationVariability + 0.1; //p1 from 0.1 to 0.9
        // Move some random values a random % distance towards one of the sides (+ or -)
        for (int i = 0; i < premuChild.length; i++) {
            double coinFlip = rnd_.nextDouble();
            boolean movePositive = coinFlip > 0.5;
            coinFlip = rnd_.nextDouble();
            if (coinFlip < p1) {
                double maxDistance = 0.0;
                if (movePositive) {
                    maxDistance = 5.0 - premuChild[i];
                } else{
                    maxDistance = -Math.abs(-5.0 - premuChild[i]);
                }

                premuChild[i] = premuChild[i] + (rnd_.nextDouble() * maxDistance);
            }
        }
    }

    public Wrapper[] hardElitism(Wrapper[] wrappers) {
        // Returns the top hardElitismN individuals directly to next generation (array of hardElitismN indivs)
        Arrays.sort(wrappers);
        Wrapper[] eliteIndiv = new Wrapper[hardElitismN];
        for (int i = 0; i < hardElitismN; i++) {
            eliteIndiv[i] = wrappers[i];
        }
        return eliteIndiv;
    }

    /**
     * Crossover with crossover point in the middle
     */
    private double[] crossover(double[] a, double[] b) {
        return new double[] { a[0], a[1], a[2], a[3], a[4], b[5], b[6], b[7], b[8], b[9]};
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
        int[] j = new int[]{0};
        population.forEach(child -> {
            fitness[j[0]++] = new Wrapper(child, (double) evaluation.evaluate(child),false);
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

    private double linearRanking(int position,int u) {
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
        System.out.println("max value: "+max);
        int[] res = new int[n];
        Random random = new Random();
        int[] i = new int[]{0};
        random.ints(n, min, max).forEach(rn -> {
            res[i[0]++] = rn;
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
                int[] parents = random(tournamentSize, 0, populationSize-1);
                //calculate the probability for every parent to be chosen, the sum is 1.0
                double[] probability = new double[tournamentSize];
                for (int i=0;i<tournamentSize;i++) {
                    probability[i] = linearRanking(i,tournamentSize);
                }
                Wrapper p1=null, p2=null;
                double amount = probability[0];
                //randomize a number between 0 and 1
                double extract = Math.random(); 
            
                System.out.println(Arrays.toString(probability));
                System.out.println(Arrays.toString(parents));
                System.out.println(popFitness.length);
                System.out.println(extract);
                
                //select the parents according to the random number and the probability of the parents
                for(int i=1;i<=probability.length;i++)
                    if (extract <= amount) {
                        p1 = popFitness[parents[i-1]];
                        break;
                    } else {
                        amount += probability[i];
                    }
                //repeat the procedure for the second parent
                extract = Math.random();
                
                System.out.println(extract);
                amount = probability[0];
                for(int i=1;i<=probability.length;i++)
                    if (extract <= amount) {
                        p2 = popFitness[parents[i-1]];
                        break;
                    } else {
                        amount += probability[i];
                    }
                System.out.println("parent 1 "+p1);
                System.out.println("parent 2 "+p2);
                //generation of the new child from the parents
                double[] newChild=crossover((double[])p1.t,(double[])p2.t);
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
