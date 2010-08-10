/**
 * ManaPool.java
 * 
 * Created on 12.04.2010
 */

package net.slightlymagic.laterna.magica.mana;


import java.util.Iterator;
import java.util.Set;

import net.slightlymagic.laterna.magica.GameContent;
import net.slightlymagic.laterna.magica.event.ManaPoolListener;
import net.slightlymagic.laterna.magica.mana.impl.ManaPoolEvent;
import net.slightlymagic.laterna.magica.player.Player;


/**
 * The class ManaPool.
 * 
 * @version V0.0 12.04.2010
 * @author Clemens Koza
 */
public interface ManaPool extends GameContent {
    /**
     * Returns the player to which the mana pool belongs
     */
    public Player getOwner();
    
    /**
     * Returns an unmodifiable view of the mana contained in the pool
     */
    public Set<Mana> getPool();
    
    /**
     * Adds the mana to the mana pool. Does nothing if that mana is already contained in this pool.
     */
    public void addMana(Mana mana);
    
    /**
     * Removes the mana from this mana pool. Does nothing if that mana was not contained in this pool.
     */
    public void removeMana(Mana mana);
    
    /**
     * Removes all mana from the mana pool, triggering an individual {@link ManaPoolEvent} for each mana removed.
     */
    public void emptyPool();
    
    public void addManaPoolListener(ManaPoolListener l);
    
    public void removeManaPoolListener(ManaPoolListener l);
    
    public Iterator<ManaPoolListener> getManaPoolListeners();
}
