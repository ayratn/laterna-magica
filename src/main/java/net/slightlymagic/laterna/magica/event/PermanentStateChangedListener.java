/**
 * PermanentStateChangedListener.java
 * 
 * Created on 19.04.2010
 */

package net.slightlymagic.laterna.magica.event;


import java.util.EventListener;

import net.slightlymagic.laterna.magica.card.impl.PermanentStateChangedEvent;


/**
 * The class PermanentStateChangedListener.
 * 
 * @version V0.0 19.04.2010
 * @author Clemens Koza
 */
public interface PermanentStateChangedListener extends EventListener {
    public void stateChanged(PermanentStateChangedEvent event);
    
    public interface Internal extends PermanentStateChangedListener {}
}
