/**
 * LoseOnLifeAction.java
 * 
 * Created on 01.09.2010
 */

package net.slightlymagic.laterna.magica.action.stateBased;


import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.action.AbstractGameAction;
import net.slightlymagic.laterna.magica.player.Player;


/**
 * The class LoseOnLifeAction.
 * 
 * @version V0.0 01.09.2010
 * @author Clemens Koza
 */
public class LoseOnLifeAction extends AbstractGameAction implements StateBasedAction {
    public LoseOnLifeAction(Game game) {
        super(game);
    }
    
    @Override
    public boolean execute() {
        boolean b = false;
        for(Player p:getGame().getPlayersInGame())
            if(p.getLifeTotal().getLifeTotal() <= 0) {
                p.loseGame();
                b = true;
            }
        return b;
    }
}
