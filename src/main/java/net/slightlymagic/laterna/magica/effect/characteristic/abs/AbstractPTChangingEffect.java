/**
 * AbstractPTChangingEffect.java
 * 
 * Created on 03.09.2009
 */

package net.slightlymagic.laterna.magica.effect.characteristic.abs;


import static java.lang.String.*;
import static net.slightlymagic.laterna.magica.characteristics.Characteristics.*;
import static net.slightlymagic.laterna.magica.effect.ContinuousEffect.Layer.*;
import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.effect.characteristic.PTChangingEffect;


/**
 * The class AbstractPTChangingEffect.
 * 
 * @version V0.0 03.09.2009
 * @author Clemens Koza
 */
public abstract class AbstractPTChangingEffect extends AbstractCharacteristicEffect implements PTChangingEffect {
    public AbstractPTChangingEffect(Game game) {
        super(game, L7c, POWER_TOUGHNESS);
    }
    
    public int getPower() {
        return 0;
    }
    
    public int getToughness() {
        return 0;
    }
    
    @Override
    public String toString() {
        return format("PTChangingEffect (%s, %s)", getPower(), getToughness());
    }
}
