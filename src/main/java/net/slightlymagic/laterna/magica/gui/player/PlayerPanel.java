/**
 * PlayerPanel.java
 * 
 * Created on 08.04.2010
 */

package net.slightlymagic.laterna.magica.gui.player;


import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import net.slightlymagic.laterna.magica.gui.mana.ManaPoolPanel;
import net.slightlymagic.laterna.magica.gui.zone.ZoneSizeUpdater;
import net.slightlymagic.laterna.magica.player.LifeTotal;
import net.slightlymagic.laterna.magica.player.Player;
import net.slightlymagic.laterna.magica.zone.Zone;
import net.slightlymagic.laterna.magica.zone.Zone.Zones;


/**
 * The class PlayerPanel.
 * 
 * @version V0.0 08.04.2010
 * @author Clemens Koza
 */
public class PlayerPanel extends JPanel {
    private static final long serialVersionUID = -5284446908967592513L;
    
    private Player            player;
    
    public PlayerPanel(Player p) {
        super(null);
        player = p;
        setupComponents();
    }
    
    public Player getPlayer() {
        return player;
    }
    
    protected void setupComponents() {
        setBorder(BorderFactory.createTitledBorder(getPlayer().toString()));
        setLayout(new BorderLayout(4, 4));
        JPanel labels = new JPanel(new GridLayout(0, 1));
        JPanel zones = new JPanel(new GridLayout(0, 1));
        Zones[] z = {Zones.LIBRARY, Zones.GRAVEYARD};
        for(Zones type:z) {
            Zone zone = getPlayer().getZone(type);
            
            labels.add(new JLabel(type.toString()));
            
            JLabel size = new JLabel("", SwingConstants.TRAILING);
            zone.addPropertyChangeListener(Zone.CARDS, new ZoneSizeUpdater(size, zone));
            zones.add(size);
        }
        
        add(labels, BorderLayout.WEST);
        add(zones);
        JLabel life = new JLabel();
        life.setFont(life.getFont().deriveFont(24f));
        LifeTotal lifeTotal = getPlayer().getLifeTotal();
        lifeTotal.addPropertyChangeListener(LifeTotal.LIFE_TOTAL, new LifeTotalUpdater(life, lifeTotal));
        add(life, BorderLayout.EAST);
        
        add(new ManaPoolPanel(getPlayer().getManaPool()), BorderLayout.SOUTH);
    }
}
