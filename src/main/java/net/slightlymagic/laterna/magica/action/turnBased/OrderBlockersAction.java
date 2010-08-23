/**
 * OrderBlockersAction.java
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
 * The class OrderBlockersAction.
 * 
 * @version V0.0 20.08.2010
 * @author Clemens Koza
 */
public class OrderBlockersAction extends AbstractGameAction implements TurnBasedAction {
    public OrderBlockersAction(Game game) {
        super(game);
    }
    
    @Override
    public boolean execute() {
        Combat combat = getGame().getCombat();
        List<Player> attacking = combat.getAttackingPlayers();
        combat.setAction(Type.ORDER_BLOCKERS);
        
        for(Player p:attacking) {
            combat.setAttackerAssignmentOrderPlayer(p);
            Edit ref = getGame().getGameState().getCurrent();
            do {
                getGame().getGameState().stepTo(ref);
                CompoundEdit edit = new CompoundEdit(getGame(), true, "Order attackers' blockers for " + p);
                p.getActor().orderBlockers();
                edit.end();
            } while(!combat.isLegalAttackersAssignmentOrder(p));
        }
        
        return true;
    }
}
