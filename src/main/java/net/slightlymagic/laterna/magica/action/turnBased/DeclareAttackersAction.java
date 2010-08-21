/**
 * DeclareAttackersAction.java
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
 * The class DeclareAttackersAction.
 * 
 * @version V0.0 20.08.2010
 * @author Clemens Koza
 */
public class DeclareAttackersAction extends AbstractGameAction implements TurnBasedAction {
    public DeclareAttackersAction(Game game) {
        super(game);
    }
    
    @Override
    public boolean execute() {
        Combat combat = getGame().getCombat();
        List<Player> attacking = combat.getAttackingPlayers();
        combat.setAction(Type.DECLARE_ATTACKERS);
        
        for(Player p:attacking) {
            Edit ref = getGame().getGameState().getCurrent();
            do {
                do {
                    getGame().getGameState().stepTo(ref);
                    CompoundEdit edit = new CompoundEdit(getGame(), true, "Choose attackers for " + p);
                    p.getActor().declareAttackers();
                    edit.end();
                } while(!combat.isLegalAttackers(p));
                combat.tapAttackers(p);
            } while(!combat.getAttackersCost(p).execute());
        }
        
        if(combat.getAttackers().isEmpty()) {
            //TODO skip to end of combat step
        }
        
        return true;
    }
}
