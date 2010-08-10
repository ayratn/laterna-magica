/**
 * GameAction.java
 * 
 * Created on 01.04.2010
 */

package net.slightlymagic.laterna.magica.action;


import net.slightlymagic.laterna.magica.GameContent;


/**
 * The class GameAction. An action executes arbitrary changes to the game state. The difference to an edit is that
 * edits are small, atomic changes while actions are bigger changes which (probably) may be executed/undone in
 * parts.
 * 
 * @version V0.0 01.04.2010
 * @author Clemens Koza
 */
public interface GameAction extends GameContent {
    /**
     * Executes the action, affecting the game state. Returned is if the action was fully executed. For example,
     * the action "Tap [a permanent]" returns false if the permanent was already tapped, was not a permanent any
     * more, or could not be tapped for another reason. It would, however, return true, if it was replaced by
     * another action.
     * 
     * An action consisting of multiple child actions returns true only if all of those actions returned true.
     */
    public boolean execute();
}
