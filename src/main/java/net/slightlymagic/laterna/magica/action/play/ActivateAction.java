/**
 * ActivateAction.java
 * 
 * Created on 16.04.2010
 */

package net.slightlymagic.laterna.magica.action.play;


import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.ability.AbilityObject;
import net.slightlymagic.laterna.magica.ability.ActivatedAbility;
import net.slightlymagic.laterna.magica.ability.impl.AbilityObjectImpl;
import net.slightlymagic.laterna.magica.player.Player;


/**
 * The class ActivateAction.
 * 
 * @version V0.0 16.04.2010
 * @author Clemens Koza
 */
public class ActivateAction extends PlayAction {
    /**
     * @param controller The player that activated the ability
     * @param ob The card the ability is on
     * @param ab The ability that was activated
     */
    public ActivateAction(Player controller, MagicObject ob, ActivatedAbility ab) {
        super(controller, new AbilityObjectImpl(controller.getGame(), ob, ab));
    }
    
    @Override
    public AbilityObject getObject() {
        return (AbilityObject) super.getObject();
    }
    
    @Override
    public String toString() {
        return "Activate \"" + getObject() + "\"";
    }
}
