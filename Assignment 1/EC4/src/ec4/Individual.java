/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec4;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.vu.contest.*;

class Individual implements Comparable {

    public double[] points;
    public double fitness;
    private boolean evaluated = false;
    Random rnd_ = new Random();
    static ContestEvaluation f = null;
    static double maxValue = 0;
    static int totEval = 0;

    @Override
    public int compareTo(Object t) {
        return Double.compare(this.getFitness(), ((Individual) t).getFitness());
    }

    public double evaluate(Object c) {
        if (f == null) {
            try {
                f = (org.vu.contest.ContestEvaluation) Class.forName("BentCigarFunction").newInstance();

            } catch (Exception ex) {
                Logger.getLogger(Individual.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Object res = f.evaluate(c);
        if (res == null) {

            f = null;
            return evaluate(c);
        }
        double result = (double) res;
        totEval++;
        if (result > maxValue) {
            maxValue = result;
            System.out.println("new record for fitness " + maxValue + ", evaluation n: " + totEval);
        }
        return result;

        // return (double)rnd_.nextDouble()*rnd_.nextInt(8);
    }

    public Individual(double[] points) {
        this.points = points;
    }

    public Individual() {
        this.points = new double[10];
        for (int i = 0; i < 10; i++) {
            this.points[i] = rnd_.nextDouble() * 10 - 5;
        }
    }

    public double getFitness() {
        if (!evaluated) {
            fitness = (double) evaluate(this.points);
        }
        evaluated = true;
        return fitness;
    }

    public void mutate(double mutateFactor) {
        for (int i = 0; i < 10; i++) {
            double coinFlip = rnd_.nextDouble();
            if (coinFlip > mutateFactor) {
                this.points[i] = (rnd_.nextDouble() * 10) - 5;
            }
        }
        evaluated = false;
    }

    private boolean isNumber(String l) {
        try {
            Integer.valueOf(l);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isNumber(char l) {
        return isNumber(String.valueOf(l));
    }

    public void mutateChildLarge(double mutationVariability) {
        for (int i = 0; i < points.length; i++) {
            String n = "";
            for (char l : String.valueOf(points[i]).toCharArray()) {
                if (isNumber(l)) {
                    if (rnd_.nextDouble() < mutationVariability) {
                        n += rnd_.nextInt(10);
                    }else
                        n += l;
                } else {
                    n += l;
                }

            }
            points[i] = Math.abs(Double.valueOf(n)) % 10 - 5;
        }
        
        evaluated = false;
    }
}
