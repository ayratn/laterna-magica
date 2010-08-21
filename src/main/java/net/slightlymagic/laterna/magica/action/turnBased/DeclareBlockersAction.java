/**
 * DeclareBlockersAction.java
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
 * The class DeclareBlockersAction.
 * 
 * @version V0.0 20.08.2010
 * @author Clemens Koza
 */
public class DeclareBlockersAction extends AbstractGameAction implements TurnBasedAction {
    public DeclareBlockersAction(Game game) {
        super(game);
    }
    
    @Override
    public boolean execute() {
        Combat combat = getGame().getCombat();
        List<Player> defending = combat.getDefendingPlayers();
        combat.setAction(Type.DECLARE_BLOCKERS);
        
        for(Player p:defending) {
            Edit ref = getGame().getGameState().getCurrent();
            do {
                do {
                    getGame().getGameState().stepTo(ref);
                    CompoundEdit edit = new CompoundEdit(getGame(), true, "Choose blockers for " + p);
                    p.getActor().declareBlockers();
                    edit.end();
                } while(!combat.isLegalBlockers(p));
            } while(!combat.getBlockersCost(p).execute());
        }
        
        return true;
    }
}
