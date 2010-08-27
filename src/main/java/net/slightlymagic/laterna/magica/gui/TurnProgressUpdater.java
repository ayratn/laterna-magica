/**
 * TurnProgressUpdater.java
 * 
 * Created on 26.08.2010
 */

package net.slightlymagic.laterna.magica.gui;


import static java.lang.String.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JLabel;

import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.action.turnBased.TurnBasedAction;
import net.slightlymagic.laterna.magica.player.Player;
import net.slightlymagic.laterna.magica.turnStructure.PhaseStructure;
import net.slightlymagic.laterna.magica.turnStructure.TurnStructure;


/**
 * The class TurnProgressUpdater.
 * 
 * @version V0.0 26.08.2010
 * @author Clemens Koza
 */
public class TurnProgressUpdater implements PropertyChangeListener {
    private JLabel label;
    private Game   game;
    
    public TurnProgressUpdater(JLabel label, Game game) {
        this.label = label;
        this.game = game;
        
        TurnStructure ts = game.getTurnStructure();
        PhaseStructure ps = game.getPhaseStructure();
        ts.addPropertyChangeListener("activePlayer", this);
        ps.addPropertyChangeListener("prior", this);
        ps.addPropertyChangeListener("phase", this);
        ps.addPropertyChangeListener("step", this);
        ps.addPropertyChangeListener("turnBasedAction", this);
        
        update();
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        update();
    }
    
    private void update() {
        TurnStructure ts = game.getTurnStructure();
        PhaseStructure ps = game.getPhaseStructure();
        
        TurnBasedAction.Type t = ps.getTurnBasedAction();
        if(t == null) {
            Player prior = ps.getPriorPlayer();
            label.setText(format("%s's %s - %s has priority", ts.getActivePlayer(), ps.getStep(), prior));
        } else {
            label.setText(format("%s's %s - %s", ts.getActivePlayer(), ps.getStep(), t));
        }
    }
}
