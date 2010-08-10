/**
 * AbstractAbilityChangingEffect.java
 * 
 * Created on 04.09.2009
 */

package net.slightlymagic.laterna.magica.effect.characteristic.abs;


import static net.slightlymagic.laterna.magica.characteristics.Characteristics.*;
import static net.slightlymagic.laterna.magica.effect.ContinuousEffect.Layer.*;
import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.ability.Ability;


/**
 * The class AbstractAbilityChangingEffect.
 * 
 * @version V0.0 04.09.2009
 * @author Clemens Koza
 */
public abstract class AbstractAbilityChangingEffect extends AbstractSetCharacteristicEffect<Ability> {
    public AbstractAbilityChangingEffect(Game game, Mode mode) {
        this(game, mode, false);
    }
    
    public AbstractAbilityChangingEffect(Game game, Mode mode, boolean isCharacteristicDefining) {
        super(game, isCharacteristicDefining? L_6:L6, COLOR, mode);
    }
}
