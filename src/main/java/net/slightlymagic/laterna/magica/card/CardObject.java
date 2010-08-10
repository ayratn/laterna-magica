/**
 * CardObject.java
 * 
 * Created on 21.04.2010
 */

package net.slightlymagic.laterna.magica.card;


import java.util.List;

import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.characteristic.CardCharacteristics;


/**
 * The class CardObject.
 * 
 * @version V0.0 21.04.2010
 * @author Clemens Koza
 */
public interface CardObject extends MagicObject {
    public CardTemplate getTemplate();
    
    /**
     * <p>
     * Returns all card parts that currently count for the rules
     * </p>
     */
    public List<? extends CardParts> getActiveCardParts();
    
    public List<? extends CardCharacteristics> getCharacteristics();
    
    /**
     * <p>
     * Returns the state of this permanent, or null if it is not a permanent (not on the battlefield).
     * </p>
     */
    public State getState();
    
    public Printing getPrinting();
    
    
    /**
     * Sets how much damage is marked on this creature. Non-creature, non-permanent MagicObjects will throw an
     * IllegalStateException.
     */
    public void setMarkedDamage(int damage);
    
    /**
     * Resets the damage marked on this creature. Unlike {@link #setMarkedDamage(int)}, this is a no-op if the
     * object is not a creature permanent.
     */
    public void resetMarkedDamage();
    
    /**
     * Returns how much damage is marked on this creature. Non-creature, non-permanent MagicObjects will throw an
     * IllegalStateException.
     */
    public int getMarkedDamage();
}
