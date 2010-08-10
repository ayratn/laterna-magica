/**
 * ListRemoveEdit.java
 * 
 * Created on 28.03.2010
 */

package net.slightlymagic.laterna.magica.edit.impl;


import java.util.List;

import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.edit.Edit;


/**
 * The class ListRemoveEdit.
 * 
 * @version V0.0 28.03.2010
 * @author Clemens Koza
 */
public class ListRemoveEdit<E> extends Edit {
    private static final long serialVersionUID = -7112666695498786788L;
    
    private List<E>           list;
    private int               index;
    private E                 oldValue;
    
    public ListRemoveEdit(Game game, List<E> list, int index) {
        super(game);
        this.list = list;
        this.index = index;
    }
    
    @Override
    public void execute() {
        oldValue = list.remove(index);
    }
    
    @Override
    protected void rollback() {
        list.add(index, oldValue);
    }
    
    public E getOldValue() {
        return oldValue;
    }
    
    @Override
    public String toString() {
        return "Remove [" + index + "] from list";
    }
}
