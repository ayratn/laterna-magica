/**
 * PropertyChangeListListener.java
 * 
 * Created on 16.07.2010
 */

package net.slightlymagic.laterna.magica.edit.property;


import java.beans.PropertyChangeSupport;

import net.slightlymagic.laterna.magica.util.ListenableCollections.ListListener;


/**
 * The class PropertyChangeListListener.
 * 
 * @version V0.0 16.07.2010
 * @author Clemens Koza
 */
public class PropertyChangeListListener<E> implements ListListener<E> {
    private static final long     serialVersionUID = 625853864429729560L;
    
    private PropertyChangeSupport s;
    private String                propertyName;
    
    public PropertyChangeListListener(PropertyChangeSupport s, String propertyName) {
        this.s = s;
        this.propertyName = propertyName;
    }
    
    public void add(int index, E newValue) {
        s.fireIndexedPropertyChange(propertyName, index, null, newValue);
    }
    
    public void set(int index, E oldValue, E newValue) {
        s.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
    }
    
    public void remove(int index, E oldValue) {
        s.fireIndexedPropertyChange(propertyName, index, oldValue, null);
    }
}
