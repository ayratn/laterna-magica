/**
 * AbstractActor.java
 * 
 * Created on 09.04.2010
 */

package net.slightlymagic.laterna.magica.player.impl;


import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.player.Actor;
import net.slightlymagic.laterna.magica.player.Player;


/**
 * The class AbstractActor.
 * 
 * @version V0.0 09.04.2010
 * @author Clemens Koza
 */
public abstract class AbstractActor implements Actor {
    private Player player;
    
    public AbstractActor(Player player) {
        this.player = player;
    }
    
    public Game getGame() {
        return getPlayer().getGame();
    }
    
    public Player getPlayer() {
        return player;
    }
}
