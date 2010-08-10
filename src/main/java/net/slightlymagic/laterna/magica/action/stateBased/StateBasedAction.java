/**
 * StateBasedAction.java
 * 
 * Created on 02.04.2010
 */

package net.slightlymagic.laterna.magica.action.stateBased;


import net.slightlymagic.laterna.magica.action.GameAction;


/**
 * The class StateBasedAction.
 * 
 * @version V0.0 02.04.2010
 * @author Clemens Koza
 */
public interface StateBasedAction extends GameAction {
    public static enum Type {
        LOSE_ON_LIFE,
        LOSE_ON_DRAW,
        LOSE_ON_POISON,
        PHASED_TOKEN_CEASE,
        COPY_CEASE,
        CREATURE_TOUGHNESS,
        LETHAL_DAMAGE,
        DEATHTOUCH_DAMAGE,
        PLANESWALKER_LOYALTY,
        PLANESWALKER_UNIQUENESS,
        LEGENDARY_UNIQUENESS,
        WORLD_UNIQUENESS,
        AURA_ILLEGAL,
        EQUIPMENT_ILLEGAL,
        UNATTACH,
        P1P1_M1M1_REMOVE,
        COUNTER_LIMIT;
    }
    
    /**
     * {@inheritDoc}
     * 
     * 
     * For a state-based action, the return value means if the action had an effect. For example, if a creature was
     * destroyed by lethal damage or not.
     * 
     * @see net.slightlymagic.laterna.magica.action.GameAction#execute()
     */
    public boolean execute();
}
