/**
 * PhaseChangedListener.java
 * 
 * Created on 10.04.2010
 */

package net.slightlymagic.laterna.magica.event;


import java.util.EventListener;

import net.slightlymagic.laterna.magica.action.turnBased.TurnBasedAction;


/**
 * The class PhaseChangedListener.
 * 
 * @version V0.0 10.04.2010
 * @author Clemens Koza
 */
public interface EnterTurnBasedActionListener extends EventListener {
    public void enterTurnBasedAction(TurnBasedAction.Type action);
    
    /**
     * Marker interface for engine-internal listeners. engine-internal listeners are notified before non-internals.
     */
    public interface Internal extends EnterTurnBasedActionListener {}
}
