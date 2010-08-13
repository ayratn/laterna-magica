/**
 * TurnStructure.java
 * 
 * Created on 05.09.2009
 */

package net.slightlymagic.laterna.magica.turnStructure;


import java.util.Iterator;

import net.slightlymagic.laterna.magica.GameContent;
import net.slightlymagic.laterna.magica.event.ActiveChangedListener;
import net.slightlymagic.laterna.magica.player.Player;


/**
 * The class TurnStructure. This class is responsible to ensure the correct turn order of players in the game.
 * 
 * @version V0.0 05.09.2009
 * @author Clemens Koza
 */
public interface TurnStructure extends GameContent {
    /**
     * Schedules the player to take an extra turn after the current one. The last turn scheduled this way is taken
     * first.
     */
    public void takeExtraTurn(Player p);
    
    /**
     * Schedules the player to skip his next turn. The next time the player would take a turn, for whatever reason,
     * that turn is skipped.
     */
    public void skipNextTurn(Player p);
    
    /**
     * Advances to the next turn. This method is ONLY to be called from the {@link PhaseStructure}.
     */
    public void nextTurn();
    
    /**
     * Returns the increasing turn number, starting with 1. The turn number increases every time the beginning
     * player regularly becomes/would become the active player. Thus, a turn (normally) consists of a turn of each
     * player, and additional/skipped turns don't influence the turn number.
     */
    public int getTurnNumber();
    
    /**
     * Returns the player whose turn it is.
     */
    public Player getActivePlayer();
    
    public Iterator<ActiveChangedListener> getActiveChangedListeners();
    
    public void removeActiveChangedListener(ActiveChangedListener l);
    
    public void addActiveChangedListener(ActiveChangedListener l);
}
