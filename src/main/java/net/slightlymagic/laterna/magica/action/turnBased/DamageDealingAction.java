/**
 * DamageDealingAction.java
 * 
 * Created on 20.08.2010
 */

package net.slightlymagic.laterna.magica.action.turnBased;


import net.slightlymagic.laterna.magica.Combat;
import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.action.AbstractGameAction;


/**
 * The class DamageDealingAction.
 * 
 * @version V0.0 20.08.2010
 * @author Clemens Koza
 */
public class DamageDealingAction extends AbstractGameAction implements TurnBasedAction {
    public DamageDealingAction(Game game) {
        super(game);
    }
    
    @Override
    public boolean execute() {
        Combat combat = getGame().getCombat();
        combat.setAction(Type.DAMAGE_DEALING);
        
        combat.dealDamage();
        
        if(combat.nextDamageStep()) {
            //TODO add an additional damage step to the phase structure
        }
        
        return true;
    }
}
