/**
 * GameLoop.java
 * 
 * Created on 09.04.2010
 */

package net.slightlymagic.laterna.magica.impl;



import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.action.play.PlayAction;
import net.slightlymagic.laterna.magica.turnStructure.PhaseStructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The class GameLoop.
 * 
 * @version V0.0 09.04.2010
 * @author Clemens Koza
 */
public class GameLoop extends AbstractGameContent implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(GameLoop.class);
    
    private boolean             run = true;
    
    public GameLoop(Game game) {
        super(game);
    }
    
    /**
     * Terminates the game loop. Note that this will not cause a blocking thread to immediately return from
     * {@link #run()}. The aborted state is checked immediately before an action would be taken. The game is not
     * modified or terminated by calling this.
     */
    public void abortGame() {
        run = false;
    }
    
    /**
     * Runs the game, asking the prior player for an action and executing it. The game loop ends when the game
     * ends, or the loop is terminated by {@link #abortGame()}.
     */
    public void run() {
        PhaseStructure ps = getGame().getPhaseStructure();
        do {
            try {
                PlayAction a = ps.getPriorPlayer().getActor().getAction();
                if(!run) break;
                if(a == null) ps.takeAction(false);
                else {
                    a.execute();
                    ps.takeAction(true);
                }
                //TODO break when the game has ended.
            } catch(Exception ex) {
                log.error(null, ex);
            }
        } while(run);
    }
}
