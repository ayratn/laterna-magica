/**
 * Edit.java
 * 
 * Created on 30.11.2009
 */

package net.slightlymagic.laterna.magica.edit;


import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.impl.AbstractGameContent;



/**
 * The class Edit. An edit is a decision a player can make. Edits can be undone.
 * 
 * Edits can be divided into three categories
 * <ul>
 * <li>Edits that have no children</li>
 * <li>Edits that have children
 * <ul>
 * <li>to be done and undone atomically</li>
 * <li>to be done and undone individually</li>
 * </ul>
 * </li>
 * </ul>
 * 
 * For the first category, {@link #execute()} and {@link #rollback()} are used; for the other two categories, the
 * children are iterated; {@link #execute()} and {@link #rollback()} are not used.
 * 
 * FIXME Edits store game-dependent information about a change, which makes them non-portable between hosts and
 * therefore does not enable saving games to disk or syncing two game instances over the network.
 * 
 * @version V1.0 17.01.2010
 * @author Clemens Koza
 */
public abstract class Edit extends AbstractGameContent implements Serializable, Comparable<Edit> {
    private static final long serialVersionUID = 381930042216610090L;
    
    private Edit              parent;
    private boolean           atomic;
    
    public Edit(Game game) {
        this(game.getGameState(), true);
    }
    
    //This constructor is used by compound edits
    Edit(GameState gameState, boolean atomic) {
        super(gameState.getGame());
        this.atomic = atomic;
        gameState.add(this);
    }
    
    void setParent(Edit parent) {
        this.parent = parent;
        parent.getChildren().add(this);
    }
    
    public Edit getParent() {
        return parent;
    }
    
    public boolean isAtomic() {
        return atomic;
    }
    
    /**
     * Returns the move from parent that comes directly before this in the child list.
     */
    public Edit getPreviousSibling() {
        Edit parent = getParent();
        if(parent == null) return null;
        List<Edit> siblings = parent.getChildren();
        assert siblings != null;
        int index = siblings.indexOf(this);
        assert index != -1;
        return index == 0? null:siblings.get(index - 1);
    }
    
    /**
     * Returns the move from parent that comes directly after this in the child list.
     */
    public Edit getNextSibling() {
        Edit parent = getParent();
        if(parent == null) return null;
        List<Edit> siblings = parent.getChildren();
        assert siblings != null;
        int index = siblings.indexOf(this);
        assert index != -1;
        return index == siblings.size() - 1? null:siblings.get(index + 1);
    }
    
    /**
     * Returns the move from parent that comes directly before this in logical order.
     */
    public Edit getPreviousEdit() {
        Edit previous = getPreviousSibling();
        if(previous != null) {
            //there is a previous sibling; return the last logical move in that move's tree
            //that is the recursively last move in the tree
            List<Edit> children;
            while((children = previous.getChildren()) != null && !children.isEmpty())
                previous = children.get(children.size() - 1);
            return previous;
        }
        //there's no previous sibling, so the previous move is simply the parent (null for the root)
        return getParent();
    }
    
    /**
     * Returns the move from parent that comes directly after this in logical order.
     */
    public Edit getNextEdit() {
        List<Edit> children = getChildren();
        //the move has children which should be traversed over
        if(children != null && !children.isEmpty()) return children.get(0);
        //there are no children, so find the next branch that contains siblings after the ancestor
        for(Edit ancestor = this; ancestor != null; ancestor = ancestor.getParent()) {
            Edit next = ancestor.getNextSibling();
            if(next != null) return next;
        }
        //the root was reached when searching for the next node, so there are no more moves
        return null;
    }
    
    protected List<Edit> getChildren() {
        return null;
    }
    
    protected void execute() {}
    
    protected void rollback() {}
    
    /**
     * Compares the edits in chronological order, the order in which the edits are executed in the course of a
     * game. negative values, zero, and positive values indicate that this edit comes before, is the same, or comes
     * after the specified edit.
     */
    public int compareTo(Edit other) {
        if(other == null) throw new IllegalArgumentException("other == null");
        if(other.getGame() != getGame()) throw new IllegalArgumentException("other.getGame() != this.getGame()");
        if(this == other) return 0;
        
        //store the ancestors
        LinkedList<Edit> thisAncestors = new LinkedList<Edit>(), otherAncestors = new LinkedList<Edit>();
        for(Edit ancestor = this; ancestor != null; ancestor = ancestor.getParent())
            thisAncestors.addFirst(ancestor);
        for(Edit ancestor = other; ancestor != null; ancestor = ancestor.getParent())
            otherAncestors.addFirst(ancestor);
        
        //remove equivalent ancestors
        //the loop breaks before one of the list is empty, because the last elements are the edits themselves,
        //which are distinct (checked before)
        //instance compare is ok because same Game has same GameState
        while(thisAncestors.getFirst() == otherAncestors.getFirst()) {
            thisAncestors.removeFirst();
            otherAncestors.removeFirst();
        }
        Edit sharedAncestor = thisAncestors.getFirst().getParent();
        assert sharedAncestor == otherAncestors.getFirst().getParent();
        
        List<Edit> edits = sharedAncestor.getChildren();
        int thisIndex = edits.indexOf(thisAncestors.getFirst()), otherIndex = edits.indexOf(otherAncestors.getFirst());
        assert thisIndex != -1;
        assert otherIndex != -1;
        return thisIndex - otherIndex;
    }
    
    /**
     * Returns a string describing the edit. Compound edits return a description or {@code null}.
     */
    @Override
    public abstract String toString();
    
    /**
     * Returns a string describing the edit. Compound edits either return a description or a list of all child
     * edits.
     */
    public String deepToString() {
        return toString();
    }
}
