/**
 * MoveCardListener.java
 * 
 * Created on 20.10.2009
 */

package net.slightlymagic.laterna.magica.event;


import java.util.EventListener;

import net.slightlymagic.laterna.magica.impl.MoveCardEvent;



/**
 * The class MoveCardListener. Listens for {@link MoveCardEvent}s. A listener is informed of every event at most
 * once. For example, if the listener is registered to the leaving and the entering zone, it is still only informed
 * once of the event.
 * 
 * @version V0.0 20.10.2009
 * @author Clemens Koza
 */
public interface MoveCardListener extends EventListener {
    public void cardMoved(MoveCardEvent ev);
    
    /**
     * Marker interface for engine-internal listeners. engine-internal listeners are notified before non-internals.
     */
    public interface Internal extends MoveCardListener {}
}
