/**
 * ActiveChangedListener.java
 * 
 * Created on 10.04.2010
 */

package net.slightlymagic.laterna.magica.event;


import java.util.EventListener;

import net.slightlymagic.laterna.magica.player.Player;


/**
 * The class ActiveChangedListener.
 * 
 * @version V0.0 10.04.2010
 * @author Clemens Koza
 */
public interface ActiveChangedListener extends EventListener {
    public void nextActive(Player oldActive, Player newActive);
    
    /**
     * Marker interface for engine-internal listeners. engine-internal listeners are notified before non-internals.
     */
    public interface Internal extends ActiveChangedListener {}
}
