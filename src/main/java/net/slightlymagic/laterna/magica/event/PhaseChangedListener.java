/**
 * PhaseChangedListener.java
 * 
 * Created on 10.04.2010
 */

package net.slightlymagic.laterna.magica.event;


import java.util.EventListener;

import net.slightlymagic.laterna.magica.turnStructure.PhaseStructure.Phase;


/**
 * The class PhaseChangedListener.
 * 
 * @version V0.0 10.04.2010
 * @author Clemens Koza
 */
public interface PhaseChangedListener extends EventListener {
    public void nextPhase(Phase oldPhase, Phase newPhase);
    
    /**
     * Marker interface for engine-internal listeners. engine-internal listeners are notified before non-internals.
     */
    public interface Internal extends PhaseChangedListener {}
}
