/**
 * OneSide.java
 * 
 * Created on 16.08.2010
 */

package net.slightlymagic.laterna.magica.util.relational;


import java.util.Set;


/**
 * The class OneSide.
 * 
 * @version V0.0 16.08.2010
 * @author Clemens Koza
 * @param <O>
 * @param <M>
 */
public interface OneSide<O, M> {
    public void add(ManySide<M, O> entity);
    
    public void remove(ManySide<M, O> entity);
    
    public boolean contains(ManySide<M, O> entity);
    
    public Set<? extends ManySide<M, O>> getManySide();
    
    public Set<M> getManySideValues();
}
