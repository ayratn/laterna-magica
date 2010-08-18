/**
 * MagicObject.java
 * 
 * Created on 19.04.2010
 */

package net.slightlymagic.laterna.magica;


import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.List;

import net.slightlymagic.laterna.magica.ability.AbilityObject;
import net.slightlymagic.laterna.magica.ability.NonStaticAbility;
import net.slightlymagic.laterna.magica.action.play.PlayAction;
import net.slightlymagic.laterna.magica.action.play.PlayInformation;
import net.slightlymagic.laterna.magica.action.play.Playable;
import net.slightlymagic.laterna.magica.card.CardObject;
import net.slightlymagic.laterna.magica.characteristic.ObjectCharacteristics;
import net.slightlymagic.laterna.magica.counter.Counter;
import net.slightlymagic.laterna.magica.effect.LocalEffects;
import net.slightlymagic.laterna.magica.event.MoveCardListener;
import net.slightlymagic.laterna.magica.player.Player;
import net.slightlymagic.laterna.magica.zone.Zone;


/**
 * The class MagicObject. This class is the common superclass of all objects that can reside in a zone. Typical
 * objects are {@link CardObject}s. Another type is {@link AbilityObject}; those can only exist on the stack and
 * have additional properties about what {@link NonStaticAbility} they belong to.
 * 
 * @version V0.0 19.04.2010
 * @author Clemens Koza
 */
public interface MagicObject extends Playable {
    /**
     * The {@code zone} property name
     */
    public static final String ZONE       = "zone";
    /**
     * The {@code controller} property name
     */
    public static final String CONTROLLER = "controller";
    
    /**
     * <p>
     * Returns the object's controller. An object's controller may be modified by effects, but only if it has a
     * controller. Only permanents and objects on the stack or in the command zone have a controller. Other objects
     * return {@code null}.
     * </p>
     */
    public Player getController();
    
    /**
     * <p>
     * Sets the card's owner. This is usually called only once after creation, but may be called even after that on
     * some casual variants.
     * </p>
     */
    public void setOwner(Player owner);
    
    /**
     * <p>
     * Returns the object's owner.
     * </p>
     */
    public Player getOwner();
    
    /**
     * <p>
     * Moves the card into a new Zone.
     * </p>
     */
    public void setZone(Zone zone);
    
    /**
     * <p>
     * Returns the zone the card is in.
     * </p>
     */
    public Zone getZone();
    
    /**
     * <p>
     * Returns the active characteristics, every one derived from one of the active card parts.
     * </p>
     */
    public List<? extends ObjectCharacteristics> getCharacteristics();
    
    /**
     * <p>
     * Returns the characteristic effects that currently apply to this object. This only contains the effects not
     * from static abilities. In other words, effects for which the affected objects don't change after the effect
     * was created.
     * </p>
     */
    public LocalEffects getEffects();
    
    /**
     * Returns a counter for the given name. Creates one if there's no such counter.
     */
    public Counter getCounter(String name);
    
    
    public boolean isLegal(PlayAction a);
    
    
    public void play(PlayAction a);
    
    
    public PlayInformation getPlayInformation();
    
    
    public void resetPlayInformation();
    
    
    public void addMoveCardListener(MoveCardListener l);
    
    public void removeMoveCardListener(MoveCardListener l);
    
    public Iterator<MoveCardListener> getMoveCardListeners();
    
    
    public void addPropertyChangeListener(PropertyChangeListener listener);
    
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);
    
    public void removePropertyChangeListener(PropertyChangeListener listener);
    
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);
}
