/**
 * StateImpl.java
 * 
 * Created on 11.04.2010
 */

package net.slightlymagic.laterna.magica.card.impl;


import java.beans.PropertyChangeListener;
import java.util.Iterator;

import net.slightlymagic.laterna.magica.card.CardObject;
import net.slightlymagic.laterna.magica.card.State;
import net.slightlymagic.laterna.magica.edit.CompoundEdit;
import net.slightlymagic.laterna.magica.edit.impl.EditableListenerList;
import net.slightlymagic.laterna.magica.edit.property.EditableProperty;
import net.slightlymagic.laterna.magica.edit.property.EditablePropertyChangeSupport;
import net.slightlymagic.laterna.magica.event.PermanentStateChangedListener;
import net.slightlymagic.laterna.magica.impl.AbstractGameContent;
import net.slightlymagic.laterna.magica.util.ExtendedListenerList;


/**
 * The class StateImpl.
 * 
 * @version V0.0 11.04.2010
 * @author Clemens Koza
 */
public class StateImpl extends AbstractGameContent implements State {
    protected final ExtendedListenerList          listeners;
    protected final EditablePropertyChangeSupport s;
    
    private final CardObject                      card;
    
    private final EditableProperty<Boolean>[]     states;
    
    @SuppressWarnings("unchecked")
    public StateImpl(CardObject card) {
        super(card.getGame());
        this.card = card;
        listeners = new EditableListenerList(getGame());
        
        s = new EditablePropertyChangeSupport(getGame(), this);
        states = new EditableProperty[StateType.values().length];
        for(int i = 0; i < states.length; i++)
            states[i] = new EditableProperty<Boolean>(getGame(), s, StateType.values()[i].name(), false);
    }
    
    public CardObject getCard() {
        return card;
    }
    
    
    public boolean getState(StateType state) {
        return states[state.ordinal()].getValue();
    }
    
    
    public void setState(StateType state, boolean value) {
        if(getState(state) == value) return;
        new PermanentStateChangedEvent(this, state, value).execute();
    }
    
    protected boolean fireStateChanged(PermanentStateChangedEvent ev) {
        assert ev.getState() == this;
        CompoundEdit ed = new CompoundEdit(getGame(), true, "Set " + getCard() + "'s state");
        
        states[ev.getType().ordinal()].setValue(ev.getValue());
        
        for(Iterator<PermanentStateChangedListener> it = getStateChangedListeners(); it.hasNext();)
            it.next().stateChanged(ev);
        
        ed.end();
        return true;
    }
    
    
    public void addStateChangedListener(PermanentStateChangedListener l) {
        if(l instanceof PermanentStateChangedListener.Internal) listeners.add(
                PermanentStateChangedListener.Internal.class, (PermanentStateChangedListener.Internal) l);
        else listeners.add(PermanentStateChangedListener.class, l);
    }
    
    public void removeStateChangedListener(PermanentStateChangedListener l) {
        if(l instanceof PermanentStateChangedListener.Internal) listeners.remove(
                PermanentStateChangedListener.Internal.class, (PermanentStateChangedListener.Internal) l);
        else listeners.remove(PermanentStateChangedListener.class, l);
    }
    
    @SuppressWarnings("unchecked")
    public Iterator<PermanentStateChangedListener> getStateChangedListeners() {
        return listeners.getIterator(PermanentStateChangedListener.Internal.class,
                PermanentStateChangedListener.class);
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        s.addPropertyChangeListener(listener);
    }
    
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        s.addPropertyChangeListener(propertyName, listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        s.removePropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        s.removePropertyChangeListener(propertyName, listener);
    }
}
