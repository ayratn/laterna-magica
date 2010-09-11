/**
 * GameLoop.java
 * 
 * Created on 09.04.2010
 */

package net.slightlymagic.laterna.magica.impl;


import static javax.swing.JOptionPane.*;

import java.util.List;

import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.action.play.PlayAction;
import net.slightlymagic.laterna.magica.player.ConcessionException;
import net.slightlymagic.laterna.magica.player.IrregularActionException;
import net.slightlymagic.laterna.magica.player.Player;
import net.slightlymagic.laterna.magica.player.Player.Status;
import net.slightlymagic.laterna.magica.turnStructure.PhaseStructure;


/**
 * The class GameLoop.
 * 
 * @version V0.0 09.04.2010
 * @author Clemens Koza
 */
public class GameLoop extends AbstractGameContent implements Runnable {
    private boolean run = true;
    
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
        loop: do {
            try {
                //TODO this is a little dirty. see 104.2: not all other players, but all opponents must have left the game.
                List<Player> p = getGame().getPlayersInGame();
                switch(p.size()) {
                    case 1:
                        p.get(0).winGame();
                        //fallthrough
                    case 0:
                    break loop;
                }
                
                try {
                    PlayAction a = ps.getPriorPlayer().getActor().getAction();
                    if(!run) break;
                    if(a == null) ps.takeAction(false);
                    else {
                        a.execute();
                        ps.takeAction(true);
                    }
                } catch(ConcessionException ex) {

                } catch(IrregularActionException ex) {
                    //there are no other IrregularActionExceptions
                    throw new AssertionError(ex);
                }
            } catch(Exception ex) {
                log.error(null, ex);
            }
        } while(run);
        if(run) {
            //the game ended regularly, praise the winner
            for(Player p:getGame().getPlayers()) {
                if(p.getPlayerStatus() == Status.WON) showMessageDialog(null, p + " has won the game!");
            }
        }
    }
}
