/**
 * PermanentStateChangedEvent.java
 * 
 * Created on 19.04.2010
 */

package net.slightlymagic.laterna.magica.card.impl;


import net.slightlymagic.laterna.magica.card.State;
import net.slightlymagic.laterna.magica.card.State.StateType;
import net.slightlymagic.laterna.magica.effect.replacement.ReplaceableEvent;


/**
 * The class PermanentStateChangedEvent.
 * 
 * @version V0.0 19.04.2010
 * @author Clemens Koza
 */
public class PermanentStateChangedEvent extends ReplaceableEvent {
    private State     state;
    private StateType type;
    private boolean   value;
    
    public PermanentStateChangedEvent(State state, StateType type, boolean value) {
        super(state.getCard());
        this.state = state;
        this.type = type;
        this.value = value;
    }
    
    public State getState() {
        return state;
    }
    
    public StateType getType() {
        return type;
    }
    
    public boolean getValue() {
        return value;
    }
    
    @Override
    protected boolean execute0() {
        return ((StateImpl) getState()).fireStateChanged(this);
    }
}
