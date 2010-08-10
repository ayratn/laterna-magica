/**
 * Actor.java
 * 
 * Created on 09.04.2010
 */

package net.slightlymagic.laterna.magica.player;


import java.util.Set;

import net.slightlymagic.laterna.magica.GameContent;
import net.slightlymagic.laterna.magica.action.play.PlayAction;
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
}
