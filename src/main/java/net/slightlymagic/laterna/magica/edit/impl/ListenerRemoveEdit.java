/**
 * ListenerRemoveEdit.java
 * 
 * Created on 19.03.2010
 */

package net.slightlymagic.laterna.magica.edit.impl;


import java.util.EventListener;

import javax.swing.event.EventListenerList;

import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.edit.Edit;



/**
 * The class ListenerRemoveEdit.
 * 
 * @version V0.0 19.03.2010
 * @author Clemens Koza
 */
public class ListenerRemoveEdit<T extends EventListener> extends Edit {
    private static final long serialVersionUID = -2964012950248977444L;
    
    private EventListenerList listeners;
    private Class<T>          clazz;
    private T                 newListener;
    
    private boolean           contains;
    
    public ListenerRemoveEdit(Game game, EventListenerList listeners, Class<T> clazz, T newListener) {
        super(game);
        this.listeners = listeners;
        this.clazz = clazz;
        this.newListener = newListener;
    }
    
    private boolean contains() {
        Object[] listeners = this.listeners.getListenerList();
        for(int i = 0; i < listeners.length; i += 2)
            if(listeners[i] == clazz && listeners[i + 1].equals(newListener)) return true;
        return false;
    }
    
    @Override
    public void execute() {
        //increase performance by not double-searching when not contained
        if(contains = contains()) listeners.remove(clazz, newListener);
    }
    
    @Override
    protected void rollback() {
        //only add back if the remove had an effect
        if(contains) listeners.add(clazz, newListener);
    }
    
    @Override
    public String toString() {
        return "Remove " + clazz.getSimpleName() + " to listeners";
    }
}
