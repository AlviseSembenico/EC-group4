

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author alvis
 */
public class Cluster {
    //list of the components that belong to the cluster
    public List<Individual> components;
    //history of the average fitness during the different generations
    public List<Double> fitnessHistory;
    //history of the centroid during the generations
    public List<double[]> centroidHistory;
    //list of the centroid of the cluster that are not consideret productive
    public static List<double[]> discartedCentroid = new LinkedList<double[]>();
    //the number of the generations in which one cluster cannnot be discarted
    public static int generationBound=1;
    //the offset the set the limit below that one cluster is considered discarted.
    public static double discardBound=1;
    //distance between 2 clusters, below that the behaviour will be similar
    public static double clusterDistance=0;
    //multiplier of the 1/step size
    public static double alphaStepSize=1;
    
    public Cluster(){
        components=new LinkedList<Individual>();
        fitnessHistory=new LinkedList<Double>();
        centroidHistory=new LinkedList<double[]>();
    }
    
    public Cluster(List<Individual> l){
        components=l;
    }
    
    public Cluster(Individual i){
        this();
        components.add(i);
    }
    
    /***
     * 
     * @return true if the cluster is close to an already discarted cluster, false otherwise
     */
    private boolean closeToDiscarted(){
        for(double[] compare:Cluster.discartedCentroid)
            if(averageDistance(compare)<clusterDistance)
                return true;
        return false;
                
    }
    
    /***
     * 
     * @return true is it not considered productive, true otherwise
     */
    public boolean nonProductive(){
        if(fitnessHistory.size()<generationBound)
            return false;
        if(closeToDiscarted())
            return true;
        int size=fitnessHistory.size();
        return fitnessHistory.get(size/3)-fitnessHistory.get(size-1)<discardBound;
    }
    
    public double fitnessMean(){
        double mean=0;
        for(Individual i:components)
            mean+=i.getFitness();
        
        mean/=components.size();
        return mean;
    }
    
    public List<Individual> purgeMax(int n){
        return purge(components.size()-n);
    }
    
    public List<Individual> purge(){
        return purgeMax(getDynamicPopSize());
    }
    
    public List<Individual> purge(int n){
        List<Individual> res=new LinkedList<Individual>();
        for(int j=0;j<n;j++){
            Individual m=components.get(0);
            for(Individual i:components)
                if(i.distance(gravityCenter())>m.distance(gravityCenter()))
                    m=i;
            res.add(m);
            components.remove(m);
        }
        return res;
    }
    
    public double getAlphaDynamicStepSize(){
        return 1/(alphaStepSize*fitnessMean()+1);
    }
    
    public double getDynamicRadius(){
        return 1/(fitnessMean()+1);
    }
    
    public int getDynamicPopSize(){
        double x=fitnessMean();
        return (int) (7.0+0.6*x-Math.pow(0.3*x, 2)+Math.pow(0.03*x, 3));
    }
    
    public boolean contains(Individual i){
        return contains(i,getDynamicRadius());
    }
    
    public boolean contains(Individual i,double radius){
        if(i.distance(gravityCenter())<radius)
            return true;
        return false;
    }
    
    public void newGeneration(List<Individual> l){
        fitnessHistory.add(fitnessMean());
        centroidHistory.add(gravityCenter());
        this.components=l;
    }
    
    public double[] gravityCenter(){
        double[] res=new double[Individual.nDimension];
        for(int i=0;i<Individual.nDimension;i++){
            double tot=0;
            for(Individual ind:components)
                tot+=ind.points[i];
            tot/=components.size();
            res[i]=tot;
        }
        return res;
    }
    
    public double fitnessVariance(){
        double tot=0;
        double mean=fitnessMean();
        for(Individual i:components)
            tot+=Math.pow(i.getFitness()-mean,2);
        
        tot/=components.size();
        tot=Math.sqrt(mean);
        return tot;
    }
    
    public double radius(){
        double max=0;
        for(Individual i:components)
            for(Individual j:components){
                double dist=i.distance(j);
                if(dist>max)
                    max=dist;
            }
        return max;
    } 
    
    public double maxDistance(Cluster cl){
        if(this==cl)
            return 0;
        double max=0;
        for(Individual i:components)
            for(Individual j:cl.components){
            double dist=i.distance(j);
                if(dist>max)
                    max=dist;
            }
        if(components.size()+cl.components.size()==0)
            return 0;
        return max;
    }
    
    public double averageDistance(double[] cl){
        double tot=0;
        for(Individual i:components)
            tot+=i.distance(cl);
        if(components.size()==0)
            return 0;
        return tot/(components.size());
    }
    
    public double averageDistance(Cluster cl){
        if(this==cl)
            return 0;
        double tot=0;
        for(Individual i:components)
            for(Individual j:cl.components)
                tot+=i.distance(j);
        if(components.size()+cl.components.size()==0)
            return 0;
        return tot/(components.size()+cl.components.size());
    }
    
    public void addIndividual(Individual i){
        components.add(i);
    }
    
}
