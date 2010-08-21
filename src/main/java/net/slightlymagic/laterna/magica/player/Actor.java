/**
 * Actor.java
 * 
 * Created on 09.04.2010
 */

package net.slightlymagic.laterna.magica.player;


import java.util.Collection;
import java.util.Set;

import net.slightlymagic.laterna.magica.Combat;
import net.slightlymagic.laterna.magica.Combat.Attacker;
import net.slightlymagic.laterna.magica.Combat.Blocker;
import net.slightlymagic.laterna.magica.Combat.Defender;
import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.GameContent;
import net.slightlymagic.laterna.magica.action.play.PlayAction;
import net.slightlymagic.laterna.magica.card.CardObject;
import net.slightlymagic.laterna.magica.cost.ManaCost;
import net.slightlymagic.laterna.magica.effect.replacement.ReplaceableEvent;
import net.slightlymagic.laterna.magica.effect.replacement.ReplacementEffect;
import net.slightlymagic.laterna.magica.mana.Mana;
import net.slightlymagic.laterna.magica.mana.ManaSequence;


/**
 * The interface Actor is the "controller" whereas Player is the model. An actor defines the behavior of the player
 * and may use any strategy, for example a GUI for user input, a remotely connected actor, or an AI algorithm.
 * 
 * @version V0.0 09.04.2010
 * @author Clemens Koza
 */
public interface Actor extends GameContent {
    /**
     * Returns the player this actor represents.
     */
    public Player getPlayer();
    
    /**
     * Returns the action to perform while a player has priority. Returns {@code null} if the player passes
     * priority.
     */
    public PlayAction getAction();
    
    /**
     * Returns the Replacement effect chosen by the player to be applied to a {@link ReplaceableEvent}. The effect
     * must be one of {@code effects}.
     * 
     * @param event The event to replace
     * @param effects The applicable effects. Will be neither {@code null} nor empty.
     */
    public ReplacementEffect getReplacementEffect(ReplaceableEvent event, Set<ReplacementEffect> effects);
    
    /**
     * Resolves a cost to pay. The returned cost will have all variable and hybrid mana symbols replaced with
     * numeric or one of the partial symbols respectively.
     * 
     * @param cost The cost to resolve
     */
    public ManaSequence resolveCost(ManaSequence cost);
    
    /**
     * Returns the mana which should be used to pay for the specified mana cost. The cost must not contain hybrid
     * or variable symbols, but may contain snow symbols. If the player can't pay the cost (or doesn't want to),
     * {@code null} is returned.
     */
    public Set<Mana> getManaToPay(ManaCost cost);
    
    //Combat
    
    /**
     * Sets the defending players for the game's combat object
     * 
     * @see Game#getCombat()
     * @see Combat#setDefendingPlayers(Set)
     */
    public void setDefendingPlayers();
    
    /**
     * Declares attackers for the game's combat object
     * 
     * @see Game#getCombat()
     * @see Combat#declareAttacker(CardObject)
     * @see Combat#assignAttacker(Attacker, Defender)
     */
    public void declareAttackers();
    
    /**
     * Declares blockers for the game's combat object
     * 
     * @see Game#getCombat()
     * @see Combat#declareBlocker(CardObject)
     * @see Combat#assignBlocker(Blocker, Attacker)
     */
    public void declareBlockers();
    
    /**
     * Sets the damage assignment order of blockers for all attacking creatures by this player
     * 
     * @see Game#getCombat()
     * @see Attacker#setDamageAssignmentOrder(java.util.List)
     */
    public void orderBlockers();
    
    /**
     * Sets the damage assignment order of attackers for all blocking creatures by this player
     * 
     * @see Game#getCombat()
     * @see Blocker#setDamageAssignmentOrder(java.util.List)
     */
    public void orderAttackers();
    
    /**
     * Returns the attacker for which the player wishes to assign damage. That creature's damage must be assigned
     * completely before the player can assign the next creature's damage.
     * 
     * @param attackers The attackers for which damage was not assigned yet
     * @return an attacker from the collection
     */
    public Attacker getAttackerToAssignDamage(Collection<? extends Attacker> attackers);
    
    /**
     * Assigns all damage for the given attacker.
     * 
     * @see Attacker#setDamageAssignmentOrder(java.util.List)
     */
    public void assignDamage(Attacker attacker);
    
    /**
     * Returns the blocker for which the player wishes to assign damage. That creature's damage must be assigned
     * completely before the player can assign the next creature's damage.
     * 
     * @param blockers The blockers for which damage was not assigned yet
     * @return a blocker from the collection
     */
    public Blocker getBlockerToAssignDamage(Collection<? extends Blocker> blockers);
    
    /**
     * Assigns all damage for the given blocker.
     * 
     * @see Blocker#setDamageAssignmentOrder(java.util.List)
     */
    public void assignDamage(Blocker blocker);
}
