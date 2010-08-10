/**
 * LifeListener.java
 * 
 * Created on 20.10.2009
 */

package net.slightlymagic.laterna.magica.event;


import java.util.EventListener;

import net.slightlymagic.laterna.magica.player.impl.LifeEvent;


/**
 * The class LifeListener. Life listeners may be registered to life totals to track changes.
 * 
 * @version V0.0 20.10.2009
 * @author Clemens Koza
 */
public interface LifeListener extends EventListener {
    public void lifeGained(LifeEvent ev);
    
    public void lifeLost(LifeEvent ev);
    
    /**
     * Marker interface for engine-internal listeners. engine-internal listeners are notified before non-internals.
     */
    public interface Internal extends LifeListener {}
}
