/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec4;



public class Wrapper<T,C> implements Comparable<Wrapper> {
    public T t;
    public C c;
    public boolean compare=true;
    
    @Override   
    public int compareTo(Wrapper t) {
        if(compare)
            return (((Comparable)(this.t)).compareTo((T) t.t));
        return (((Comparable)(this.c)).compareTo((C) t.c));
    }
    
    public Wrapper(T t, C c){
        this.t=t;
        this.c=c;
    }

    public Wrapper(T t, C c,boolean compare){
        this.t=t;
        this.c=c;
        this.compare=false;
    }
    
}
