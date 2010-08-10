/**
 * AbilityCharacteristics.java
 * 
 * Created on 21.04.2010
 */

package net.slightlymagic.laterna.magica.characteristic;


import net.slightlymagic.laterna.magica.ability.NonStaticAbility;


/**
 * The class AbilityCharacteristics represents the characteristics of an activated or triggered ability on the
 * stack.
 * 
 * @version V0.0 21.04.2010
 * @author Clemens Koza
 */
public interface AbilityCharacteristics extends ObjectCharacteristics {
    public NonStaticAbility getAbility();
}
