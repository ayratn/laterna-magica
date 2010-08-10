/**
 * PTChangingEffectImpl.java
 * 
 * Created on 09.09.2009
 */

package net.slightlymagic.laterna.magica.effect.characteristic.impl;


import static java.util.Arrays.*;
import static java.util.Collections.*;

import java.util.HashSet;
import java.util.Set;

import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.characteristics.SubType;
import net.slightlymagic.laterna.magica.effect.characteristic.abs.AbstractSubTypeChangingEffect;



/**
 * The class SubTypeChangingEffectImpl.
 * 
 * @version V0.0 09.09.2009
 * @author Clemens Koza
 */
public class SubTypeChangingEffectImpl extends AbstractSubTypeChangingEffect {
    private Set<SubType> values;
    
    public SubTypeChangingEffectImpl(Game game, Mode mode, SubType... values) {
        this(game, mode, false, values);
    }
    
    public SubTypeChangingEffectImpl(Game game, Mode mode, Set<SubType> values) {
        this(game, mode, false, values);
    }
    
    public SubTypeChangingEffectImpl(Game game, Mode mode, boolean isCharacteristicDefining, SubType... values) {
        this(game, mode, isCharacteristicDefining, new HashSet<SubType>(asList(values)));
    }
    
    public SubTypeChangingEffectImpl(Game game, Mode mode, boolean isCharacteristicDefining, Set<SubType> values) {
        super(game, mode, isCharacteristicDefining);
        this.values = unmodifiableSet(values);
    }
    
    public Set<SubType> getSetValues() {
        return values;
    }
}
