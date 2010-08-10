/**
 * ListIteratorRemoveEdit.java
 * 
 * Created on 28.03.2010
 */

package net.slightlymagic.laterna.magica.edit.impl;


import java.util.List;
import java.util.ListIterator;

import net.slightlymagic.laterna.magica.Game;


/**
 * The class ListIteratorRemoveEdit.
 * 
 * @version V0.0 28.03.2010
 * @author Clemens Koza
 */
public class ListIteratorRemoveEdit<E> extends IteratorRemoveEdit<E> {
    private static final long serialVersionUID = -6442973327359637613L;
    
    private List<E>           list;
    private int               index;
    
    public ListIteratorRemoveEdit(Game game, List<E> list, ListIterator<E> it, int index, E oldValue) {
        super(game, it, oldValue);
        this.list = list;
        this.index = index;
    }
    
    @Override
    protected void add() {
        list.add(index, oldValue);
    }
    
    @Override
    protected void remove() {
        list.remove(index);
    }
    
    @Override
    public String toString() {
        return "Remove " + oldValue + " from list iterator at [" + index + "]";
    }
}
