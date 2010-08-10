/**
 * CounterImpl.java
 * 
 * Created on 31.03.2010
 */

package net.slightlymagic.laterna.magica.counter;


import java.beans.PropertyChangeSupport;


/**
 * The class CounterImpl.
 * 
 * @version V0.0 31.03.2010
 * @author Clemens Koza
 */
public class CounterImpl implements Counter {
    private PropertyChangeSupport s = new PropertyChangeSupport(this);
    private int                   count;
    
    public int getCount() {
        return count;
    }
    
    public void increase() {
        s.firePropertyChange("count", count, ++count);
    }
    
    public void reset() {
        s.firePropertyChange("count", count, count = 0);
    }
}
