/**
 * SetCharacteristicEffect.java
 * 
 * Created on 12.07.2009
 */

package net.slightlymagic.laterna.magica.effect.characteristic;


import java.util.Set;

import net.slightlymagic.laterna.magica.characteristic.SetCharacteristic;


/**
 * The class SetCharacteristicEffect. A set-characteristic effect can work in four different ways, described in
 * {@link SetCharacteristic}.
 * 
 * @version V0.0 12.07.2009
 * @author Clemens Koza
 */
public interface SetCharacteristicEffect<T> extends CharacteristicEffect {
    public static enum Mode {
        /**
         * Constant for {@link #getMode()}, meaning that the values are used as the only ones contained.
         */
        SETTING,
        /**
         * Constant for {@link #getMode()}, meaning that the values are used as the only ones not contained.
         */
        UNSETTING,
        /**
         * Constant for {@link #getMode()}, meaning that the values are added to the already contained values.
         */
        ADDING,
        /**
         * Constant for {@link #getMode()}, meaning that the values are substracted from the already contained
         * values.
         */
        REMOVING;
    }
    
    /**
     * Returns how the effect modifies the SetCharacteristic.
     */
    public Mode getMode();
    
    /**
     * Returns the values the effect specifies. Interpretation of this depends on {@link #getMode()}.
     */
    public Set<T> getSetValues();
}
