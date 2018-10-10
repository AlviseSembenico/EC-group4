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
    
    
    
    public void addIndividual(Individual i){
        components.add(i);
    }
    
}
