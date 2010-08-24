/**
 * AbstractGameContent.java
 * 
 * Created on 04.09.2009
 */

package net.slightlymagic.laterna.magica.impl;


import net.slightlymagic.beans.EventListenerList;
import net.slightlymagic.beans.PropertyChangeSupport;
import net.slightlymagic.beans.properties.Properties;
import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.GameContent;
import net.slightlymagic.laterna.magica.edit.property.EditableBoundBean;


/**
 * The class AbstractGameContent.
 * 
 * @version V0.0 04.09.2009
 * @author Clemens Koza
 */
public abstract class AbstractGameContent extends EditableBoundBean implements GameContent {
    private final Game game;
    
    /**
     * Constructor taking another AbstractGameContent's {@link PropertyChangeSupport}, {@link Properties} and
     * {@link EventListenerList}. This is meant for objects which are parts of another game content.
     */
    public AbstractGameContent(AbstractGameContent c) {
        if(c == null) throw new IllegalArgumentException("c == null");
        this.game = c.getGame();
        s = c.s;
        properties = c.properties;
        listeners = c.listeners;
    }
    
    public AbstractGameContent(Game game) {
        if(game == null) throw new IllegalArgumentException("game == null");
        this.game = game;
        init(game);
    }
    
    public Game getGame() {
        return this.game;
    }
}
