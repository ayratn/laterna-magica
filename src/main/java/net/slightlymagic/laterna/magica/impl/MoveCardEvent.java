/**
 * MoveCardEvent.java
 * 
 * Created on 20.10.2009
 */

package net.slightlymagic.laterna.magica.impl;


import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.effect.replacement.ReplaceableEvent;
import net.slightlymagic.laterna.magica.zone.Zone;


/**
 * The class MoveCardEvent. MoveCardEvents are posted to notify a listener for the movement of a
 * {@link MagicObject} between two {@link Zone}s. Such events are also posted after creation and before deletion of
 * objects (such as newly created/destroyed tokens, ability objects put on/leaving the stack etc.), but not per se.
 * The event is the result of the object being put into a zone after creation, or removing from the zone before
 * deleting.
 * 
 * @version V0.0 20.10.2009
 * @author Clemens Koza
 */
public class MoveCardEvent extends ReplaceableEvent {
    private MagicObject card;
    private Zone        from, to;
    
    public MoveCardEvent(MagicObject card, Zone from, Zone to) {
        super(card);
        if(from == to) throw new IllegalArgumentException();
        if(from != null && from.getOwner() != null && from.getOwner() != card.getOwner()) throw new IllegalArgumentException();
        if(to != null && to.getOwner() != null && to.getOwner() != card.getOwner()) throw new IllegalArgumentException();
        this.card = card;
        this.from = from;
        this.to = to;
    }
    
    public MagicObject getCard() {
        return card;
    }
    
    public Zone getFrom() {
        return from;
    }
    
    public Zone getTo() {
        return to;
    }
    
    @Override
    protected boolean execute0() {
        return ((MagicObjectImpl) card).fireCardMoved(this);
    }
    
    @Override
    public String toString() {
        return "[" + getCard() + ", " + getFrom() + ", " + getTo() + "]";
    }
}
