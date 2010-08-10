/**
 * OverridingCharacteristic.java
 * 
 * Created on 12.07.2009
 */

package net.slightlymagic.laterna.magica.characteristic;


/**
 * <p>
 * The class OverridingCharacteristic. This interface defines the capabilities for a single-value characteristic,
 * which can be overridden by a Continuous Effect.
 * </p>
 * 
 * @version V0.0 12.07.2009
 * @author Clemens Koza
 */
public interface OverridingCharacteristic<T> extends Characteristic {
    /**
     * <p>
     * Returns the value of the characteristic, determined by taking the latest effect.
     * </p>
     */
    public T getValue();
}
