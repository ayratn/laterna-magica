/**
 * ManyToMany.java
 * 
 * Created on 16.08.2010
 */

package net.slightlymagic.laterna.magica.util.relational;


import java.util.Map;


/**
 * The class ManyToMany.
 * 
 * @version V0.0 16.08.2010
 * @author Clemens Koza
 * @param <M>
 * @param <N>
 * @param <T>
 */
public interface ManyToMany<M, N, T> {
    public void add(ManyToMany<N, M, T> entity);
    
    public void add(ManyToMany<N, M, T> entity, T center);
    
    public T set(ManyToMany<N, M, T> entity, T center);
    
    public T addOrSet(ManyToMany<N, M, T> entity, T center);
    
    public void remove(ManyToMany<N, M, T> entity);
    
    public boolean contains(ManyToMany<N, M, T> entity);
    
    public T get(ManyToMany<N, M, T> entity);
    
    public Map<? extends ManyToMany<N, M, T>, T> getOtherSide();
    
    public Map<N, T> getOtherSideValues();
}
