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
import net.slightlymagic.laterna.magica.characteristics.SuperType;
import net.slightlymagic.laterna.magica.effect.characteristic.abs.AbstractSuperTypeChangingEffect;



/**
 * The class SuperTypeChangingEffectImpl.
 * 
 * @version V0.0 09.09.2009
 * @author Clemens Koza
 */
public class SuperTypeChangingEffectImpl extends AbstractSuperTypeChangingEffect {
    private Set<SuperType> values;
    
    public SuperTypeChangingEffectImpl(Game game, Mode mode, SuperType... values) {
        this(game, mode, false, values);
    }
    
    public SuperTypeChangingEffectImpl(Game game, Mode mode, Set<SuperType> values) {
        this(game, mode, false, values);
    }
    
    public SuperTypeChangingEffectImpl(Game game, Mode mode, boolean isCharacteristicDefining, SuperType... values) {
        this(game, mode, isCharacteristicDefining, new HashSet<SuperType>(asList(values)));
    }
    
    public SuperTypeChangingEffectImpl(Game game, Mode mode, boolean isCharacteristicDefining, Set<SuperType> values) {
        super(game, mode, isCharacteristicDefining);
        this.values = unmodifiableSet(values);
    }
    
    public Set<SuperType> getSetValues() {
        return values;
    }
}
