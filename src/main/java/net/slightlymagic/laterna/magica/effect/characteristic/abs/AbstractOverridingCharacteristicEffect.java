/**
 * AbstractOverridingCharacteristicEffect.java
 * 
 * Created on 03.09.2009
 */

package net.slightlymagic.laterna.magica.effect.characteristic.abs;


import static java.lang.String.*;
import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.characteristics.Characteristics;
import net.slightlymagic.laterna.magica.effect.characteristic.OverridingCharacteristicEffect;


/**
 * The class AbstractOverridingCharacteristicEffect.
 * 
 * @version V0.0 03.09.2009
 * @author Clemens Koza
 */
public abstract class AbstractOverridingCharacteristicEffect<T> extends AbstractCharacteristicEffect implements OverridingCharacteristicEffect<T> {
    public AbstractOverridingCharacteristicEffect(Game game, Layer l, Characteristics c) {
        super(game, l, c);
    }
    
    @Override
    public String toString() {
        return format("OverridingCharacteristicEffect %s(%s)", getCharacteristic(), getOverridingValue());
    }
}
