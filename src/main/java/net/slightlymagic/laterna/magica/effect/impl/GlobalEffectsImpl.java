/**
 * GlobalEffectsImpl.java
 * 
 * Created on 10.10.2009
 */

package net.slightlymagic.laterna.magica.effect.impl;


import static java.util.Collections.*;

import java.util.HashMap;
import java.util.Map;

import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.effect.GlobalEffects;
import net.slightlymagic.laterna.magica.effect.characteristic.CharacteristicEffect;
import net.slightlymagic.laterna.magica.impl.AbstractGameContent;
import net.slightlymagic.laterna.magica.util.MagicaCollections;


import com.google.common.base.Predicate;


/**
 * The class GlobalEffectsImpl.
 * 
 * @version V0.0 10.10.2009
 * @author Clemens Koza
 */
public class GlobalEffectsImpl extends AbstractGameContent implements GlobalEffects {
    private Map<CharacteristicEffect, Predicate<? super MagicObject>> effects, effectsView;
    
    public GlobalEffectsImpl(Game game) {
        super(game);
    }
    
    public Map<CharacteristicEffect, Predicate<? super MagicObject>> getEffects() {
        if(effects == null) {
            HashMap<CharacteristicEffect, Predicate<? super MagicObject>> effects = new HashMap<CharacteristicEffect, Predicate<? super MagicObject>>();
            this.effects = MagicaCollections.editableMap(getGame(), effects);
            effectsView = unmodifiableMap(effects);
        }
        return effectsView;
    }
    
    public void put(CharacteristicEffect effect, Predicate<? super MagicObject> matcher) {
        getEffects().put(effect, matcher);
    }
    
    public void remove(CharacteristicEffect effect) {
        getEffects().remove(effect);
    }
}
