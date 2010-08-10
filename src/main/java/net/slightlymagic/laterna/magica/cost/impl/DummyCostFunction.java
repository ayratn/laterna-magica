/**
 * DummyCostFunction.java
 * 
 * Created on 19.04.2010
 */

package net.slightlymagic.laterna.magica.cost.impl;


import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.action.GameAction;

import com.google.common.base.Function;


/**
 * The class DummyCostFunction.
 * 
 * @version V0.0 19.04.2010
 * @author Clemens Koza
 */
public enum DummyCostFunction implements Function<Game, GameAction> {
    EMPTY(true), UNPAYABLE(false);
    
    private boolean payable;
    
    private DummyCostFunction(boolean payable) {
        this.payable = payable;
    }
    
    public GameAction apply(Game from) {
        return new DummyCost(from, payable);
    }
}
