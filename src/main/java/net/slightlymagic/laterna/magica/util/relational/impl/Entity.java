/**
 * EditableEntity.java
 * 
 * Created on 15.08.2010
 */

package net.slightlymagic.laterna.magica.util.relational.impl;


/**
 * The class EditableEntity.
 * 
 * @version V0.0 15.08.2010
 * @author Clemens Koza
 */
class Entity<T> {
    private T value;
    
    public Entity() {
        this(null);
    }
    
    public Entity(T value) {
        setValue(value);
    }
    
    public void setValue(T value) {
        this.value = value;
    }
    
    public T getValue() {
        return value;
    }
}
