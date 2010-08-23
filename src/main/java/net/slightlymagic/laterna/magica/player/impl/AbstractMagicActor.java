/**
 * AbstractMagicActor.java
 * 
 * Created on 09.04.2010
 */

package net.slightlymagic.laterna.magica.player.impl;


import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.player.MagicActor;
import net.slightlymagic.laterna.magica.player.Player;


/**
 * The class AbstractMagicActor.
 * 
 * @version V0.0 09.04.2010
 * @author Clemens Koza
 */
public abstract class AbstractMagicActor implements MagicActor {
    private Player player;
    
    public AbstractMagicActor(Player player) {
        this.player = player;
    }
    
    public Game getGame() {
        return getPlayer().getGame();
    }
    
    public Player getPlayer() {
        return player;
    }
}
