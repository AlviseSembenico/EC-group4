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
    
    public Cluster(){
        components=new LinkedList<Individual>();
    }
    public Cluster(List<Individual> l){
        components=l;
    }
    public Cluster(Individual i){
        this();
        components.add(i);
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
