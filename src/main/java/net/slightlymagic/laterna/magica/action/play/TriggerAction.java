/**
 * TriggerAction.java
 * 
 * Created on 08.09.2010
 */

package net.slightlymagic.laterna.magica.action.play;


import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.ability.AbilityObject;
import net.slightlymagic.laterna.magica.ability.TriggeredAbility;
import net.slightlymagic.laterna.magica.ability.impl.AbilityObjectImpl;
import net.slightlymagic.laterna.magica.effect.replacement.ReplaceableEvent;
import net.slightlymagic.laterna.magica.player.Player;


/**
 * The class TriggerAction.
 * 
 * @version V0.0 08.09.2010
 * @author Clemens Koza
 */
public class TriggerAction extends PlayAction {
    private ReplaceableEvent trigger;
    
    /**
     * @param controller The player controlling the ability
     * @param ob The card the ability is on
     * @param ab The ability that triggered
     * @param trigger the triggering event
     */
    public TriggerAction(Player controller, MagicObject ob, TriggeredAbility ab, ReplaceableEvent trigger) {
        super(controller, new AbilityObjectImpl(controller.getGame(), ob, ab));
        this.trigger = trigger;
    }
    
    @Override
    public AbilityObject getObject() {
        return (AbilityObject) super.getObject();
    }
    
    public ReplaceableEvent getTrigger() {
        return trigger;
    }
    
    @Override
    public String toString() {
        return "Put \"" + getObject() + "\" onto the stack";
    }
}
