/**
 * DummyCost.java
 * 
 * Created on 19.04.2010
 */

package net.slightlymagic.laterna.magica.cost.impl;


import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.action.AbstractGameAction;


/**
 * The class DummyCost. A cost that is either unpayable (represented by "{}" in mana costs), or requires no payment
 * (represented by "{0}" in mana costs).
 * 
 * @version V0.0 19.04.2010
 * @author Clemens Koza
 */
public class DummyCost extends AbstractGameAction {
    private boolean result;
    
    public DummyCost(Game game, boolean payable) {
        super(game);
        result = payable;
    }
    
    @Override
    public boolean execute() {
        return result;
    }
}
