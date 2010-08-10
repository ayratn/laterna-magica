/**
 * GameState.java
 * 
 * Created on 11.01.2010
 */

package net.slightlymagic.laterna.magica.edit;


import static java.lang.Integer.*;
import static java.lang.String.*;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;


import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.impl.AbstractGameContent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The class GameState. The game state stores the root edit and therefore the whole edit hierarchy.
 * 
 * FIXME Edits store game-dependent information about a change, which makes them non-portable between hosts and
 * therefore does not enable saving games to disk or syncing two game instances over the network.
 * 
 * @version V1.0 17.01.2010
 * @author Clemens Koza
 */
public class GameState extends AbstractGameContent implements Serializable {
    private static final long   serialVersionUID = -8594325531298473425L;
    
    private static final Logger log              = LoggerFactory.getLogger(GameState.class);
    
    private LinkedList<Edit>    stack;
    
    //first is the tree's root, current is the last executed move (or an open parent move), last is the last move
    //in the tree (either the current or a move in the future)
    private Edit                first, current, last;
    
    public GameState(Game game) {
        super(game);
        stack = new LinkedList<Edit>();
        first = new CompoundEdit(this, false, game + "'s GameState", true);
        
        current = first;
        last = first;
    }
    
    /**
     * Adds the move to the active move. If m is a {@link CompoundEdit}, it additionally becomes the active move.
     */
    void add(Edit m) {
        synchronized(this) {
            //works for the first call since both are null
            if(current != last) {
                //TODO remove child edits of current from the stack
                
                log.trace(valueOf(this));
                log.debug(valueOf(stack));
                log.debug(valueOf(current));
                
                if(current instanceof CompoundEdit) {
                    //if a compound move is current, all its children are in the future
                    current.getChildren().clear();
                }
                
                //of current's ancestors, all children after current (or after an ancestor of it) are in the future
                Edit child = current, ancestor;
                while((ancestor = child.getParent()) != null) {
                    List<Edit> children = ancestor.getChildren();
                    int index = children.indexOf(child);
                    while(children.size() > index + 1)
                        children.remove(index + 1);
                    child = ancestor;
                }
//            throw new UnsupportedOperationException("not yet implemented");
            }
            if(!stack.isEmpty()) m.setParent(stack.getFirst());
            if(m instanceof CompoundEdit) stack.addFirst(m);
            current = m;
            last = m;
        }
        
        String s = m.getClass().getSimpleName();
        try {
            s = m.toString();
            if(s == null) s = m.getClass().getSimpleName();
        } catch(Exception ex) {
            // this is called from m's constructor. If m's toString isn't robust at that time, that's reasonable
        }
        if(m instanceof CompoundEdit) log.debug("PUSH: " + s);
        else {
            log.debug("PUT:  " + s);
            log.trace(null, new Exception());
        }
    }
    
    /**
     * Ends the active move, but only if it is the given move.
     * 
     * @throws AssertionError If the move to end is not the active move. Although this is a parameter check, an
     *             assertion error is right here since the cause is most likely forgetting to close a move after
     *             creating its children.
     * @throws IllegalArgumentException If the move to end is the root move.
     */
    void end(CompoundEdit e) {
        if(!e.equals(stack.getFirst())) throw new AssertionError("Tried to end a non-active move");
        if(e == first) throw new IllegalArgumentException("Tried to end the root move");
        stack.removeFirst();
        
        log.debug("POP:  " + e);
    }
    
    /**
     * Moves the GameState to the given edit. that means, if the edit lies in the past, undos edits until the given
     * edit is reached; if it lies in the future, redos edits until it is reached.
     */
    public synchronized void stepTo(Edit e) {
        switch(signum(e.compareTo(current))) {
            case -1: //the edit lies in the past
                while(current != e)
                    backward();
            break;
            case 0: //nothing to do
            break;
            case 1: //the edit lies in the future
                while(current != e)
                    forward();
            break;
            default:
                throw new AssertionError();
        }
    }
    
    synchronized void forward() {
        Edit next = current.getNextEdit();
        if(next == null) throw new IllegalStateException();
        current = next;
        next.execute();
        log.debug("FORW: " + current);
    }
    
    synchronized void backward() {
        Edit previous = current.getPreviousEdit();
        if(previous == null) throw new IllegalStateException();
        current.rollback();
        log.debug("BACK: " + current);
        current = previous;
    }
    
    /**
     * returns the state's root
     */
    public Edit getRoot() {
        return first;
    }
    
    public synchronized Edit getCurrent() {
        return current;
    }
    
    public Iterator<Edit> iterator() {
        return listIterator();
    }
    
    /**
     * Returns a list iterator whose cursor is right before the specified index.
     */
    public ListIterator<Edit> listIterator(int index) {
        if(index < 0) throw new IllegalArgumentException();
        ListIterator<Edit> it = listIterator();
        for(int i = 0; i < index; i++) {
            if(!it.hasNext()) throw new IllegalArgumentException();
            it.next();
        }
        return it;
    }
    
    /**
     * Returns a list iterator whose cursor is right after the last element.
     */
    public ListIterator<Edit> listIteratorEnd() {
        ListIterator<Edit> it = listIterator();
        while(it.hasNext())
            it.next();
        return it;
    }
    
    public ListIterator<Edit> listIterator() {
        return new ListIterator<Edit>() {
            private Edit current = null;
            private int  index   = -1;
            
            public boolean hasNext() {
                return current == null || current.getNextEdit() != null;
            }
            
            public synchronized Edit next() {
                Edit next = current == null? first:current.getNextEdit();
                if(next == null) throw new NoSuchElementException();
                current = next;
                index++;
                return next;
            }
            
            public int nextIndex() {
                return index + 1;
            }
            
            public boolean hasPrevious() {
                return current != null;
            }
            
            public synchronized Edit previous() {
                Edit previous = current;
                if(previous == null) throw new NoSuchElementException();
                current = current.getPreviousEdit();
                index--;
                return previous;
            }
            
            public int previousIndex() {
                return index;
            }
            
            public void add(Edit o) {
                throw new UnsupportedOperationException();
            }
            
            public void set(Edit o) {
                throw new UnsupportedOperationException();
            }
            
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
    
    @Override
    public String toString() {
        return getRoot().deepToString();
    }
}
