/**
 * EmptyPoolsAction.java
 * 
 * Created on 18.04.2010
 */

package net.slightlymagic.laterna.magica.action.turnBased;


import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.impl.AbstractGameContent;
import net.slightlymagic.laterna.magica.player.Player;


/**
 * The class EmptyPoolsAction.
 * 
 * @version V0.0 18.04.2010
 * @author Clemens Koza
 */
public class EmptyPoolsAction extends AbstractGameContent implements TurnBasedAction {
    public EmptyPoolsAction(Game game) {
        super(game);
    }
    
    public boolean execute() {
        for(Player p:getGame().getPlayers())
            p.getManaPool().emptyPool();
        return true;
    }
}
