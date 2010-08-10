/**
 * AbstractGameContent.java
 * 
 * Created on 04.09.2009
 */

package net.slightlymagic.laterna.magica.impl;


import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.GameContent;


/**
 * The class AbstractGameContent.
 * 
 * @version V0.0 04.09.2009
 * @author Clemens Koza
 */
public abstract class AbstractGameContent implements GameContent {
    private final Game game;
    
    public AbstractGameContent(Game game) {
        if(game == null) throw new IllegalArgumentException("game == null");
        this.game = game;
    }
    
    public Game getGame() {
        return this.game;
    }
}
