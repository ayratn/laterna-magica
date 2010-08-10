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
public class ListIteratorAddEdit<E> extends Edit {
    private static final long         serialVersionUID = -7436047066662673366L;
    
    private List<E>                   list;
    private transient ListIterator<E> it;
    private int                       index;
    private E                         newValue;
    
    public ListIteratorAddEdit(Game game, List<E> list, ListIterator<E> it, E newValue) {
        super(game);
        this.list = list;
        this.it = it;
        this.index = it.nextIndex();
        this.newValue = newValue;
    }
    
    @Override
    public void execute() {
        if(it != null) it.add(newValue);
        else list.add(index, newValue);
        it = null;
    }
    
    @Override
    protected void rollback() {
        list.remove(index);
    }
    
    @Override
    public String toString() {
        return "Add " + newValue + " to list iterator at [" + index + "]";
    }
}
