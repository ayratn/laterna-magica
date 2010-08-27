/**
 * IndexProperty.java
 * 
 * Created on 27.08.2010
 */

package net.slightlymagic.laterna.magica.turnStructure.impl;


import java.util.List;

import net.slightlymagic.beans.PropertyChangeSupport;
import net.slightlymagic.beans.properties.AbstractProperty;


/**
 * The class IndexProperty.
 * 
 * @version V0.0 27.08.2010
 * @author Clemens Koza
 */
public class IndexProperty<T> extends AbstractProperty<Integer> {
    private PropertyChangeSupport s;
    private String                name;
    private List<T>               values;
    private Integer               index;
    
    public IndexProperty(PropertyChangeSupport s, String name, int index, List<T> values) {
        this.s = s;
        this.name = name;
        this.index = index;
        this.values = values;
    }
    
    public List<T> getValues() {
        return values;
    }
    
    public T getRealValue() {
        List<T> values = getValues();
        return index == null || values == null || index < 0 || index >= values.size()? null:values.get(index);
    }
    
    public void setRealValue(T value) {
        setValue(getValues().indexOf(value));
    }
    
    public void setValue(Integer index) {
        T oldValue = getRealValue();
        this.index = index;
        T newValue = getRealValue();
        s.firePropertyChange(name, oldValue, newValue);
    }
    
    public Integer getValue() {
        return index;
    }
}
