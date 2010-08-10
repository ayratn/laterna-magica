/**
 * NonStaticAbility.java
 * 
 * Created on 21.04.2010
 */

package net.slightlymagic.laterna.magica.ability;


/**
 * The class NonStaticAbility.
 * 
 * @version V0.0 21.04.2010
 * @author Clemens Koza
 */
public interface NonStaticAbility extends Ability {
    /**
     * Returns if this ability is a mana ability or not. Mana abilities are immediately resolved after the usual
     * process of playing, including declaring targets etc., whereas non-mana abilities are simply put onto the
     * stack.
     */
    public boolean isManaAbility();
}
