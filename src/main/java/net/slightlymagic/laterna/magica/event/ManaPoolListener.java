/**
 * ManaPoolListener.java
 * 
 * Created on 12.04.2010
 */

package net.slightlymagic.laterna.magica.event;


import java.util.EventListener;

import net.slightlymagic.laterna.magica.mana.impl.ManaPoolEvent;


/**
 * The class ManaPoolListener.
 * 
 * @version V0.0 12.04.2010
 * @author Clemens Koza
 */
public interface ManaPoolListener extends EventListener {
    public void ManaAdded(ManaPoolEvent ev);
    
    public void ManaRemoved(ManaPoolEvent ev);
    
    /**
     * Marker interface for engine-internal listeners. engine-internal listeners are notified before non-internals.
     */
    public interface Internal extends ManaPoolListener {}
}
