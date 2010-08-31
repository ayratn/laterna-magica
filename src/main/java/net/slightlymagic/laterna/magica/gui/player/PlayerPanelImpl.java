/**
 * PlayerPanelImpl.java
 * 
 * Created on 08.04.2010
 */

package net.slightlymagic.laterna.magica.gui.player;


import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.slightlymagic.laterna.magica.gui.DisposeSupport;
import net.slightlymagic.laterna.magica.gui.Gui;
import net.slightlymagic.laterna.magica.gui.mana.ManaPoolPanel;
import net.slightlymagic.laterna.magica.player.Player;
import net.slightlymagic.laterna.magica.zone.Zone.Zones;


/**
 * The class PlayerPanelImpl.
 * 
 * @version V0.0 08.04.2010
 * @author Clemens Koza
 */
public class PlayerPanelImpl extends PlayerPanel {
    private static final long      serialVersionUID = -5284446908967592513L;
    
    protected final DisposeSupport d                = new DisposeSupport();
    
    public PlayerPanelImpl(Gui gui, Player player) {
        super(gui, player);
        setupComponents();
    }
    
    protected void setupComponents() {
        setBorder(BorderFactory.createTitledBorder(getPlayer().toString()));
        setLayout(new BorderLayout(4, 4));
        JPanel labels = new JPanel(new GridLayout(0, 1));
        JPanel zones = new JPanel(new GridLayout(0, 1));
        Zones[] z = {Zones.LIBRARY, Zones.GRAVEYARD};
        for(Zones type:z) {
            labels.add(new JLabel(type.toString()));
            zones.add(getGui().getZonePanel(getPlayer(), type));
        }
        
        add(labels, BorderLayout.WEST);
        add(zones);
        JLabel life = new JLabel();
        life.setFont(life.getFont().deriveFont(24f));
        d.add(new LifeTotalUpdater(life, getPlayer().getLifeTotal()));
        add(life, BorderLayout.EAST);
        
        ManaPoolPanel mp = new ManaPoolPanel(getPlayer().getManaPool());
        d.add(mp);
        add(mp, BorderLayout.SOUTH);
    }
    
    public void dispose() {
        d.dispose();
    }
}
