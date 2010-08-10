/**
 * OverridingCharacteristicEffect.java
 * 
 * Created on 12.07.2009
 */

package net.slightlymagic.laterna.magica.effect.characteristic;


/**
 * The class OverridingCharacteristicEffect. A characteristic setting effect tries to override all other effects
 * that modify the characteristic.
 * 
 * @version V0.0 12.07.2009
 * @author Clemens Koza
 */
public interface OverridingCharacteristicEffect<T> extends CharacteristicEffect {
    /**
     * Returns the value that this effect tries to set.
     */
    public T getOverridingValue();
}
