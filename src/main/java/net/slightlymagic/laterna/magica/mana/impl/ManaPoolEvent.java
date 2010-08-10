/**
 * ManaPoolEvent.java
 * 
 * Created on 12.04.2010
 */

package net.slightlymagic.laterna.magica.mana.impl;


import net.slightlymagic.laterna.magica.mana.Mana;
import net.slightlymagic.laterna.magica.mana.ManaPool;


/**
 * The class ManaPoolEvent.
 * 
 * @version V0.0 12.04.2010
 * @author Clemens Koza
 */
public class ManaPoolEvent {
    private ManaPool pool;
    private Mana     mana;
    private boolean  add;
    
    /**
     * @param pool The player to whose mana pool to add to/remove from
     * @param mana The mana to add/remove
     * @param add If adding/romoving
     */
    public ManaPoolEvent(ManaPool pool, Mana mana, boolean add) {
        this.pool = pool;
        this.mana = mana;
        this.add = add;
    }
    
    public ManaPool getPool() {
        return pool;
    }
    
    public Mana getMana() {
        return mana;
    }
    
    public boolean isAdding() {
        return add;
    }
}
