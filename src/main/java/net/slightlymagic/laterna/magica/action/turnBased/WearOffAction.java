/**
 * WearOffAction.java
 * 
 * Created on 01.09.2010
 */

package net.slightlymagic.laterna.magica.action.turnBased;


import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.action.AbstractGameAction;
import net.slightlymagic.laterna.magica.card.CardObject;


/**
 * The class WearOffAction.
 * 
 * @version V0.0 01.09.2010
 * @author Clemens Koza
 */
public class WearOffAction extends AbstractGameAction implements TurnBasedAction {
    public WearOffAction(Game game) {
        super(game);
    }
    
    @Override
    public boolean execute() {
        for(MagicObject o:getGame().getBattlefield().getCards())
            ((CardObject) o).setMarkedDamage(0);
        return true;
    }
}
