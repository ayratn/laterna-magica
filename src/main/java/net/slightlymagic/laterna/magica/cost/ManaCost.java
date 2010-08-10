/**
 * ManaCost.java
 * 
 * Created on 16.04.2010
 */

package net.slightlymagic.laterna.magica.cost;


import net.slightlymagic.laterna.magica.action.GameAction;
import net.slightlymagic.laterna.magica.mana.ManaSequence;


/**
 * The class ManaCost. A mana cost is a cost that requires mana payment. Mana costs may be modified by effects.
 * 
 * @version V0.0 16.04.2010
 * @author Clemens Koza
 */
public interface ManaCost extends GameAction {
    /**
     * Returns the mana sequence needed to pay this cost.
     */
    public ManaSequence getCost();
}
