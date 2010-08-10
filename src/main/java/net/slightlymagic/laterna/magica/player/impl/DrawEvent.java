/**
 * DrawEvent.java
 * 
 * Created on 02.04.2010
 */

package net.slightlymagic.laterna.magica.player.impl;


import net.slightlymagic.laterna.magica.effect.replacement.ReplaceableEvent;
import net.slightlymagic.laterna.magica.player.Player;


/**
 * The class DrawEvent.
 * 
 * @version V0.0 02.04.2010
 * @author Clemens Koza
 */
public class DrawEvent extends ReplaceableEvent {
    public DrawEvent(Player player) {
        super(player);
    }
    
    @Override
    protected boolean execute0() {
        PlayerImpl p = (PlayerImpl) getAffectedPlayer();
        return p.fireDrawEvent(this);
    }
}
