/**
 * OrderAttackersAction.java
 * 
 * Created on 20.08.2010
 */

package net.slightlymagic.laterna.magica.action.turnBased;


import java.util.List;

import net.slightlymagic.laterna.magica.Combat;
import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.action.AbstractGameAction;
import net.slightlymagic.laterna.magica.edit.CompoundEdit;
import net.slightlymagic.laterna.magica.edit.Edit;
import net.slightlymagic.laterna.magica.player.Player;


/**
 * The class OrderAttackersAction.
 * 
 * @version V0.0 20.08.2010
 * @author Clemens Koza
 */
public class OrderAttackersAction extends AbstractGameAction implements TurnBasedAction {
    public OrderAttackersAction(Game game) {
        super(game);
    }
    
    @Override
    public boolean execute() {
        Combat combat = getGame().getCombat();
        List<Player> defending = combat.getDefendingPlayers();
        combat.setAction(Type.ORDER_ATTACKERS);
        
        for(Player p:defending) {
            Edit ref = getGame().getGameState().getCurrent();
            do {
                getGame().getGameState().stepTo(ref);
                CompoundEdit edit = new CompoundEdit(getGame(), true, "Order blockers' attackers for " + p);
                p.getActor().orderAttackers();
                edit.end();
            } while(!combat.isLegalAttackersAssignmentOrder(p));
        }
        
        return true;
    }
}
