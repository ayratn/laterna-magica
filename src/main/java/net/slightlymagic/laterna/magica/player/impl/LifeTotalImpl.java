/**
 * LifeTotalImpl.java
 * 
 * Created on 16.10.2009
 */

package net.slightlymagic.laterna.magica.player.impl;


import java.beans.PropertyChangeListener;
import java.util.Iterator;

import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.GameContent;
import net.slightlymagic.laterna.magica.edit.CompoundEdit;
import net.slightlymagic.laterna.magica.edit.impl.EditableListenerList;
import net.slightlymagic.laterna.magica.edit.property.EditableProperty;
import net.slightlymagic.laterna.magica.edit.property.EditablePropertyChangeSupport;
import net.slightlymagic.laterna.magica.event.LifeListener;
import net.slightlymagic.laterna.magica.player.LifeTotal;
import net.slightlymagic.laterna.magica.player.Player;
import net.slightlymagic.laterna.magica.util.ExtendedListenerList;


/**
 * The class LifeTotalImpl. The life total is NOT initialized to 20 upon construction.
 * 
 * @version V0.0 16.10.2009
 * @author Clemens Koza
 */
public class LifeTotalImpl implements LifeTotal, GameContent {
    protected ExtendedListenerList          listeners;
    protected EditablePropertyChangeSupport s;
    private EditableProperty<Integer>       life;
    private Player                          player;
    
    public LifeTotalImpl(Player player) {
        this.player = player;
        listeners = new EditableListenerList(getGame());
        s = new EditablePropertyChangeSupport(getGame(), this);
        life = new EditableProperty<Integer>(getGame(), s, LIFE_TOTAL, 0);
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public Game getGame() {
        return getPlayer().getGame();
    }
    
    public int getLifeTotal() {
        return life.getValue();
    }
    
    public void setLifeTotal(int total) {
        int life = this.life.getValue();
        if(total < life) {
            loseLife(life - total);
        } else if(total > life) {
            gainLife(total - life);
        }
    }
    
    public void gainLife(int life) {
        if(life == 0) return;
        new LifeEvent(getPlayer(), life, true).execute();
    }
    
    public void loseLife(int life) {
        if(life == 0) return;
        new LifeEvent(getPlayer(), life, false).execute();
    }
    
    public void addLifeListener(LifeListener l) {
        listeners.add(LifeListener.class, l);
    }
    
    public void removeLifeListener(LifeListener l) {
        listeners.remove(LifeListener.class, l);
    }
    
    @SuppressWarnings("unchecked")
    public Iterator<LifeListener> getLifeListeners() {
        return listeners.getIterator(LifeListener.Internal.class, LifeListener.class);
    }
    
    protected boolean fireLifeEvent(LifeEvent e) {
        CompoundEdit ed = new CompoundEdit(getGame(), true, "Adjust " + getPlayer() + "'s life by "
                + (e.isGained()? "+":"-") + e.getAmount());
        
        life.setValue(life.getValue() + e.getChange());
        
        boolean gain = e.isGained();
        for(Iterator<LifeListener> it = getLifeListeners(); it.hasNext();) {
            if(gain) it.next().lifeGained(e);
            else it.next().lifeLost(e);
        }
        ed.end();
        
        //if called, the ammount will be != null
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
}
