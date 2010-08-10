/**
 * LocalEffectsImpl.java
 * 
 * Created on 21.03.2010
 */

package net.slightlymagic.laterna.magica.effect.impl;


import static java.util.Collections.*;

import java.util.HashSet;
import java.util.Set;

import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.effect.LocalEffects;
import net.slightlymagic.laterna.magica.effect.characteristic.CharacteristicEffect;
import net.slightlymagic.laterna.magica.impl.AbstractGameContent;
import net.slightlymagic.laterna.magica.util.MagicaCollections;



/**
 * The class LocalEffectsImpl.
 * 
 * @version V0.0 21.03.2010
 * @author Clemens Koza
 */
public class LocalEffectsImpl extends AbstractGameContent implements LocalEffects {
    private Set<CharacteristicEffect> effects, effectsView;
    
    public LocalEffectsImpl(Game game) {
        super(game);
    }
    
    public Set<CharacteristicEffect> getEffects() {
        if(effects == null) {
            Set<CharacteristicEffect> effects = new HashSet<CharacteristicEffect>();
            this.effects = MagicaCollections.editableSet(getGame(), effects);
            effectsView = unmodifiableSet(effects);
        }
        return effectsView;
    }
    
    public void add(CharacteristicEffect effect) {
        //ensure the map exists
        getEffects().add(effect);
    }
    
    public void remove(CharacteristicEffect effect) {
        //ensure the map exists
        getEffects().remove(effect);
    }
}
