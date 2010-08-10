/**
 * SpecialAction.java
 * 
 * Created on 18.04.2010
 */

package net.slightlymagic.laterna.magica.action.play;


import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.player.Player;


/**
 * The class SpecialAction.
 * 
 * @version V0.0 18.04.2010
 * @author Clemens Koza
 */
public abstract class SpecialAction extends PlayAction {
    public SpecialAction(Player controller) {
        super(controller);
    }
    
    public SpecialAction(Player controller, MagicObject ob) {
        super(controller, ob);
    }
    
    @Override
    public boolean isLegal() {
        return isLegalTiming() && isLegalState();
    }
    
    public abstract boolean isLegalTiming();
    
    /**
     * Returns if the game state allows this action
     */
    protected boolean isLegalState() {
        return true;
    }
}
