/**
 * LoseOnDrawAction.java
 * 
 * Created on 02.04.2010
 */

package net.slightlymagic.laterna.magica.action.stateBased;


import java.util.HashSet;
import java.util.Set;

import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.action.AbstractGameAction;
import net.slightlymagic.laterna.magica.event.DrawListener;
import net.slightlymagic.laterna.magica.event.GameStartListener;
import net.slightlymagic.laterna.magica.player.Player;
import net.slightlymagic.laterna.magica.player.impl.DrawEvent;
import net.slightlymagic.laterna.magica.util.MagicaCollections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The class LoseOnDrawAction. This class implements {@magic.ruleRef 20100716/R7045b}: If a player
 * attempted to draw a card from an empty library since the last time state-based actions were checked, he or she
 * loses the game.
 * 
 * @version V0.0 02.04.2010
 * @author Clemens Koza
 */
public class LoseOnDrawAction extends AbstractGameAction implements StateBasedAction {
    private static final Logger log = LoggerFactory.getLogger(LoseOnDrawAction.class);
    
    //Set of players that tried to draw a card but couldn't
    private Set<Player>         players;
    
    public LoseOnDrawAction(Game game) {
        super(game);
        game.addGameStartListener(new GameStart());
        players = MagicaCollections.editableSet(getGame(), new HashSet<Player>());
    }
    
    
    @Override
    public boolean execute() {
        if(!players.isEmpty()) {
            for(Player p:players)
                p.loseGame();
            players.clear();
            return true;
        }
        return false;
    }
    
    private class GameStart implements GameStartListener.Internal {
        
        public void gameStarted(Game game) {
            DrawListener l = new Draw();
            for(Player p:getGame().getPlayers())
                p.addDrawListener(l);
        }
    }
    
    private class Draw implements DrawListener.Internal {
        
        public void cardDrawn(DrawEvent ev) {}
        
        
        public void cardNotDrawn(DrawEvent ev) {
            players.add(ev.getAffectedPlayer());
        }
    }
}
