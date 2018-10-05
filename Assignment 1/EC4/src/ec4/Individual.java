/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec4;

import java.util.Random;


 class Individual implements Comparable {
    public double[] points;
    public double fitness;
    private boolean evaluated = false;
    Random rnd_=new Random();
    
    @Override   
    public int compareTo(Object t) {
        return Double.compare(this.getFitness(), ((Individual) t).getFitness());
    }
    
    public double evaluate(Object c){
        return 1.0;
    }
    
    public Individual(double[] points){
        this.points = points;
    }

    public Individual() {
        this.points = new double[10];
        for (int i = 0; i < 10; i++)
            this.points[i] = rnd_.nextDouble() * 10 - 5;
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
}

