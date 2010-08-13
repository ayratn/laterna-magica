/**
 * GlobalEffects.java
 * 
 * Created on 12.07.2009
 */

package net.slightlymagic.laterna.magica.effect;


import java.util.Map;

import net.slightlymagic.laterna.magica.GameContent;
import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.effect.characteristic.CharacteristicEffect;

import com.google.common.base.Predicate;


/**
 * The class GlobalEffects. A game of magic has exactly one GlobalEffects-Object, which stores all active global
 * continuous effects. <i>Active</i> is defined as
 * 
 * <p>
 * {@magic.ruleRef 20100716/R6113b} The effect applies at all times that the permanent generating
 * it is on the battlefield or the object generating it is in the appropriate zone.
 * </p>
 * 
 * Static abilities make sure that their effects are in the global effects as long as the ability's card is in the
 * appropriate zone.
 * 
 * @version V0.0 12.07.2009
 * @author Clemens Koza
 */
public interface GlobalEffects extends GameContent {
    /**
     * Returns the characteristic effects that currently apply in game.
     * 
     * This map only contains the effects from static abilities. In other words, effects for which the affected
     * objects may change after the effect was created. The value of every effect is a predicate that
     * differentiates between affected and non-affected objects.
     * 
     * A static ability must store its effects, so that it can remove them when it stops to apply. Because
     * duplicate keys are not possible, effects can not be shared between multiple abilities, not even between
     * different instances of the same ability!
     * 
     * The returned map is not modifiable.
     */
    public Map<CharacteristicEffect, Predicate<? super MagicObject>> getEffects();
    
    /**
     * Puts the specified effect with its predicate into the map.
     */
    public void put(CharacteristicEffect effect, Predicate<? super MagicObject> matcher);
    
    /**
     * Removes the specified effect from the map.
     */
    public void remove(CharacteristicEffect effect);
}
