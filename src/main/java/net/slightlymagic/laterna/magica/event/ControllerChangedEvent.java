/**
 * ControllerChangedEvent.java
 * 
 * Created on 20.10.2009
 */

package net.slightlymagic.laterna.magica.event;


import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.player.Player;


/**
 * The class ControllerChangedEvent.
 * 
 * @version V0.0 20.10.2009
 * @author Clemens Koza
 */
public class ControllerChangedEvent {
    private MagicObject card;
    private Player      old, now;
    
    public ControllerChangedEvent(MagicObject card, Player old, Player now) {
        this.card = card;
        this.old = old;
        this.now = now;
    }
    
    public MagicObject getCard() {
        return card;
    }
    
    public Player getOldController() {
        return old;
    }
    
    public Player getNewController() {
        return now;
    }
}
