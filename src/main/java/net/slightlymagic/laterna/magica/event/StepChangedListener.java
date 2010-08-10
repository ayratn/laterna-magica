/**
 * StepChangedListener.java
 * 
 * Created on 10.04.2010
 */

package net.slightlymagic.laterna.magica.event;


import java.util.EventListener;

import net.slightlymagic.laterna.magica.turnStructure.PhaseStructure.Step;


/**
 * The class StepChangedListener.
 * 
 * @version V0.0 10.04.2010
 * @author Clemens Koza
 */
public interface StepChangedListener extends EventListener {
    public void nextStep(Step oldStep, Step newStep);
    
    /**
     * Marker interface for engine-internal listeners. engine-internal listeners are notified before non-internals.
     */
    public interface Internal extends StepChangedListener {}
}
