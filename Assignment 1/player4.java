import org.vu.contest.ContestSubmission;
import org.vu.contest.ContestEvaluation;

import java.util.LinkedList;
import java.util.Random;
import java.util.Properties;
import java.util.Arrays;
import java.util.List;
import java.util.Comparator;
import java.util.Collections;
import java.util.Iterator;
import java.util.stream.*;


public class player4 implements ContestSubmission {
    public static Random rnd_;
    public static ContestEvaluation evaluation;
    private int evaluations_limit_;
    // Population size
    private final int populationSize = 20;
    private final int F_DIMENSIONS = 10;
    private LinkedList<Individual> population;
    private double selectivePressure = 1.5;
    private int tournamentSize = 5;
    private double mutationRate = 0.1;
    private double mutationVariability = 0.5;
    private int hardElitismN = 1;
    private int tot=0;


    /**
     * Initialize the popoulation randomly
     */
    private void populationInitialization() {
        population = new LinkedList<Individual>();
        for (int j = 0; j < populationSize; j++) {
            population.add(new Individual());
        }
    }

    public player4() {
        rnd_ = new Random();
        populationInitialization();
    }

    //same as before
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

    /**
     * 
     * @param wrappers larray of Wrapper containing the population
     * @return Returns the top hardElitismN individuals directly to next generation (array of hardElitismN indivs)
     */
    public List<Individual> hardElitism(List<Individual> individuals) {
        Collections.sort(individuals);
        List<Individual> eliteIndiv = new LinkedList<Individual>();
        for (int i = 0; i < hardElitismN; i++) {
            eliteIndiv.add(individuals.get(i));
        }
        return eliteIndiv;
    }

    /**t
     * Crossover with crossover point in the middle
     */

    private double[] crossover(List<Individual> toBreed, int numPoints){
        // Using 3 parents, make a 3 point crossover
        // Choose the 3 points
        List<Integer> crossovers = new LinkedList<Integer>();
        int randomparent;
        int randomcrossover;
        for (int i = 0; i < numPoints; i++) {
            randomcrossover = rnd_.nextInt(10 + 1);
            if(!crossovers.contains(randomcrossover))
                crossovers.add(randomcrossover);
            else{
                i--;
            }
        }

        Collections.sort(crossovers);
        int prevPoint = 0;
        int currentPoint;
        int z = 0;
        Individual newGuy = new Individual();
        double[] newChild = new double[10];

        for (int k = toBreed.size() + 1; k > 0; k--) {
            randomparent = rnd_.nextInt(toBreed.size() + 1 - 0);
            currentPoint =  crossovers.get(z);
            for (int j = prevPoint; j < currentPoint; j++) {
                newChild[j] = toBreed.get(k).points[j];
            }
            prevPoint = currentPoint;
            z++;
        }
        if(crossovers.get(crossovers.size()-1) < 10){
            for (int j = prevPoint; j < 10; j++) {
                newChild[j] = toBreed.get(0).points[j];
            }
        }
        return newChild;
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
    private double linearRanking(int position,int u) {
        return ((2 - selectivePressure) / u) + ((2 * position * (selectivePressure - 1)) / (u * (u - 1)));
    }

    /**
     * 
     * @param n # of random number generated
     * @param min min for every number
     * @param max max for every number
     * @return  n integer random number between min and max
     */
    private int[] random(int n, int min, int max) {
        int[] res = new int[n];
        int[] i = new int[]{0};
        rnd_.ints(n, min, max).forEach(rn -> {
            res[i[0]++] = rn;
        });
        return res;
    }

    private List<Individual> tournament(int tournamentSize, int winners, int rounds) {
        List<Individual> res = new LinkedList<Individual>();
        List<Individual> candidates = (List) population.clone();

        for (int r = 0; r < rounds; r++) {
            // select randomly the index of 5 parents
            List<Integer> parents = IntStream.of(random(tournamentSize, 0, candidates.size() - 1)).boxed()
                    .collect(Collectors.toList());

            List<Individual> tournament = new LinkedList<Individual>();
            Collections.sort(parents);

            // add all selected parents to tournament
            Iterator can = candidates.iterator(), p = parents.iterator();
            int iteration = 0;
            while (can.hasNext() && p.hasNext()) {
                if (iteration == (Integer) p.next())
                    tournament.add((Individual) can.next());
                else
                    can.next();
                iteration++;
            }

            for (int w = 0; w < winners; w++) {
                // calculate the total fitness of the tournament
                double totalFitness = 0;
                for (Individual candidate : tournament)
                    totalFitness += candidate.getFitness();

                // calculate the probability for every parent to be chosen, the sum is 1.0
                double[] probability = new double[tournamentSize];
                iteration = 0;
                for (Individual candidate : tournament)
                    probability[iteration++] = candidate.getFitness() / totalFitness;

                double amount = probability[0];
                // randomize a number between 0 and 1
                double extract = Math.random();
                for (int i = 1; i <= probability.length; i++)
                    if (extract <= amount) {
                        Individual winner = tournament.get(i);
                        // add the winner to the outcome
                        res.add(winner);
                        // remove the winner for next round within the same tournament
                        tournament.remove(winner);
                        //remove also to general candidates
                        candidates.remove(winner);
                        break;
                    } else {
                        amount += probability[i];
                    }
            }
        }
        return res;
    }


    public void run() {
        // Run your algorithm here
        int evals = 0;
        while (evals++*populationSize < 10000) {
        }
    }
}
