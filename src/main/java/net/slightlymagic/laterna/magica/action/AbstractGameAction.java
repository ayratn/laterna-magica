/**
 * AbstractGameAction.java
 * 
 * Created on 01.04.2010
 */

package net.slightlymagic.laterna.magica.action;


import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.impl.AbstractGameContent;


/**
 * The class AbstractGameAction.
 * 
 * @version V0.0 01.04.2010
 * @author Clemens Koza
 */
public abstract class AbstractGameAction extends AbstractGameContent implements GameAction {
    public AbstractGameAction(Game game) {
        super(game);
    }
    
    public abstract boolean execute();
}
