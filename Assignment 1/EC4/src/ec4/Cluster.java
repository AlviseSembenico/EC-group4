/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec4;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author alvis
 */
public class Cluster {
    public List<Individual> components;
    public List<Double> fitnessHistory;
    private boolean newGen=true;
    
    public Cluster(){
        components=new LinkedList<Individual>();
        fitnessHistory=new LinkedList<Double>();
    }
    public Cluster(List<Individual> l){
        components=l;
    }
    public Cluster(Individual i){
        this();
        components.add(i);
    }
    
    public double fitnessMean(){
        double mean=0;
        for(Individual i:components)
            mean+=i.getFitness();
        
        mean/=components.size();
        fitnessHistory.add(mean);
        return mean;
    }
    public List<Individual> purgeMax(int n){
        return purge(components.size()-n);
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
    
    public int populationSizeTh(){
        //to implement more smart
        return components.size();
    }
    
    public boolean contains(Individual i,double radius){
        if(i.distance(gravityCenter())<radius)
            return true;
        return false;
    }
    
    public void newGeneration(List<Individual> l){
        this.components=l;
        newGen=true;
    }
    
    public double[] gravityCenter(){
        double[] res=new double[Individual.nDimension];
        for(int i=0;i<Individual.nDimension;i++){
            double tot=0;
            for(Individual ind:components)
                tot+=ind.points[i];
            tot/=Individual.nDimension;
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
