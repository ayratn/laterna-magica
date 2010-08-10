/**
 * NoopActor.java
 * 
 * Created on 09.04.2010
 */

package net.slightlymagic.laterna.magica.player.impl;


import java.util.Set;

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
public class NoopActor extends AbstractActor {
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
}
