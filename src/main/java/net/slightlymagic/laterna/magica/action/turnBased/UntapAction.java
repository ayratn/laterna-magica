/**
 * UntapAction.java
 * 
 * Created on 18.04.2010
 */

package net.slightlymagic.laterna.magica.action.turnBased;


import java.util.Collection;

import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.action.AbstractGameAction;
import net.slightlymagic.laterna.magica.card.CardObject;
import net.slightlymagic.laterna.magica.card.State.StateType;
import net.slightlymagic.laterna.magica.player.Player;



/**
 * The class UntapAction.
 * 
 * @version V0.0 18.04.2010
 * @author Clemens Koza
 */
public class UntapAction extends AbstractGameAction implements TurnBasedAction {
    public UntapAction(Game game) {
        super(game);
    }
    
    @Override
    public boolean execute() {
        Player p = getGame().getTurnStructure().getActivePlayer();
        Collection<MagicObject> cards = getGame().getBattlefield().getCards();
        for(MagicObject card:cards) {
            //only CardObjects are on the battlefield. The stack is the only zone that can contain other magic objects
            assert card instanceof CardObject;
            if(card.getController() == p) ((CardObject) card).getState().setState(StateType.TAPPED, false);
        }
        return false;
    }
}
