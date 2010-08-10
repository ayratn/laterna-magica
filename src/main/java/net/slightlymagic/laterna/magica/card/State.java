/**
 * State.java
 * 
 * Created on 13.09.2009
 */

package net.slightlymagic.laterna.magica.card;


import java.beans.PropertyChangeListener;
import java.util.Iterator;

import net.slightlymagic.laterna.magica.GameContent;
import net.slightlymagic.laterna.magica.event.PermanentStateChangedListener;


/**
 * <p>
 * The class State. The physical state of a permanent, consisting the four boolean properties tapped, flipped, face
 * down, and phased out. A non-permanent card neither has a state, nor does it remember it when it leaves play.
 * </p>
 * 
 * @version V0.0 13.09.2009
 * @author Clemens Koza
 */
public interface State extends GameContent {
    /**
     * <p>
     * The four status categories, specified in {@magic.ruleRef 110.6 CR 110.6}
     * </p>
     */
    public static enum StateType {
        TAPPED, FLIPPED, FACE_DOWN, PHASED_OUT;
    }
    
    public CardObject getCard();
    
    /**
     * <p>
     * Returns the current value of the given state
     * </p>
     */
    public boolean getState(StateType state);
    
    /**
     * <p>
     * Sets the new value for the given state
     * </p>
     */
    public void setState(StateType state, boolean value);
    
    
    public void addStateChangedListener(PermanentStateChangedListener l);
    
    public void removeStateChangedListener(PermanentStateChangedListener l);
    
    public Iterator<PermanentStateChangedListener> getStateChangedListeners();
    
    public void addPropertyChangeListener(PropertyChangeListener listener);
    
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);
    
    public void removePropertyChangeListener(PropertyChangeListener listener);
    
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);
}
