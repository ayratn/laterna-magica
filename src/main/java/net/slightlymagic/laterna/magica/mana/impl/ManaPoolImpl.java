/**
 * ManaPoolImpl.java
 * 
 * Created on 12.04.2010
 */

package net.slightlymagic.laterna.magica.mana.impl;


import static java.util.Collections.*;

import java.util.Iterator;
import java.util.Set;

import net.slightlymagic.laterna.magica.event.ManaPoolListener;
import net.slightlymagic.laterna.magica.impl.AbstractGameContent;
import net.slightlymagic.laterna.magica.mana.Mana;
import net.slightlymagic.laterna.magica.mana.ManaPool;
import net.slightlymagic.laterna.magica.player.Player;


/**
 * The class ManaPoolImpl.
 * 
 * @version V0.0 12.04.2010
 * @author Clemens Koza
 */
public class ManaPoolImpl extends AbstractGameContent implements ManaPool {
    private Player owner;
    private Set<Mana> pool, poolView;
    
    public ManaPoolImpl(Player owner) {
        super(owner.getGame());
        this.owner = owner;
        pool = properties.set("pool");
        poolView = unmodifiableSet(pool);
    }
    
    public Player getOwner() {
        return owner;
    }
    
    public Set<Mana> getPool() {
        return poolView;
    }
    
    public void addMana(Mana mana) {
        if(pool.add(mana)) fireManaPoolEvent(new ManaPoolEvent(this, mana, true));
    }
    
    public void removeMana(Mana mana) {
        if(pool.remove(mana)) fireManaPoolEvent(new ManaPoolEvent(this, mana, false));
    }
    
    public void emptyPool() {
        Iterator<Mana> it;
        //remove mana with removeMana instead of the iterator for event notification
        //don't use a single iterator because of ConcurrentModificationExceptions
        while((it = getPool().iterator()).hasNext())
            removeMana(it.next());
    }
    
    protected void fireManaPoolEvent(ManaPoolEvent e) {
        for(Iterator<ManaPoolListener> it = getManaPoolListeners(); it.hasNext();)
            if(e.isAdding()) it.next().ManaAdded(e);
            else it.next().ManaRemoved(e);
    }
    
    public void addManaPoolListener(ManaPoolListener l) {
        listeners.add(ManaPoolListener.class, l);
    }
    
    public void removeManaPoolListener(ManaPoolListener l) {
        listeners.remove(ManaPoolListener.class, l);
    }
    
    @SuppressWarnings("unchecked")
    public Iterator<ManaPoolListener> getManaPoolListeners() {
        return listeners.getIterator(ManaPoolListener.class);
    }
}
