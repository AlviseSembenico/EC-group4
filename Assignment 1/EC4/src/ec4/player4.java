package ec4;

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
    private int populationSize = 100;
    private static final int F_DIMENSIONS = 10;
    private LinkedList<Cluster> clusters;
    private Cluster outsideCluster;
    private double selectivePressure = 1.8;
    private int tournamentSize = 10;
    private double mutationRate = 0.1;
    private double mutationVariability = 0.8;
    private int crossoverPoints = 2;
    private boolean ageing = false;
    private int elitismElements = 0;
    private double ageingFactor = 0.3;
    private double clusterRadius = 5.0;

    /**
     * Initialize the popoulation randomly
     */
    private void populationInitialization() {
        clusters = new LinkedList<Cluster>();
        outsideCluster = new Cluster();
        for (int j = 0; j < populationSize; j++) {
            outsideCluster.addIndividual(new Individual(j));
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
        populationInitialization();
        setParameters();
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
    public static Individual crossover(List<Individual> parents, int numPoints, int position) {
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

    /**
     * @param n   # of random number generated
     * @param min min for every number
     * @param max max for every number
     * @return n integer random number between min and max
     */
    public static int[] random(int n, int min, int max) {
        int[] res = new int[n];
        int[] i = new int[]{0};
        rnd_.ints(n, min, max).forEach(rn -> {
            res[i[0]++] = rn;
        });
        return res;
    }

    private void checkClusters() {
        for (Cluster c : clusters) {
            for (Individual i : outsideCluster.components) {
                if (c.averageDistance(i) < clusterRadius) {
                    c.addIndividual(i);
                    outsideCluster.removeIndividual(i);
                    System.out.println("added individual to cluster");
                    break;
                }
            }
        }

        outerloop:
        for (Individual i : outsideCluster.components) {
            for (Individual j : outsideCluster.components) {
                if (i.distance(j) < clusterRadius) {
                    Cluster c = new Cluster();
                    c.addIndividual(i);
                    c.addIndividual(j);
                    outsideCluster.removeIndividual(i);
                    outsideCluster.removeIndividual(j);
                    clusters.add(c);
                    System.out.println("created new cluster from two individuals");
                    break outerloop;
                }
            }
        }

        Cluster fc1 = null, fc2 = null;
        for (Cluster c1 : clusters) {
            for (Cluster c2 : clusters) {
                if (c1 != c2 && c1.averageDistance(c2) < clusterRadius) {
                    fc1 = c1;
                    fc2 = c2;
                }
            }
        }
        if (fc1 != null) {
            fc1.merge(fc2);
            clusters.remove(fc2);
            System.out.println("merged two clusters: " + clusters.size() + " clusters now");
        } else {
            System.out.println("nothing merged: " + clusters.size() + " clusters");
        }
    }

    public void run() {
        // Run your algorithm here
        int evals = 0;
        while (true) {
            outsideCluster.iterate();
            checkClusters();
            for (Cluster c : clusters) {
                c.iterate();
            }
        }
    }

}
