/**
 * Characteristic.java
 * 
 * Created on 03.09.2009
 */

package net.slightlymagic.laterna.magica.characteristic;


import net.slightlymagic.laterna.magica.GameContent;
import net.slightlymagic.laterna.magica.characteristics.Characteristics;


/**
 * <p>
 * The class Characteristic.
 * </p>
 * 
 * @version V0.0 03.09.2009
 * @author Clemens Koza
 */
public interface Characteristic extends GameContent {
    /**
     * <p>
     * Returns the characteristics to which this specific characteristic belongs
     * </p>
     */
    public ObjectCharacteristics getCharacteristics();
    
    /**
     * <p>
     * Returns what characteristic this object represents
     * </p>
     */
    public Characteristics getCharacteristic();
    
    /**
     * Returns {@link Characteristics} of this characteristic.
     */
    @Override
    public String toString();
}
