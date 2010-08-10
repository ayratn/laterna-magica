/**
 * ListIteratorSetEdit.java
 * 
 * Created on 28.03.2010
 */

package net.slightlymagic.laterna.magica.edit.impl;


import java.util.List;
import java.util.ListIterator;

import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.edit.Edit;



/**
 * The class ListIteratorSetEdit.
 * 
 * @version V0.0 28.03.2010
 * @author Clemens Koza
 */
public class ListIteratorSetEdit<E> extends Edit {
    private static final long         serialVersionUID = -498439166599994286L;
    
    private List<E>                   list;
    private transient ListIterator<E> it;
    private int                       index;
    private E                         oldValue, newValue;
    
    public ListIteratorSetEdit(Game game, List<E> list, ListIterator<E> it, int index, E oldValue, E newValue) {
        super(game);
        this.list = list;
        this.it = it;
        this.index = index;
        this.newValue = newValue;
        this.oldValue = oldValue;
    }
    
    @Override
    public void execute() {
        if(it != null) it.set(newValue);
        else list.set(index, newValue);
        it = null;
    }
    
    @Override
    protected void rollback() {
        list.set(index, oldValue);
    }
    
    @Override
    public String toString() {
        return "Set [" + index + "] of list iterator to " + newValue;
    }
}
