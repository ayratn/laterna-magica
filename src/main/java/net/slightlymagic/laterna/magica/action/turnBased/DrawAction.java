/**
 * DrawAction.java
 * 
 * Created on 02.04.2010
 */

package net.slightlymagic.laterna.magica.action.turnBased;


import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.action.AbstractGameAction;
import net.slightlymagic.laterna.magica.player.Player;


/**
 * The class DrawAction.
 * 
 * @version V0.0 02.04.2010
 * @author Clemens Koza
 */
public class DrawAction extends AbstractGameAction implements TurnBasedAction {
    public DrawAction(Game game) {
        super(game);
    }
    
    @Override
    public boolean execute() {
        Player p = getGame().getTurnStructure().getActivePlayer();
        return p.drawCard();
    }
}
