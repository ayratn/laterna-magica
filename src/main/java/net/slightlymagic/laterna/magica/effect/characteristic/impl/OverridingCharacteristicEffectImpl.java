/**
 * OverridingCharacteristicEffectImpl.java
 * 
 * Created on 10.09.2009
 */

package net.slightlymagic.laterna.magica.effect.characteristic.impl;


import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.characteristics.Characteristics;
import net.slightlymagic.laterna.magica.effect.characteristic.abs.AbstractOverridingCharacteristicEffect;


/**
 * The class OverridingCharacteristicEffectImpl.
 * 
 * @version V0.0 10.09.2009
 * @author Clemens Koza
 */
public class OverridingCharacteristicEffectImpl<T> extends AbstractOverridingCharacteristicEffect<T> {
    private T value;
    
    public OverridingCharacteristicEffectImpl(Game game, Layer l, Characteristics c, T value) {
        super(game, l, c);
        this.value = value;
    }
    
    public T getOverridingValue() {
        return value;
    }
}
