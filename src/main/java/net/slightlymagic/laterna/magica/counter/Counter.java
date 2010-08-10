/**
 * Counter.java
 * 
 * Created on 31.03.2010
 */

package net.slightlymagic.laterna.magica.counter;


/**
 * The class Counter. A counter is used to keep track of the number of times an event has happened, for example for
 * storm or lands.
 * 
 * @version V0.0 31.03.2010
 * @author Clemens Koza
 */
public interface Counter {
    /**
     * Returns the current count.
     */
    public int getCount();
    
    /**
     * Increases the count by one.
     */
    public void increase();
    
    /**
     * Resets the count
     */
    public void reset();
}
