/**
 * DrawListener.java
 * 
 * Created on 02.04.2010
 */

package net.slightlymagic.laterna.magica.event;


import java.util.EventListener;

import net.slightlymagic.laterna.magica.player.impl.DrawEvent;


/**
 * The class DrawListener.
 * 
 * @version V0.0 02.04.2010
 * @author Clemens Koza
 */
public interface DrawListener extends EventListener {
    /**
     * Called when a player has drawn a card.
     */
    public void cardDrawn(DrawEvent ev);
    
    /**
     * Called when a player should have drawn a card, but he couldn't, because his library was empty.
     */
    public void cardNotDrawn(DrawEvent ev);
    
    /**
     * Marker interface for engine-internal listeners. engine-internal listeners are notified before non-internals.
     */
    public interface Internal extends DrawListener {}
}
