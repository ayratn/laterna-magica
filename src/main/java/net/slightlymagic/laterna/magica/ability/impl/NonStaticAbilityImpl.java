/**
 * NonStaticAbilityImpl.java
 * 
 * Created on 21.04.2010
 */

package net.slightlymagic.laterna.magica.ability.impl;


import net.slightlymagic.laterna.magica.ability.NonStaticAbility;


/**
 * The class NonStaticAbilityImpl.
 * 
 * @version V0.0 21.04.2010
 * @author Clemens Koza
 */
public abstract class NonStaticAbilityImpl implements NonStaticAbility {
    private static final long serialVersionUID = -6073244989365610540L;
    
    private boolean           manaAbility;
    private String            text;
    
    public NonStaticAbilityImpl(boolean manaAbility, String text) {
        this.manaAbility = manaAbility;
        this.text = text;
    }
    
    public boolean isManaAbility() {
        return manaAbility;
    }
    
    @Override
    public String toString() {
        return text;
    }
}
