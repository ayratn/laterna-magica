/**
 * EditableListenerList.java
 * 
 * Created on 29.03.2010
 */

package net.slightlymagic.laterna.magica.edit.impl;


import java.util.EventListener;

import net.slightlymagic.beans.EventListenerList;
import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.GameContent;


/**
 * The class EditableListenerList.
 * 
 * @version V0.0 29.03.2010
 * @author Clemens Koza
 */
public class EditableListenerList extends EventListenerList implements GameContent {
    private static final long serialVersionUID = 8278453808866780920L;
    
    private Game              game;
    
    public EditableListenerList(Game game) {
        this.game = game;
    }
    
    public Game getGame() {
        return game;
    }
    
    @Override
    public <T extends EventListener> void add(Class<T> t, T l) {
        new ListenerAddEdit<T>(getGame(), this, t, l).execute();
    }
    
    <T extends EventListener> void add0(Class<T> t, T l) {
        super.add(t, l);
    }
    
    @Override
    public <T extends EventListener> void remove(Class<T> t, T l) {
        new ListenerRemoveEdit<T>(getGame(), this, t, l).execute();
    }
    
    <T extends EventListener> void remove0(Class<T> t, T l) {
        super.remove(t, l);
    }
}
