/**
 * ColorChangingEffectImpl.java
 * 
 * Created on 09.09.2009
 */

package net.slightlymagic.laterna.magica.effect.characteristic.impl;


import static java.util.Arrays.*;
import static java.util.Collections.*;

import java.util.HashSet;
import java.util.Set;

import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.characteristics.MagicColor;
import net.slightlymagic.laterna.magica.effect.characteristic.abs.AbstractColorChangingEffect;



/**
 * The class ColorChangingEffectImpl.
 * 
 * @version V0.0 09.09.2009
 * @author Clemens Koza
 */
public class ColorChangingEffectImpl extends AbstractColorChangingEffect {
    private Set<MagicColor> values;
    
    public ColorChangingEffectImpl(Game game, Mode mode, MagicColor... values) {
        this(game, mode, false, values);
    }
    
    public ColorChangingEffectImpl(Game game, Mode mode, Set<MagicColor> values) {
        this(game, mode, false, values);
    }
    
    public ColorChangingEffectImpl(Game game, Mode mode, boolean isCharacteristicDefining, MagicColor... values) {
        this(game, mode, isCharacteristicDefining, new HashSet<MagicColor>(asList(values)));
    }
    
    public ColorChangingEffectImpl(Game game, Mode mode, boolean isCharacteristicDefining, Set<MagicColor> values) {
        super(game, mode, isCharacteristicDefining);
        this.values = unmodifiableSet(values);
    }
    
    public Set<MagicColor> getSetValues() {
        return values;
    }
}
