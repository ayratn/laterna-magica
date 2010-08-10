/**
 * GameStartListener.java
 * 
 * Created on 02.04.2010
 */

package net.slightlymagic.laterna.magica.event;


import java.util.EventListener;

import net.slightlymagic.laterna.magica.Game;


/**
 * The class GameStartListener.
 * 
 * @version V0.0 02.04.2010
 * @author Clemens Koza
 */
public interface GameStartListener extends EventListener {
    public void gameStarted(Game game);
    
    /**
     * Marker interface for engine-internal listeners. engine-internal listeners are notified before non-internals.
     */
    public interface Internal extends GameStartListener {}
}
