/**
 * TriggeredAbility.java
 * 
 * Created on 15.04.2010
 */

package net.slightlymagic.laterna.magica.ability;


import net.slightlymagic.laterna.magica.action.play.PlayInformation;
import net.slightlymagic.laterna.magica.action.play.TriggerAction;
import net.slightlymagic.laterna.magica.effect.replacement.ReplaceableEvent;


/**
 * The class TriggeredAbility.
 * 
 * @version V0.0 15.04.2010
 * @author Clemens Koza
 */
public interface TriggeredAbility extends NonStaticAbility {
    /**
     * Returns whether the triggered ability triggers from the given event
     */
    public boolean triggersFrom(ReplaceableEvent trigger);
    
    public PlayInformation getPlayInformation(TriggerAction a);
}
