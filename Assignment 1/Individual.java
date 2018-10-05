
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
        return fitness;
    }

    public void mutate() {
        for (int i = 0; i < 10; i++) {
            double coinFlip = player4.rnd_.nextDouble();
            if (coinFlip > 0.5) {
                this.points[i] = (player4.rnd_.nextDouble() * 10) - 5;
            }
        }
    }
}

