import org.vu.contest.ContestSubmission;
import org.vu.contest.ContestEvaluation;

import java.util.LinkedList;
import java.util.Random;
import java.util.Properties;
import java.util.Arrays;
import java.util.List;
import java.util.Comparator;
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
    private int evaluations_limit_;
    // Population size
    private final int populationSize = 100;
    private final int F_DIMENSIONS = 10;
    private LinkedList<Individual> population;
    private double selectivePressure = 1.8;
    private int tournamentSize = 10;
    private double mutationRate = 0.1;
    private double mutationVariability = 0.8;
    private int hardElitismN = 1;
    private int crossoverPoints = 2;
    private int elitismElements = 10;

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

        Properties prop = new Properties();
        InputStream input = null;

        try {

            input = new FileInputStream("configuration.txt");

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            System.out.println(prop.getProperty("prova"));

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
    private Individual crossover(List<Individual> parents, int numPoints) {
        // Using 3 parents, make a 3 point crossover
        // Choose the 3 points
        List<Integer> crossovers = new LinkedList<Integer>();
        int randomCrossover;
        for (int i = 0; i < numPoints; i++) {
            randomCrossover = rnd_.nextInt(F_DIMENSIONS - 1);
            if (!crossovers.contains(randomCrossover))
                crossovers.add(randomCrossover);
            else {
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
                if (!p.hasNext())
                    p = parents.iterator();
                currentParent = (Individual) p.next();

                if (!c.hasNext())
                    point = -1;
            }
            newChild[i] = currentParent.points[i];
        }
        return new Individual(newChild);
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
     * @param u        the dimension of the tournament
     * @return the probability for the parent to be chosen
     */
    private double linearRanking(int position, int u) {
        return ((2 - selectivePressure) / u) + ((2 * position * (selectivePressure - 1)) / (u * (u - 1)));
    }

    /**
     * 
     * @param n   # of random number generated
     * @param min min for every number
     * @param max max for every number
     * @return n integer random number between min and max
     */
    private int[] random(int n, int min, int max) {
        int[] res = new int[n];
        int[] i = new int[] { 0 };
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
                for (Individual candidate : tournament.keySet())
                    totalFitness += candidate.getFitness();
                if (totalFitness == 0)
                    for (int c = 0; c < winners * rounds - res.size(); c++)
                        res.add(candidates.get(c));

                // calculate the probability for every parent to be chosen, the sum is 1.0
                iteration = 0;
                for (Individual candidate : tournament.keySet())
                    tournament.replace(candidate, candidate.getFitness() / totalFitness);

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

    private double distance(Individual ch1, Individual ch2) {
        double res = 0.0;
        for (int i = 0; i < ch1.points.length; i++)
            res += Math.pow(ch1.points[i] + ch2.points[i], 2);
        return Math.sqrt(res);
    }

    private List<Individual> topIndividual(int n) {
        List<Individual> top = new LinkedList<Individual>();
        Collections.sort(population);
        for (n--; n >= 0; n--)
            top.add(population.get(n));
        return top;
    }

    public void run() {
        // Run your algorithm here
        int evals = 0;
        while (true) {
            List<Individual> offspring = new LinkedList<Individual>();
            for (int i = 0; i < populationSize; i++) {
                List<Individual> parents = tournament(tournamentSize, 3, 1);
                Individual child = crossover(parents, crossoverPoints);
                if (rnd_.nextDouble() < mutationRate)
                    child.mutate(mutationVariability);
                offspring.add(child);
            }

            for (Individual c : offspring)
                population.add(c);
            List<Individual> tmp = topIndividual(elitismElements);

            population = (LinkedList<Individual>) tournament(tournamentSize, 1, populationSize - elitismElements);
            // System.out.println("size of tournament"+population.size());
            population.addAll(tmp);

        }

    }
}
