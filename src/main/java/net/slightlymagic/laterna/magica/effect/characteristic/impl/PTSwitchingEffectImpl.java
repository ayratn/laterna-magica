/**
 * PTSwitchingEffectImpl.java
 * 
 * Created on 03.09.2009
 */

package net.slightlymagic.laterna.magica.effect.characteristic.impl;


import static net.slightlymagic.laterna.magica.characteristics.Characteristics.*;
import static net.slightlymagic.laterna.magica.effect.ContinuousEffect.Layer.*;
import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.effect.characteristic.PTSwitchingEffect;
import net.slightlymagic.laterna.magica.effect.characteristic.abs.AbstractCharacteristicEffect;


/**
 * The class PTSwitchingEffectImpl.
 * 
 * @version V0.0 03.09.2009
 * @author Clemens Koza
 */
public class PTSwitchingEffectImpl extends AbstractCharacteristicEffect implements PTSwitchingEffect {
    public PTSwitchingEffectImpl(Game game) {
        super(game, L7e, POWER_TOUGHNESS);
    }
    
    @Override
    public Layer getLayer() {
        return L7e;
    }
    
    @Override
    public String toString() {
        return "P/T Switching Effect";
    }
}
