/**
 * TimestampListener.java
 * 
 * Created on 15.04.2010
 */

package net.slightlymagic.laterna.magica.event;


import java.util.EventListener;

import net.slightlymagic.laterna.magica.timestamp.Timestamp;



/**
 * The class TimestampListener.
 * 
 * @version V0.0 15.04.2010
 * @author Clemens Koza
 */
public interface TimestampListener extends EventListener {
    public void timestampUpdated(Timestamp t);
    
    /**
     * Marker interface for engine-internal listeners. engine-internal listeners are notified before non-internals.
     */
    public interface Internal extends TimestampListener {}
}
