
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.vu.contest.*;

class Individual implements Comparable {

    public double[] points;
    public double fitness;
    private boolean evaluated = false;
    Random rnd_ = new Random();
    public double[] stepSize;
    static ContestEvaluation f = null;
    static double maxValue = Double.NEGATIVE_INFINITY;
    static int totEval = 0;
    static int nEval ;
    static int nDimension;
    public final double adaptiveStep =1/Math.sqrt(2*nDimension);
    public final double coordinateStep=1/Math.sqrt(2*Math.sqrt(nDimension));
    public final int position;
    static double maxFitness=0;

    @Override
    public int compareTo(Object t) {
        return -Double.compare(this.getFitness(), ((Individual) t).getFitness());
    }

    public double computeDistance(Individual ch1) {
        double res = 0.0;
        for (int i = 0; i < ch1.points.length; i++)
            res += Math.pow(ch1.points[i] - this.points[i], 2);
        return Math.sqrt(res);
    }
    
    public double distance(Individual ch1) {
        return player4.distanceMatrix[this.position][ch1.position];
    }
    

    public double computeDistance(double [] ch1){
        double res = 0.0;
        for (int i = 0; i < ch1.length; i++)
            res += Math.pow(ch1[i] - this.points[i],2);
        return Math.sqrt(res);
    }
    
    public double distance(double [] ch1){
        return computeDistance(ch1);
    }
    

    public Individual(double[] points) {
        this();
        this.points=points;
    }

    public Individual() {
        this.position=player4.individualPosition++;
        this.points = new double[10];
        for (int i = 0; i < nDimension; i++) 
            this.points[i] = rnd_.nextDouble() * 10 - 5;
        
        stepSize=new double[nDimension];
        for(int i=0;i<nDimension;i++)
            stepSize[i]=1;
    }

    public double getFitness() {
        if (!evaluated) {
            fitness = (double) player4.evaluation.evaluate(this.points);
        }
        evaluated = true;
        if(fitness>maxFitness){
            maxFitness=fitness;
            //System.out.println("New max fitness found: " + fitness);
        }
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

    private void mutateStepSize(){
        for(int i=0;i<nDimension;i++)
            stepSize[i]*=Math.pow(Math.E,adaptiveStep*rnd_.nextGaussian()+coordinateStep*rnd_.nextGaussian());
    }
    
    public Individual arithmeticCrossover(Individual parent,double arithmeticCrossoverStep){
        double[] res=new double[nDimension];
        for(int i=0;i<nDimension;i++)
                res[i]=arithmeticCrossoverStep*points[i]+(1-arithmeticCrossoverStep)*parent.points[i];
        
        Individual ind=new Individual(res);
        ind.stepSize=stepSize.clone();
        return ind;
    }
    
    public void mutateFromNormal(double mutateFactor, double clusterScale) {
        for (int i = 0; i < nDimension; i++) {
            double coinFlip = rnd_.nextDouble();
            if (coinFlip > mutateFactor) {
                double generated = rnd_.nextGaussian() * stepSize[i] * clusterScale;
                if(this.points[i] + generated >= 5 || this.points[i] + generated <= -5)
                    generated=-generated;
                while (this.points[i] + generated >= 5 || this.points[i] + generated <= -5) {
                    generated *= rnd_.nextDouble();
                }
                this.points[i] += generated;
            }
        }
        mutateStepSize();
        evaluated = false;
    }

}
