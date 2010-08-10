/**
 * ActivatedAbility.java
 * 
 * Created on 15.04.2010
 */

package net.slightlymagic.laterna.magica.ability;


import net.slightlymagic.laterna.magica.action.play.ActivateAction;
import net.slightlymagic.laterna.magica.action.play.PlayInformation;


/**
 * The class ActivatedAbility.
 * 
 * @version V0.0 15.04.2010
 * @author Clemens Koza
 */
public interface ActivatedAbility extends NonStaticAbility {
    public boolean isLegal(ActivateAction a);
    
    public PlayInformation getPlayInformation(ActivateAction a);
}
