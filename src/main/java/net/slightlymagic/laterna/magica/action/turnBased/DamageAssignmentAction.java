/**
 * DamageAssignmentAction.java
 * 
 * Created on 20.08.2010
 */

package net.slightlymagic.laterna.magica.action.turnBased;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.slightlymagic.laterna.magica.Combat;
import net.slightlymagic.laterna.magica.Combat.Attacker;
import net.slightlymagic.laterna.magica.Combat.Blocker;
import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.action.AbstractGameAction;
import net.slightlymagic.laterna.magica.edit.CompoundEdit;
import net.slightlymagic.laterna.magica.edit.Edit;
import net.slightlymagic.laterna.magica.player.Player;


/**
 * The class DamageAssignmentAction.
 * 
 * @version V0.0 20.08.2010
 * @author Clemens Koza
 */
public class DamageAssignmentAction extends AbstractGameAction implements TurnBasedAction {
    public DamageAssignmentAction(Game game) {
        super(game);
    }
    
    @Override
    public boolean execute() {
        Combat combat = getGame().getCombat();
        //TODO order players APNAP
        List<Player> attacking = combat.getAttackingPlayers();
        List<Player> defending = combat.getDefendingPlayers();
        combat.setAction(Type.DAMAGE_ASSIGNMENT);
        combat.startCombatDamageStep();
        
        for(Player p:attacking) {
            CompoundEdit edit = new CompoundEdit(getGame(), true, "Assign attacker damage for " + p);
            
            List<Attacker> attackers = new ArrayList<Attacker>(combat.getAttackers());
            for(Iterator<Attacker> it = attackers.iterator(); it.hasNext();)
                if(it.next().getAttacker().getController() != p) it.remove();
            
            while(!attackers.isEmpty()) {
                Attacker a;
                while(!attackers.contains(a = p.getActor().getAttackerToAssignDamage(attackers)));
                combat.setAttackerAssignmentAttacker(a);
                
                Edit ref = getGame().getGameState().getCurrent();
                do {
                    getGame().getGameState().stepTo(ref);
                    CompoundEdit edit2 = new CompoundEdit(getGame(), true, "Assign damage for " + a);
                    
                    p.getActor().assignDamage(a);
                    
                    edit2.end();
                } while(!combat.isLegalAttackerAssignment(a));
                
                attackers.remove(a);
            }
            edit.end();
        }
        
        for(Player p:defending) {
            CompoundEdit edit = new CompoundEdit(getGame(), true, "Assign blocker damage for " + p);
            
            List<Blocker> blockers = new ArrayList<Blocker>(combat.getBlockers());
            for(Iterator<Blocker> it = blockers.iterator(); it.hasNext();)
                if(it.next().getBlocker().getController() != p) it.remove();
            
            while(!blockers.isEmpty()) {
                Blocker b;
                while(!blockers.contains(b = p.getActor().getBlockerToAssignDamage(blockers)));
                combat.setBlockerAssignmentBlocker(b);
                
                Edit ref = getGame().getGameState().getCurrent();
                do {
                    getGame().getGameState().stepTo(ref);
                    CompoundEdit edit2 = new CompoundEdit(getGame(), true, "Assign damage for " + b);
                    
                    p.getActor().assignDamage(b);
                    
                    edit2.end();
                } while(!combat.isLegalBlockerAssignment(b));
                
                blockers.remove(b);
            }
            edit.end();
        }
        
        return true;
    }
}
