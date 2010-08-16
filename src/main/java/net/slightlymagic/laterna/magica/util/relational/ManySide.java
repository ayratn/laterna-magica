/**
 * ManySide.java
 * 
 * Created on 16.08.2010
 */

package net.slightlymagic.laterna.magica.util.relational;


/**
 * The class ManySide.
 * 
 * @version V0.0 16.08.2010
 * @author Clemens Koza
 * @param <O>
 * @param <M>
 */
public interface ManySide<M, O> {
    public void setOneSide(OneSide<O, M> entity);
    
    public OneSide<O, M> getOneSide();
    
    public O getOneSideValue();
}
