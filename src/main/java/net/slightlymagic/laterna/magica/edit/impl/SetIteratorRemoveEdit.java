/**
 * SetIteratorRemoveEdit.java
 * 
 * Created on 25.03.2010
 */

package net.slightlymagic.laterna.magica.edit.impl;


import java.util.Iterator;
import java.util.Set;

import net.slightlymagic.laterna.magica.Game;


/**
 * The class SetIteratorRemoveEdit.
 * 
 * @version V0.0 25.03.2010
 * @author Clemens Koza
 */
public class SetIteratorRemoveEdit<E> extends IteratorRemoveEdit<E> {
    private static final long serialVersionUID = -2260233843588452384L;
    
    private Set<E>            set;
    
    public SetIteratorRemoveEdit(Game game, Set<E> set, Iterator<E> it, E value) {
        super(game, it, value);
        this.set = set;
    }
    
    @Override
    protected void add() {
        set.add(oldValue);
    }
    
    @Override
    protected void remove() {
        set.remove(oldValue);
    }
}
