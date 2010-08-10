/**
 * AbstractPTSettingEffect.java
 * 
 * Created on 03.09.2009
 */

package net.slightlymagic.laterna.magica.effect.characteristic.abs;


import static java.lang.String.*;
import static net.slightlymagic.laterna.magica.characteristics.Characteristics.*;
import static net.slightlymagic.laterna.magica.effect.ContinuousEffect.Layer.*;
import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.effect.characteristic.PTSettingEffect;


/**
 * The class AbstractPTSettingEffect.
 * 
 * @version V0.0 03.09.2009
 * @author Clemens Koza
 */
public abstract class AbstractPTSettingEffect extends AbstractCharacteristicEffect implements PTSettingEffect {
    public AbstractPTSettingEffect(Game game) {
        this(game, false);
    }
    
    public AbstractPTSettingEffect(Game game, boolean isCharacteristicDefining) {
        super(game, isCharacteristicDefining? L7a:L7b, POWER_TOUGHNESS);
    }
    
    public boolean affectsPower() {
        return false;
    }
    
    public boolean affectsToughness() {
        return false;
    }
    
    public int getPower() {
        throw new IllegalStateException();
    }
    
    public int getToughness() {
        throw new IllegalStateException();
    }
    
    @Override
    public String toString() {
        return format("PTSettingEffect (%s, %s)", affectsPower()? getPower():"",
                affectsToughness()? getToughness():"");
    }
}
