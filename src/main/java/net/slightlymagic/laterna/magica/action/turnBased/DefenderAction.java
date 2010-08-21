/**
 * DefenderAction.java
 * 
 * Created on 20.08.2010
 */

package net.slightlymagic.laterna.magica.action.turnBased;


import net.slightlymagic.laterna.magica.Combat;
import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.action.AbstractGameAction;
import net.slightlymagic.laterna.magica.edit.CompoundEdit;
import net.slightlymagic.laterna.magica.player.Player;


/**
 * The class DefenderAction.
 * 
 * @version V0.0 20.08.2010
 * @author Clemens Koza
 */
public class DefenderAction extends AbstractGameAction implements TurnBasedAction {
    public DefenderAction(Game game) {
        super(game);
    }
    
    @Override
    public boolean execute() {
        Combat combat = getGame().getCombat();
        Player active = getGame().getTurnStructure().getActivePlayer();
        combat.setAction(Type.DEFENDER);
        
        CompoundEdit edit = new CompoundEdit(getGame(), true, "Choose defending players");
        
        active.getActor().setDefendingPlayers();
        
        edit.end();
        
        return true;
    }
}
