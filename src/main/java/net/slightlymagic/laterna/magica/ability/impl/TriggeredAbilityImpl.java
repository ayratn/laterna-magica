/**
 * TriggeredAbilityImpl.java
 * 
 * Created on 09.09.2010
 */

package net.slightlymagic.laterna.magica.ability.impl;


import net.slightlymagic.laterna.magica.ability.TriggeredAbility;
import net.slightlymagic.laterna.magica.action.play.PlayInformation;
import net.slightlymagic.laterna.magica.action.play.TriggerAction;

import com.google.common.base.Function;
import com.google.common.base.Predicate;


/**
 * The class TriggeredAbilityImpl.
 * 
 * @version V0.0 09.09.2010
 * @author Clemens Koza
 */
public class TriggeredAbilityImpl extends NonStaticAbilityImpl implements TriggeredAbility {
    private static final long                                          serialVersionUID = -8987886570576715112L;
    
    private Predicate<? super TriggerAction>                           trigger;
    private Function<? super TriggerAction, ? extends PlayInformation> object;
    
    public TriggeredAbilityImpl(boolean manaAbility, Predicate<? super TriggerAction> trigger, Function<? super TriggerAction, ? extends PlayInformation> object, String text) {
        super(manaAbility, text);
        this.trigger = trigger;
        this.object = object;
    }
    
    @Override
    public boolean triggersFrom(TriggerAction trigger) {
        return this.trigger.apply(trigger);
    }
    
    public PlayInformation getPlayInformation(TriggerAction a) {
        return object.apply(a);
    }
}
