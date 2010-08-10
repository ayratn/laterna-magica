/**
 * ListAddEdit.java
 * 
 * Created on 28.03.2010
 */

package net.slightlymagic.laterna.magica.edit.impl;


import java.util.List;

import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.edit.Edit;



/**
 * The class ListAddEdit.
 * 
 * @version V0.0 28.03.2010
 * @author Clemens Koza
 */
public class ListAddEdit<E> extends Edit {
    private static final long serialVersionUID = 1890403548436321285L;
    
    private List<E>           list;
    private int               index;
    private E                 newValue;
    
    public ListAddEdit(Game game, List<E> list, int index, E value) {
        super(game);
        this.list = list;
        this.index = index;
        newValue = value;
    }
    
    @Override
    public void execute() {
        list.add(index, newValue);
    }
    
    @Override
    protected void rollback() {
        list.remove(index);
    }
    
    @Override
    public String toString() {
        return "Add " + newValue + " at [" + index + "]";
    }
}
