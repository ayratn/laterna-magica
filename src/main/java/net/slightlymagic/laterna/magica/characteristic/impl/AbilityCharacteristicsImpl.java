/**
 * AbilityCharacteristicsImpl.java
 * 
 * Created on 22.04.2010
 */

package net.slightlymagic.laterna.magica.characteristic.impl;


import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.ability.NonStaticAbility;
import net.slightlymagic.laterna.magica.characteristic.AbilityCharacteristics;


/**
 * The class AbilityCharacteristicsImpl.
 * 
 * @version V0.0 22.04.2010
 * @author Clemens Koza
 */
public class AbilityCharacteristicsImpl extends ObjectCharacteristicsImpl implements AbilityCharacteristics {
    private NonStaticAbility ability;
    
    public AbilityCharacteristicsImpl(MagicObject card, NonStaticAbility ability) {
        super(card);
        this.ability = ability;
        //TODO implement ability text
    }
    
    public NonStaticAbility getAbility() {
        return ability;
    }
}
