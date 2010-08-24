/**
 * TimestampFactory.java
 * 
 * Created on 04.09.2009
 */

package net.slightlymagic.laterna.magica.timestamp;


import java.util.Iterator;

import net.slightlymagic.beans.properties.Property;
import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.event.TimestampListener;
import net.slightlymagic.laterna.magica.impl.AbstractGameContent;


/**
 * The class TimestampFactory.
 * 
 * @version V0.0 04.09.2009
 * @author Clemens Koza
 */
public class TimestampFactory extends AbstractGameContent {
    private Property<Integer> currentTimestamp;
    
    public TimestampFactory(Game game) {
        super(game);
        currentTimestamp = properties.property("currentTimestamp", 0);
    }
    
    public Timestamp newTimestamp() {
        return new Timestamp(this);
    }
    
    /**
     * Returns the Timestamp that was previously given.
     */
    public int getCurrentTimestamp() {
        return currentTimestamp.getValue();
    }
    
    /**
     * Advances the factory to the next timestamp and returns it.
     */
    int nextTimestamp() {
        currentTimestamp.setValue(currentTimestamp.getValue() + 1);
        return getCurrentTimestamp();
    }
    
    public void addTimestampListener(TimestampListener l) {
        if(l instanceof TimestampListener.Internal) {
            listeners.add(TimestampListener.Internal.class, (TimestampListener.Internal) l);
        } else listeners.add(TimestampListener.class, l);
    }
    
    public void removeTimestampListener(TimestampListener l) {
        if(l instanceof TimestampListener.Internal) {
            listeners.remove(TimestampListener.Internal.class, (TimestampListener.Internal) l);
        } else listeners.remove(TimestampListener.class, l);
    }
    
    @SuppressWarnings("unchecked")
    public Iterator<TimestampListener> getTimestampListeners() {
        return listeners.getIterator(TimestampListener.Internal.class, TimestampListener.class);
    }
}
