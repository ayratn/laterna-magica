/**
 * ActivatedAbilityImpl.java
 * 
 * Created on 16.04.2010
 */

package net.slightlymagic.laterna.magica.ability.impl;


import net.slightlymagic.laterna.magica.ability.ActivatedAbility;
import net.slightlymagic.laterna.magica.action.play.ActivateAction;
import net.slightlymagic.laterna.magica.action.play.PlayInformation;

import com.google.common.base.Function;
import com.google.common.base.Predicate;


/**
 * The class ActivatedAbilityImpl.
 * 
 * @version V0.0 16.04.2010
 * @author Clemens Koza
 */
public class ActivatedAbilityImpl extends NonStaticAbilityImpl implements ActivatedAbility {
    private static final long                                           serialVersionUID = -8987886570576715112L;
    
    private Function<? super ActivateAction, ? extends PlayInformation> object;
    private Predicate<? super ActivateAction>                           legal;
    
    public ActivatedAbilityImpl(boolean manaAbility, Function<? super ActivateAction, ? extends PlayInformation> object, Predicate<? super ActivateAction> legal, String text) {
        super(manaAbility, text);
        this.object = object;
        this.legal = legal;
    }
    
    /**
     * Returns if activating the ability using the specified ActivateAction is legal.
     */
    public boolean isLegal(ActivateAction a) {
        return legal.apply(a);
    }
    
    
    public PlayInformation getPlayInformation(ActivateAction a) {
        return object.apply(a);
    }
}
