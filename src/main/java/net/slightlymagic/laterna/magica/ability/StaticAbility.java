/**
 * StaticAbility.java
 * 
 * Created on 15.04.2010
 */

package net.slightlymagic.laterna.magica.ability;


import java.util.Map;

import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.effect.ContinuousEffect;

import com.google.common.base.Predicate;


/**
 * The class StaticAbility. A static ability has a continuous effect as long as a condition is satisfied. For
 * example, "All creatures you control get +1/+1." has the implied condition that the card the ability is on must
 * be on the battlefield.
 * 
 * TODO restructure GlobalEffects:
 * 
 * instead of storing effect and predicate, store all static abilities in the game (regardless of {@link #apply()})
 * when iterating the effect, {@link #apply()} is used. only if true, the map and its predicates is considered.
 * 
 * @version V0.0 15.04.2010
 * @author Clemens Koza
 */
public interface StaticAbility extends Ability {
    /**
     * Returns the condition for this static ability to apply. In addition to checking the predicate from the map,
     * this boolean is queried every time the continuous effect would be considered.
     */
    public boolean apply();
    
    public Map<ContinuousEffect, Predicate<? super MagicObject>> getEffects();
}
