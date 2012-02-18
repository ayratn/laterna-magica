/**
 * GameMessage.java
 * 
 * Created on 16.02.2012
 */

package net.slightlymagic.laterna.magica.gui.actor;


import java.util.UUID;

import net.slightlymagic.beans.properties.Property;
import net.slightlymagic.objectTransactions.History;
import net.slightlymagic.objectTransactions.properties.reference.ReferenceProperty;


/**
 * <p>
 * The class GameMessage.
 * </p>
 * 
 * @version V0.0 16.02.2012
 * @author Clemens Koza
 */
public class GameMessage<T> {
    private UUID              history;
    private final Property<T> value = new ReferenceProperty<T>();
    
    public GameMessage(T object) {
        history = History.getHistoryForThread().getId();
        value.setValue(object);
    }
    
    public T getValue() {
        return value.getValue();
    }
    
    public void pushHistory() {
        History.getHistory(history).pushHistoryForThread();
    }
    
    public void popHistory() {
        History.getHistory(history).popHistoryForThread();
    }
}
