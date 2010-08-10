/**
 * ZoneImpl.java
 * 
 * Created on 19.10.2009
 */

package net.slightlymagic.laterna.magica.zone.impl;


import static java.lang.String.*;
import static java.util.Collections.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.edit.CompoundEdit;
import net.slightlymagic.laterna.magica.edit.impl.EditableListenerList;
import net.slightlymagic.laterna.magica.edit.property.EditablePropertyChangeSupport;
import net.slightlymagic.laterna.magica.edit.property.PropertyChangeListListener;
import net.slightlymagic.laterna.magica.event.MoveCardListener;
import net.slightlymagic.laterna.magica.impl.AbstractGameContent;
import net.slightlymagic.laterna.magica.impl.MoveCardEvent;
import net.slightlymagic.laterna.magica.player.Player;
import net.slightlymagic.laterna.magica.util.ExtendedListenerList;
import net.slightlymagic.laterna.magica.util.ListenableCollections;
import net.slightlymagic.laterna.magica.util.MagicaCollections;
import net.slightlymagic.laterna.magica.zone.SortedZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The class ZoneImpl. This class implements {@link SortedZone} and may be used as normal zone, too. The additional
 * semantics of a sorted zone are meaningless to unsorted zones, so there's no problem with sharing the
 * implementation.
 * 
 * @version V0.0 19.10.2009
 * @author Clemens Koza
 */
public class ZoneImpl extends AbstractGameContent implements SortedZone {
    private static final Logger     log = LoggerFactory.getLogger(ZoneImpl.class);
    
    protected ExtendedListenerList  listeners;
    protected PropertyChangeSupport s;
    private Zones                   type;
    private Player                  owner;
    private List<MagicObject>       cards, view;
    
    public ZoneImpl(Game game, Zones type) {
        this(game, type, null);
    }
    
    public ZoneImpl(Game game, Zones type, Player owner) {
        super(game);
        CompoundEdit e = new CompoundEdit(getGame(), true, owner == null? "Create " + type:"Create " + owner
                + "'s " + type);
        this.type = type;
        this.owner = owner;
        listeners = new EditableListenerList(getGame());
        s = new EditablePropertyChangeSupport(getGame(), this);
        addMoveCardListener(new MoveListener());
        e.end();
    }
    
    public Zones getType() {
        return type;
    }
    
    public Player getOwner() {
        return owner;
    }
    
    public List<MagicObject> getCards() {
        if(cards == null) {
            LinkedList<MagicObject> cards = new LinkedList<MagicObject>();
            this.cards = MagicaCollections.editableList(getGame(), ListenableCollections.listenableList(cards,
                    new PropertyChangeListListener<MagicObject>(s, CARDS)));
            view = unmodifiableList(cards);
        }
        return view;
    }
    
    public void shuffle() {
        if(cards == null) return;
        CompoundEdit edit = new CompoundEdit(getGame(), true, "Shuffle " + toString());
        Collections.shuffle(cards, getGame().getRandom());
        edit.end();
    }
    
    public int size() {
        if(cards == null) return 0;
        return getCards().size();
    }
    
    public boolean isEmpty() {
        if(cards == null) return true;
        return getCards().isEmpty();
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
    
    @Override
    public String toString() {
        if(owner == null) return type.toString();
        else return format("%s's %s", owner.toString(), type.toString());
    }
    
    private class MoveListener implements MoveCardListener.Internal {
        public void cardMoved(MoveCardEvent ev) {
            log.debug(ZoneImpl.this + ": " + ev);
            //ensure the list was created
            getCards();
            if(ev.getTo() == ZoneImpl.this) {
                cards.add(ev.getCard());
            } else if(ev.getFrom() == ZoneImpl.this) {
                cards.remove(ev.getCard());
            } else throw new AssertionError();
        }
    }
}
