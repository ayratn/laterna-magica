/**
 * ListenerAddEdit.java
 * 
 * Created on 19.03.2010
 */

package net.slightlymagic.laterna.magica.edit.impl;


import java.util.EventListener;

import javax.swing.event.EventListenerList;

import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.edit.Edit;


/**
 * The class ListenerAddEdit.
 * 
 * @version V0.0 19.03.2010
 * @author Clemens Koza
 */
public class ListenerAddEdit<T extends EventListener> extends Edit {
    private static final long serialVersionUID = 5254381947556727172L;
    
    private EventListenerList listeners;
    private Class<T>          clazz;
    private T                 newListener;
    
    public ListenerAddEdit(Game game, EventListenerList listeners, Class<T> clazz, T newListener) {
        super(game);
        this.listeners = listeners;
        this.clazz = clazz;
        this.newListener = newListener;
    }
    
    @Override
    public void execute() {
        if(listeners instanceof EditableListenerList) ((EditableListenerList) listeners).add0(clazz, newListener);
        else listeners.add(clazz, newListener);
    }
    
    @Override
    protected void rollback() {
        if(listeners instanceof EditableListenerList) ((EditableListenerList) listeners).remove0(clazz,
                newListener);
        else listeners.remove(clazz, newListener);
    }
    
    @Override
    public String toString() {
        return "Add " + clazz.getSimpleName() + " to listeners";
    }
}
