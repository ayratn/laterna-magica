/**
 * PlayerPanel.java
 * 
 * Created on 27.08.2010
 */

package net.slightlymagic.laterna.magica.gui.player;


import javax.swing.JPanel;

import net.slightlymagic.laterna.magica.gui.Gui;
import net.slightlymagic.laterna.magica.player.Player;

import org.jetlang.core.Disposable;


/**
 * The class PlayerPanel.
 * 
 * @version V0.0 27.08.2010
 * @author Clemens Koza
 */
public abstract class PlayerPanel extends JPanel implements Disposable {
    private static final long serialVersionUID = 4749888553786730209L;
    
    private final Gui         gui;
    private final Player      player;
    
    public PlayerPanel(Gui gui, Player player) {
        this.gui = gui;
        this.player = player;
    }
    
    public Gui getGui() {
        return gui;
    }
    
    public Player getPlayer() {
        return player;
    }
}
