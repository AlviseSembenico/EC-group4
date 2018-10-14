/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec4;

import org.omg.PortableInterceptor.INACTIVE;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author alvis
 */
public class Cluster {
    public LinkedList<Individual> components;
    public List<Double> fitnessHistory;

    public Cluster() {
        components = new LinkedList<Individual>();
        fitnessHistory = new LinkedList<Double>();
    }

    public Cluster(LinkedList<Individual> l) {
        components = l;
    }

    public Cluster(Individual i) {
        this();
        components.add(i);
    }

    public double fitnessMean() {
        double mean = 0;
        for (Individual i : components)
            mean += i.getFitness();

        mean /= components.size();
        fitnessHistory.add(mean);
        return mean;
    }

    public double fitnessVariance() {
        double tot = 0;
        double mean = fitnessMean();
        for (Individual i : components)
            tot += Math.pow(i.getFitness() - mean, 2);

        tot /= components.size();
        tot = Math.sqrt(mean);
        return tot;
    }

    public double radius() {
        double max = 0;
        for (Individual i : components)
            for (Individual j : components) {
                double dist = i.distance(j);
                if (dist > max)
                    max = dist;
            }
        return max;
    }

    public double maxDistance(Cluster cl) {
        if (this == cl)
            return 0;
        double max = 0;
        for (Individual i : components)
            for (Individual j : cl.components) {
                double dist = i.distance(j);
                if (dist > max)
                    max = dist;
            }
        if (components.size() + cl.components.size() == 0)
            return 0;
        return max;
    }

    public double averageDistance(Individual i) {
        double tot = 0;
        for (Individual j : components)
            tot += j.distance(i);
        return tot / components.size();
    }

    public double averageDistance(Cluster cl) {
        if (this == cl)
            return 0;
        double tot = 0;
        for (Individual i : components)
            for (Individual j : cl.components)
                tot += i.distance(j);
        if (components.size() + cl.components.size() == 0)
            return 0;
        return tot / (components.size() + cl.components.size());
    }

    public void addIndividual(Individual i) {
        components.add(i);
    }

    public void merge(Cluster c) {
        for (Individual i : c.components) {
            components.add(i);
        }
    }

    private List<Individual> tournament(int tournamentSize, int winners, int rounds) {
        List<Individual> res = new LinkedList<Individual>();
        List<Individual> candidates = (List<Individual>) components.clone();

        for (int r = 0; r < rounds; r++) {
            // select randomly the index of 5 parents
            List<Integer> parents = IntStream.of(player4.random(tournamentSize, 0, candidates.size() - 1)).boxed()
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
                double extract = player4.rnd_.nextDouble();
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
        Collections.sort(components);
        for (n--; n >= 0; n--) {
            top.add(components.get(n));
        }
        return top;
    }

    private void slideWindow() {
        double minFitness = 0.0;
        for (Individual ind : components) {
            if (ind.getFitness() < minFitness) {
                minFitness = ind.getFitness();
            }
        }
        for (Individual ind : components) {
            ind.fitness -= minFitness;
        }
    }

    private void ageing() {
        for (Individual ind : components) {
            ind.fitness -= 0.3; //ageingFactor
        }
    }

    public void iterate() {
        int individualPosition = 0;
//        if (ageing) {
//            ageing();
//        }
        if (components.size() < 3) {
//            System.out.println("not running tournament with less than 3 components.");
        } else {
            LinkedList<Individual> offspring = new LinkedList<Individual>();
            for (int i = 0; i < components.size(); i++) {
                List<Individual> parents = tournament((components.size() > 10) ? 10 : components.size(), 3, 1);
                Individual child = player4.crossover(parents, 3, individualPosition++);
                if (player4.rnd_.nextDouble() < 0.1) { // mutationRate
                    offspring.add(new Individual(child.points, individualPosition++));
                    child.mutateFromNormal(0.8); //mutateFactor
                } else {
                    offspring.add(child);
                }
            }
            components = offspring;
        }
    }

    public void removeIndividual(Individual j) {
        components.remove(j);
    }
}
