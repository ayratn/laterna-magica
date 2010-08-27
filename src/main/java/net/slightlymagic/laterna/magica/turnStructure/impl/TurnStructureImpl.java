/**
 * TurnStructureImpl.java
 * 
 * Created on 11.10.2009
 */

package net.slightlymagic.laterna.magica.turnStructure.impl;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.slightlymagic.beans.properties.Property;
import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.edit.CompoundEdit;
import net.slightlymagic.laterna.magica.event.ActiveChangedListener;
import net.slightlymagic.laterna.magica.impl.AbstractGameContent;
import net.slightlymagic.laterna.magica.player.Player;
import net.slightlymagic.laterna.magica.turnStructure.TurnStructure;


/**
 * The class TurnStructureImpl.
 * 
 * @version V0.0 11.10.2009
 * @author Clemens Koza
 */
public class TurnStructureImpl extends AbstractGameContent implements TurnStructure {
    /**
     * The number of the turn in the turn sequence, starting with 1.
     */
    private Property<Integer> turnNumber;
    /**
     * The index of the player whose turn would currently be.
     * 
     * This number does not necessarily reflect the player whose turn currently is: For example, in a game with
     * players A, B and C, C takes an extra turn after A's turn. After C's turn, B should continue. If the index
     * were that of C, A would have taken the next turn.
     */
    private Property<Integer> regularPlayer;
    /**
     * Extra turns taken after the current turn
     */
    private List<Player>      addedTurns;
    /**
     * Skipped turns
     */
    private List<Player>      skippedTurns;
    /**
     * The really active Player
     */
    private Property<Player>  activePlayer;
    
    public TurnStructureImpl(Game game) {
        super(game);
        turnNumber = properties.property("turnNumber", 0);
        activePlayer = properties.property("activePlayer");
        //don't provide property change events for this one
        regularPlayer = properties.property("regularPlayer", -1);
        
        addedTurns = properties.list("addedTurns", new LinkedList<Player>());
        skippedTurns = properties.list("skippedTurns", new LinkedList<Player>());
    }
    
    public void takeExtraTurn(Player p) {
        if(p.getGame() != getGame()) throw new IllegalArgumentException();
        addedTurns.add(0, p);
    }
    
    public void skipNextTurn(Player p) {
        if(p.getGame() != getGame()) throw new IllegalArgumentException();
        skippedTurns.add(p);
    }
    
    public void nextTurn() {
        CompoundEdit e = new CompoundEdit(getGame(), true, "Go to next turn");
        Player oldActive = regularPlayer.getValue() == -1? null:getActivePlayer();
        
        do {
            //go to next turn considering extra turns
            if(!addedTurns.isEmpty()) {
                activePlayer.setValue(addedTurns.remove(0));
            } else {
                regularPlayer.setValue((regularPlayer.getValue() + 1) % getGame().getPlayers().size());
                if(regularPlayer.getValue().intValue() == 0) turnNumber.setValue(turnNumber.getValue() + 1);
                activePlayer.setValue(getGame().getPlayers().get(regularPlayer.getValue()));
            }
            //check skipped turns; move on if the new turn should be skipped
        } while(skippedTurns.remove(activePlayer.getValue()));
        
        Player newActive = getActivePlayer();
        fireNextActive(oldActive, newActive);
        
        e.end();
    }
    
    public Player getActivePlayer() {
        return activePlayer.getValue();
    }
    
    public int getTurnNumber() {
        return turnNumber.getValue();
    }
    
    private void fireNextActive(Player oldActive, Player newActive) {
        for(Iterator<ActiveChangedListener> it = getActiveChangedListeners(); it.hasNext();)
            it.next().nextActive(oldActive, newActive);
    }
    
    public void addActiveChangedListener(ActiveChangedListener l) {
        listeners.add(ActiveChangedListener.class, l);
    }
    
    public void removeActiveChangedListener(ActiveChangedListener l) {
        listeners.remove(ActiveChangedListener.class, l);
    }
    
    @SuppressWarnings("unchecked")
    public Iterator<ActiveChangedListener> getActiveChangedListeners() {
        return listeners.getIterator(ActiveChangedListener.class);
    }
}
