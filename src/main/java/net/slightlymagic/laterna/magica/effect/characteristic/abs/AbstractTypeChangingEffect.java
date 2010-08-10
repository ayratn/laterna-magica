/**
 * AbstractTypeChangingEffect.java
 * 
 * Created on 04.09.2009
 */

package net.slightlymagic.laterna.magica.effect.characteristic.abs;


import static net.slightlymagic.laterna.magica.characteristics.Characteristics.*;
import static net.slightlymagic.laterna.magica.effect.ContinuousEffect.Layer.*;
import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.characteristics.CardType;


/**
 * The class AbstractTypeChangingEffect.
 * 
 * @version V0.0 04.09.2009
 * @author Clemens Koza
 */
public abstract class AbstractTypeChangingEffect extends AbstractSetCharacteristicEffect<CardType> {
    public AbstractTypeChangingEffect(Game game, Mode mode) {
        this(game, mode, false);
    }
    
    public AbstractTypeChangingEffect(Game game, Mode mode, boolean isCharacteristicDefining) {
        super(game, isCharacteristicDefining? L_4:L4, CARD_TYPE, mode);
    }
}
