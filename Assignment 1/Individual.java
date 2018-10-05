
 class Individual implements Comparable {
    public double[] points;
    public double fitness;
    private boolean evaluated = false;
    
    @Override   
    public int compareTo(Object t) {
        return Double.compare(this.getFitness(), ((Individual) t).getFitness());
    }
    
    public Individual(double[] points){
        this.points = points;
    }

    public Individual() {
        this.points = new double[10];
        for (int i = 0; i < 10; i++)
            this.points[i] = player4.rnd_.nextDouble() * 10 - 5;
    }

    public double getFitness() {
        if (!evaluated) {
            fitness = (double) player4.evaluation.evaluate(this.points);
        }
        evaluated = true;
        return fitness;
    }

    public void mutate(double mutateFactor) {
        for (int i = 0; i < 10; i++) {
            double coinFlip = player4.rnd_.nextDouble();
            if (coinFlip > mutateFactor) {
                this.points[i] = (player4.rnd_.nextDouble() * 10) - 5;
            }
        }
        evaluated = false;
    }
}

