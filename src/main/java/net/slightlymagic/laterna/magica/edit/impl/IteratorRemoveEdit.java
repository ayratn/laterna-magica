/**
 * IteratorRemoveEdit.java
 * 
 * Created on 25.03.2010
 */

package net.slightlymagic.laterna.magica.edit.impl;


import java.util.Iterator;

import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.edit.Edit;


/**
 * The class IteratorRemoveEdit. This edit is used to implement an undoable {@link Iterator#remove()} operation.
 * Since an iterator itself can't add elements, and also the {@link Iterator#next()} method can't be undone, there
 * is no symmethric way to do this. instead, the semantics are defined as follows:
 * <ul>
 * <li>When such an edit is first executed, {@link Iterator#remove()} is called to preserve its legality. (in the
 * common case of fail-fast iterators)</li>
 * <li>When such an edit is undone, {@link #add()} is called to add the value back to the collection. The
 * implementation will usually vary between collections.</li>
 * <li>When such an edit is re-executed, {@link #remove()} is called to again remove the element.</li>
 * </ul>
 * As you see, rolling back will not restore the iterator's but rather the collection's state.
 * 
 * @version V0.0 25.03.2010
 * @author Clemens Koza
 */
public abstract class IteratorRemoveEdit<E> extends Edit {
    private static final long serialVersionUID = -88827982161495817L;
    
    private Iterator<E>       it;
    protected final E         oldValue;
    
    public IteratorRemoveEdit(Game game, Iterator<E> it, E oldValue) {
        super(game);
        this.it = it;
        this.oldValue = oldValue;
    }
    
    @Override
    public void execute() {
        if(it != null) it.remove();
        else remove();
        it = null;
    }
    
    @Override
    protected void rollback() {
        add();
    }
    
    protected abstract void remove();
    
    protected abstract void add();
    
    @Override
    public String toString() {
        return "Remove " + oldValue + " from iterator";
    }
}
