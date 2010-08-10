/**
 * AbstractSetCharacteristicEffect.java
 * 
 * Created on 03.09.2009
 */

package net.slightlymagic.laterna.magica.effect.characteristic.abs;


import static java.lang.String.*;
import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.characteristics.Characteristics;
import net.slightlymagic.laterna.magica.effect.characteristic.SetCharacteristicEffect;


/**
 * The class AbstractSetCharacteristicEffect.
 * 
 * @version V0.0 03.09.2009
 * @author Clemens Koza
 */
public abstract class AbstractSetCharacteristicEffect<T> extends AbstractCharacteristicEffect implements SetCharacteristicEffect<T> {
    private Mode mode;
    
    public AbstractSetCharacteristicEffect(Game game, Layer l, Characteristics c, Mode mode) {
        super(game, l, c);
        this.mode = mode;
    }
    
    public Mode getMode() {
        return mode;
    }
    
    @Override
    public String toString() {
        return format("SetCharacteristicEffect %s(%s, %s)", getCharacteristic(), getMode(), getSetValues());
    }
}
