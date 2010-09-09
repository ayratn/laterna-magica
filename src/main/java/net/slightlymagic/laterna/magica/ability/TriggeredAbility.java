/**
 * TriggeredAbility.java
 * 
 * Created on 15.04.2010
 */

package net.slightlymagic.laterna.magica.ability;


import net.slightlymagic.laterna.magica.action.play.PlayInformation;
import net.slightlymagic.laterna.magica.action.play.TriggerAction;


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
    public boolean triggersFrom(TriggerAction trigger);
    
    public PlayInformation getPlayInformation(TriggerAction a);
}
