/**
 * TurnBasedAction.java
 * 
 * Created on 02.04.2010
 */

package net.slightlymagic.laterna.magica.action.turnBased;


import net.slightlymagic.laterna.magica.action.GameAction;


/**
 * The class TurnBasedAction.
 * 
 * @version V0.0 02.04.2010
 * @author Clemens Koza
 */
public interface TurnBasedAction extends GameAction {
    /**
     * The turn based actions, as defined in {@magic.ruleRef 703 CR 703}.
     */
    public static enum Type {
        /**
         * {@magic.ruleRef 703.4a CR 703.4a}
         */
        PHASING,
        /**
         * {@magic.ruleRef 703.4b CR 703.4b}
         */
        UNTAP,
        /**
         * {@magic.ruleRef 703.4c CR 703.4c}
         */
        DRAW,
        /**
         * {@magic.ruleRef 703.4d CR 703.4d}
         */
        DEFENDER,
        /**
         * {@magic.ruleRef 703.4e CR 703.4e}
         */
        ATTACKERS,
        /**
         * {@magic.ruleRef 703.4f CR 703.4f}
         */
        BLOCKERS,
        /**
         * {@magic.ruleRef 703.4g CR 703.4g}
         */
        BLOCKER_ORDER,
        /**
         * {@magic.ruleRef 703.4h CR 703.4h}
         */
        ATTACKER_ORDER,
        /**
         * {@magic.ruleRef 703.4i CR 703.4i}
         */
        DAMAGE_ASSIGNMENT,
        /**
         * {@magic.ruleRef 703.4j CR 703.4j}
         */
        DAMAGE_DEALING,
        /**
         * {@magic.ruleRef 703.4k CR 703.4k}
         */
        HAND_LIMIT,
        /**
         * {@magic.ruleRef 703.4m CR 703.4m}
         */
        WEAR_OFF,
        /**
         * {@magic.ruleRef 703.4n CR 703.4n}
         */
        EMPTY_POOLS;
    }
}
