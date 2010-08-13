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
     * The turn based actions, as defined in {@magic.ruleRef 20100716/R7034}.
     */
    public static enum Type {
        /**
         * {@magic.ruleRef 20100716/R7034a}
         */
        PHASING,
        /**
         * {@magic.ruleRef 20100716/R7034b}
         */
        UNTAP,
        /**
         * {@magic.ruleRef 20100716/R7034c}
         */
        DRAW,
        /**
         * {@magic.ruleRef 20100716/R7034d}
         */
        SCHEME,
        /**
         * {@magic.ruleRef 20100716/R7034e}
         */
        DEFENDER,
        /**
         * {@magic.ruleRef 20100716/R7034f}
         */
        ATTACKERS,
        /**
         * {@magic.ruleRef 20100716/R7034g}
         */
        BLOCKERS,
        /**
         * {@magic.ruleRef 20100716/R7034h}
         */
        BLOCKER_ORDER,
        /**
         * {@magic.ruleRef 20100716/R7034i}
         */
        ATTACKER_ORDER,
        /**
         * {@magic.ruleRef 20100716/R7034j}
         */
        DAMAGE_ASSIGNMENT,
        /**
         * {@magic.ruleRef 20100716/R7034k}
         */
        DAMAGE_DEALING,
        /**
         * {@magic.ruleRef 20100716/R7034m}
         */
        HAND_LIMIT,
        /**
         * {@magic.ruleRef 20100716/R7034n}
         */
        WEAR_OFF,
        /**
         * {@magic.ruleRef 20100716/R7034p}
         */
        EMPTY_POOLS;
    }
}
