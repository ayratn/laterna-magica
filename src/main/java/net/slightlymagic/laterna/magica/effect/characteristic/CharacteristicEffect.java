/**
 * CharacteristicEffect.java
 * 
 * Created on 12.07.2009
 */

package net.slightlymagic.laterna.magica.effect.characteristic;


import net.slightlymagic.laterna.magica.characteristics.Characteristics;
import net.slightlymagic.laterna.magica.effect.ContinuousEffect;


/**
 * The class CharacteristicEffect.
 * 
 * @version V0.0 12.07.2009
 * @author Clemens Koza
 */
public interface CharacteristicEffect extends ContinuousEffect {
    /**
     * Returns the characteristic affected by this effect
     */
    public Characteristics getCharacteristic();
}
