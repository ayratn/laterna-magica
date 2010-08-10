/**
 * SetRemoveEdit.java
 * 
 * Created on 21.03.2010
 */

package net.slightlymagic.laterna.magica.edit.impl;


import java.util.Set;

import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.edit.Edit;


/**
 * The class SetRemoveEdit.
 * 
 * @version V0.0 21.03.2010
 * @author Clemens Koza
 */
public class SetRemoveEdit<E> extends Edit {
    private static final long serialVersionUID = 2694000154192080900L;
    
    private Set<E>            set;
    private Object            value;
    private boolean           removed;
    
    public SetRemoveEdit(Game game, Set<E> set, Object value) {
        super(game);
        this.set = set;
        this.value = value;
    }
    
    /**
     * Returns if the edit removed the value or if it was not present. The result is only meaningful after
     * {@link #execute()} was called.
     */
    public boolean isRemoved() {
        return removed;
    }
    
    @Override
    public void execute() {
        removed = set.remove(value);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void rollback() {
        //the value is only added if removed is true, which means that the value was already contained. it is
        //therefore save to add the element, even if the reference is to class Object
        if(removed) set.add((E) value);
    }
    
    @Override
    public String toString() {
        return "Remove " + value + " from set";
    }
}
