/**
 * LifeTotal.java
 * 
 * Created on 16.10.2009
 */

package net.slightlymagic.laterna.magica.player;


import java.beans.PropertyChangeListener;

import net.slightlymagic.laterna.magica.event.LifeListener;



/**
 * The class LifeTotal.
 * 
 * @version V0.0 16.10.2009
 * @author Clemens Koza
 */
public interface LifeTotal {
    /**
     * The {@code lifeTotal} property name
     */
    public static final String LIFE_TOTAL = "lifeTotal";
    
    /**
     * Returns the player's life total.
     */
    public int getLifeTotal();
    
    /**
     * Sets the player's life total. This causes the player to gain or lose life.
     */
    public void setLifeTotal(int total);
    
    public void gainLife(int life);
    
    public void loseLife(int life);
    
    
    public void addLifeListener(LifeListener l);
    
    public void removeLifeListener(LifeListener l);
    
    public void addPropertyChangeListener(PropertyChangeListener listener);
    
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);
    
    public void removePropertyChangeListener(PropertyChangeListener listener);
    
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);
}
