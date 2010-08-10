/**
 * ListSetEdit.java
 * 
 * Created on 28.03.2010
 */

package net.slightlymagic.laterna.magica.edit.impl;


import java.util.List;

import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.edit.Edit;



/**
 * The class ListSetEdit.
 * 
 * @version V0.0 28.03.2010
 * @author Clemens Koza
 */
public class ListSetEdit<E> extends Edit {
    private static final long serialVersionUID = -3611883981044034051L;
    
    private List<E>           list;
    private int               index;
    private E                 oldValue, newValue;
    
    public ListSetEdit(Game game, List<E> list, int index, E value) {
        super(game);
        this.list = list;
        this.index = index;
        newValue = value;
    }
    
    @Override
    public void execute() {
        oldValue = list.set(index, newValue);
    }
    
    @Override
    protected void rollback() {
        list.set(index, oldValue);
    }
    
    public E getOldValue() {
        return oldValue;
    }
    
    @Override
    public String toString() {
        return "Set [" + index + "] to " + newValue;
    }
}
