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
import net.slightlymagic.laterna.magica.characteristics.CardType;
import net.slightlymagic.laterna.magica.effect.characteristic.abs.AbstractTypeChangingEffect;



/**
 * The class PTChangingEffectImpl.
 * 
 * @version V0.0 09.09.2009
 * @author Clemens Koza
 */
public class TypeChangingEffectImpl extends AbstractTypeChangingEffect {
    private Set<CardType> values;
    
    public TypeChangingEffectImpl(Game game, Mode mode, CardType... values) {
        this(game, mode, false, values);
    }
    
    public TypeChangingEffectImpl(Game game, Mode mode, Set<CardType> values) {
        this(game, mode, false, values);
    }
    
    public TypeChangingEffectImpl(Game game, Mode mode, boolean isCharacteristicDefining, CardType... values) {
        this(game, mode, isCharacteristicDefining, new HashSet<CardType>(asList(values)));
    }
    
    public TypeChangingEffectImpl(Game game, Mode mode, boolean isCharacteristicDefining, Set<CardType> values) {
        super(game, mode, isCharacteristicDefining);
        this.values = unmodifiableSet(values);
    }
    
    public Set<CardType> getSetValues() {
        return values;
    }
}
