/**
 * PriorChangedListener.java
 * 
 * Created on 10.04.2010
 */

package net.slightlymagic.laterna.magica.event;


import java.util.EventListener;

import net.slightlymagic.laterna.magica.player.Player;



/**
 * The class PriorChangedListener.
 * 
 * @version V0.0 10.04.2010
 * @author Clemens Koza
 */
public interface PriorChangedListener extends EventListener {
    public void nextPrior(Player oldPrior, Player newPrior);
    
    /**
     * Marker interface for engine-internal listeners. engine-internal listeners are notified before non-internals.
     */
    public interface Internal extends PriorChangedListener {}
}
