/**
 * NoopActor.java
 * 
 * Created on 09.04.2010
 */

package net.slightlymagic.laterna.magica.player.impl;


import java.util.Collection;
import java.util.Set;

import net.slightlymagic.laterna.magica.Combat.Attacker;
import net.slightlymagic.laterna.magica.Combat.Blocker;
import net.slightlymagic.laterna.magica.action.GameAction;
import net.slightlymagic.laterna.magica.action.play.ActivateAction;
import net.slightlymagic.laterna.magica.action.play.PlayAction;
import net.slightlymagic.laterna.magica.cost.ManaCost;
import net.slightlymagic.laterna.magica.effect.replacement.ReplaceableEvent;
import net.slightlymagic.laterna.magica.effect.replacement.ReplacementEffect;
import net.slightlymagic.laterna.magica.mana.Mana;
import net.slightlymagic.laterna.magica.mana.ManaSequence;
import net.slightlymagic.laterna.magica.player.Player;


/**
 * The class NoopActor. An actor that always passes priority and makes default choices.
 * 
 * @version V0.0 09.04.2010
 * @author Clemens Koza
 */
public class NoopActor extends AbstractMagicActor {
    public NoopActor(Player player) {
        super(player);
    }
    
    /**
     * Always returns null, which means to pass priority.
     */
    public PlayAction getAction() {
        return null;
    }
    
    /**
     * Always returns null, which means not to activate abilities.
     */
    @Override
    public ActivateAction activateManaAbility(GameAction cost) {
        return null;
    }
    
    /**
     * Returns the first effect returned by the iterator.
     */
    public ReplacementEffect getReplacementEffect(ReplaceableEvent event, Set<ReplacementEffect> effects) {
        return effects.iterator().next();
    }
    
    /**
     * Returns the cost if it is resolved, otherwise returns null.
     */
    public ManaSequence resolveCost(ManaSequence cost) {
        if(cost.isResolved()) return cost;
        else return null;
    }
    
    /**
     * Always returns {@code null}, which means not to pay the cost.
     */
    public Set<Mana> getManaToPay(ManaCost cost) {
        return null;
    }
    
    @Override
    public void setDefendingPlayers() {
        //TODO implement
        //this method has to make a default choice
    }
    
    @Override
    public void declareAttackers() {
        //don't declare attackers
    }
    
    @Override
    public void declareBlockers() {
        //don't declare blockers
    }
    
    @Override
    public void orderAttackers() {
        //since the actor didn't block, nothing to do
    }
    
    @Override
    public void orderBlockers() {
        //since the actor didn't attack, nothing to do
    }
    
    @Override
    public Attacker getAttackerToAssignDamage(Collection<? extends Attacker> attackers) {
        //should never get here, as there are no attackers by this player
        throw new AssertionError();
    }
    
    @Override
    public void assignDamage(Attacker attacker) {
        //should never get here, as there are no attackers by this player
        throw new AssertionError();
    }
    
    @Override
    public Blocker getBlockerToAssignDamage(Collection<? extends Blocker> blockers) {
        //should never get here, as there are no blockers by this player
        throw new AssertionError();
    }
    
    @Override
    public void assignDamage(Blocker blocker) {
        //should never get here, as there are no blockers by this player
        throw new AssertionError();
    }
}
