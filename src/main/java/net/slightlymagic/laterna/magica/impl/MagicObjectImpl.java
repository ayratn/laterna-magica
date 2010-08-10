/**
 * MagicObjectImpl.java
 * 
 * Created on 03.09.2009
 */

package net.slightlymagic.laterna.magica.impl;


import static java.lang.String.*;

import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.characteristic.ObjectCharacteristics;
import net.slightlymagic.laterna.magica.characteristic.OverridingCharacteristic;
import net.slightlymagic.laterna.magica.characteristics.Characteristics;
import net.slightlymagic.laterna.magica.counter.EditableCounter;
import net.slightlymagic.laterna.magica.counter.EditableCounterImpl;
import net.slightlymagic.laterna.magica.edit.CompoundEdit;
import net.slightlymagic.laterna.magica.edit.impl.EditableListenerList;
import net.slightlymagic.laterna.magica.edit.property.EditableProperty;
import net.slightlymagic.laterna.magica.edit.property.EditablePropertyChangeSupport;
import net.slightlymagic.laterna.magica.effect.LocalEffects;
import net.slightlymagic.laterna.magica.effect.impl.LocalEffectsImpl;
import net.slightlymagic.laterna.magica.event.MoveCardListener;
import net.slightlymagic.laterna.magica.player.Player;
import net.slightlymagic.laterna.magica.timestamp.impl.AbstractTimestamped;
import net.slightlymagic.laterna.magica.util.ExtendedListenerList;
import net.slightlymagic.laterna.magica.util.MagicaCollections;
import net.slightlymagic.laterna.magica.zone.Zone;
import net.slightlymagic.laterna.magica.zone.Zone.Zones;

import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;


/**
 * The class MagicObjectImpl.
 * 
 * @version V0.0 03.09.2009
 * @author Clemens Koza
 */
public abstract class MagicObjectImpl extends AbstractTimestamped implements MagicObject {
    protected ExtendedListenerList           listeners;
    protected EditablePropertyChangeSupport  s;
    

    private Player                           owner;
    //a card's controller is not a characteristic, but the representation is perfect
    private OverridingCharacteristic<Player> controller;
    //the zone the card is in
    private LocalEffects                     effects;
    private EditableProperty<Zone>           zone;
    
    private Map<String, EditableCounter>     counters;
    
    public MagicObjectImpl(Game game) {
        super(game);
        listeners = new EditableListenerList(game);
        s = new EditablePropertyChangeSupport(getGame(), this);
        
        zone = new EditableProperty<Zone>(getGame(), s, ZONE);
        
        counters = MagicaCollections.editableMap(game, new HashMap<String, EditableCounter>());
        
        effects = new LocalEffectsImpl(getGame());
        
        controller = new Controller();
    }
    
    public void setOwner(Player owner) {
        //TODO make this a replaceable event
        this.owner = owner;
    }
    
    public Player getOwner() {
        return owner;
    }
    
    public Player getController() {
        //a card can have a controller on the battlefield, the stack and in the command zone
        if(getZone() == null
                || (getZone().getType() != Zones.BATTLEFIELD && getZone().getType() != Zones.STACK && getZone().getType() != Zones.COMMAND)) {
            return null;
        } else return controller.getValue();
    }
    
    public void setZone(Zone zone) {
        if(this.zone.getValue() == zone) return;
        if(zone != null && zone.getOwner() != null && zone.getOwner() != this.getOwner()) throw new IllegalArgumentException(
                format("Moving %s to %s's %s", this, zone.getOwner(), zone.getType()));
        new MoveCardEvent(this, this.zone.getValue(), zone).execute();
    }
    
    public Zone getZone() {
        return zone.getValue();
    }
    
    public LocalEffects getEffects() {
        return effects;
    }
    
    
    public EditableCounter getCounter(String name) {
        EditableCounter result = counters.get(name);
        if(result == null) counters.put(name, result = new EditableCounterImpl(getGame()));
        return result;
    }
    
    
    public void addMoveCardListener(MoveCardListener l) {
        if(l instanceof MoveCardListener.Internal) {
            listeners.add(MoveCardListener.Internal.class, (MoveCardListener.Internal) l);
        } else listeners.add(MoveCardListener.class, l);
    }
    
    public void removeMoveCardListener(MoveCardListener l) {
        if(l instanceof MoveCardListener.Internal) {
            listeners.remove(MoveCardListener.Internal.class, (MoveCardListener.Internal) l);
        } else listeners.remove(MoveCardListener.class, l);
    }
    
    @SuppressWarnings("unchecked")
    public Iterator<MoveCardListener> getMoveCardListeners() {
        return listeners.getIterator(MoveCardListener.Internal.class, MoveCardListener.class);
    }
    
    protected boolean fireCardMoved(MoveCardEvent ev) {
        assert ev.getCard() == this;
        CompoundEdit ed = new CompoundEdit(getGame(), true, "Move " + this + " from " + ev.getFrom() + " to "
                + ev.getTo());
        
        zone.setValue(ev.getTo());
        getTimestamp().updateTimestamp();
        
        PeekingIterator<MoveCardListener> from = Iterators.peekingIterator(ev.getFrom() == null? Iterators.<MoveCardListener> emptyIterator():ev.getFrom().getMoveCardListeners());
        PeekingIterator<MoveCardListener> card = Iterators.peekingIterator(getMoveCardListeners());
        PeekingIterator<MoveCardListener> to = Iterators.peekingIterator(ev.getTo() == null? Iterators.<MoveCardListener> emptyIterator():ev.getTo().getMoveCardListeners());
        
        MoveCardListener l;
        Set<MoveCardListener> prev = new HashSet<MoveCardListener>();
        
        while(from.hasNext() && from.peek() instanceof MoveCardListener.Internal)
            if(prev.add(l = from.next())) l.cardMoved(ev);
        while(card.hasNext() && card.peek() instanceof MoveCardListener.Internal)
            if(prev.add(l = card.next())) l.cardMoved(ev);
        while(to.hasNext() && to.peek() instanceof MoveCardListener.Internal)
            if(prev.add(l = to.next())) l.cardMoved(ev);
        
        while(from.hasNext())
            if(prev.add(l = from.next())) l.cardMoved(ev);
        while(card.hasNext())
            if(prev.add(l = card.next())) l.cardMoved(ev);
        while(to.hasNext())
            if(prev.add(l = to.next())) l.cardMoved(ev);
        
        ed.end();
        
        return true;
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
    
    private class Controller implements OverridingCharacteristic<Player> {
        protected Player value;
        
        public Player getValue() {
            value = getOwner();
            //TODO s controller-changing effects
            return value;
        }
        
        public Characteristics getCharacteristic() {
            return null;
        }
        
        public ObjectCharacteristics getCharacteristics() {
            return null;
        }
        
        public Game getGame() {
            return MagicObjectImpl.this.getGame();
        }
    }
}
