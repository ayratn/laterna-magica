/**
 * AbilityChangingEffectImpl.java
 * 
 * Created on 09.09.2009
 */

package net.slightlymagic.laterna.magica.effect.characteristic.impl;


import static java.util.Arrays.*;
import static java.util.Collections.*;

import java.util.HashSet;
import java.util.Set;

import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.ability.Ability;
import net.slightlymagic.laterna.magica.effect.characteristic.abs.AbstractAbilityChangingEffect;


/**
 * The class AbilityChangingEffectImpl.
 * 
 * @version V0.0 09.09.2009
 * @author Clemens Koza
 */
public class AbilityChangingEffectImpl extends AbstractAbilityChangingEffect {
    private Set<Ability> values;
    
    public AbilityChangingEffectImpl(Game game, Mode mode, Ability... values) {
        this(game, mode, false, values);
    }
    
    public AbilityChangingEffectImpl(Game game, Mode mode, Set<Ability> values) {
        this(game, mode, false, values);
    }
    
    public AbilityChangingEffectImpl(Game game, Mode mode, boolean isCharacteristicDefining, Ability... values) {
        this(game, mode, isCharacteristicDefining, new HashSet<Ability>(asList(values)));
    }
    
    public AbilityChangingEffectImpl(Game game, Mode mode, boolean isCharacteristicDefining, Set<Ability> values) {
        super(game, mode, isCharacteristicDefining);
        this.values = unmodifiableSet(values);
    }
    
    public Set<Ability> getSetValues() {
        return values;
    }
}
