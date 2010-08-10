/**
 * SetAddEdit.java
 * 
 * Created on 21.03.2010
 */

package net.slightlymagic.laterna.magica.edit.impl;


import java.util.Set;

import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.edit.Edit;


/**
 * The class SetAddEdit.
 * 
 * @version V0.0 21.03.2010
 * @author Clemens Koza
 */
public class SetAddEdit<E> extends Edit {
    private static final long serialVersionUID = 2694000154192080900L;
    
    private Set<E>            set;
    private E                 value;
    private boolean           added;
    
    public SetAddEdit(Game game, Set<E> set, E value) {
        super(game);
        this.set = set;
        this.value = value;
    }
    
    /**
     * Returns if the edit added the value or if it was already contained. The result is only meaningful after
     * {@link #execute()} was called.
     */
    public boolean isAdded() {
        return added;
    }
    
    @Override
    public void execute() {
        added = set.add(value);
    }
    
    @Override
    protected void rollback() {
        if(added) set.remove(value);
    }
    
    @Override
    public String toString() {
        return "Add " + value + " to set";
    }
}
