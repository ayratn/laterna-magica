/**
 * AbstractCharacteristicEffect.java
 * 
 * Created on 03.09.2009
 */

package net.slightlymagic.laterna.magica.effect.characteristic.abs;


import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.characteristics.Characteristics;
import net.slightlymagic.laterna.magica.effect.characteristic.CharacteristicEffect;
import net.slightlymagic.laterna.magica.effect.impl.AbstractContinuousEffect;


/**
 * The class AbstractCharacteristicEffect.
 * 
 * @version V0.0 03.09.2009
 * @author Clemens Koza
 */
public abstract class AbstractCharacteristicEffect extends AbstractContinuousEffect implements CharacteristicEffect {
    private Characteristics c;
    
    public AbstractCharacteristicEffect(Game game, Layer l, Characteristics c) {
        super(game, l);
        this.c = c;
    }
    
    public Characteristics getCharacteristic() {
        return c;
    }
}
