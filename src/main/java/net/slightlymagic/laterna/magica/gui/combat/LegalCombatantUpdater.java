/**
 * LegalCombatantUpdater.java
 * 
 * Created on 27.08.2010
 */

package net.slightlymagic.laterna.magica.gui.combat;


import static net.slightlymagic.laterna.magica.impl.CombatUtil.*;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

import net.slightlymagic.laterna.magica.Combat;
import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.card.CardObject;
import net.slightlymagic.laterna.magica.gui.Gui;
import net.slightlymagic.laterna.magica.gui.card.CardPanel;
import net.slightlymagic.laterna.magica.gui.zone.ZonePanelImpl;
import net.slightlymagic.laterna.magica.player.Player;
import net.slightlymagic.laterna.magica.zone.Zone.Zones;


/**
 * The class LegalCombatantUpdater.
 * 
 * @version V0.0 27.08.2010
 * @author Clemens Koza
 */
public class LegalCombatantUpdater implements PropertyChangeListener {
    private final Gui                    gui;
    private final Map<CardPanel, Border> borders = new HashMap<CardPanel, Border>();
    private Border                       t, f;
    
    public LegalCombatantUpdater(Gui gui) {
        this.gui = gui;
        t = BorderFactory.createLineBorder(Color.RED, 2);
        f = BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2);
        getGui().getGame().addPropertyChangeListener("combat", this);
        update();
    }
    
    public Gui getGui() {
        return gui;
    }
    
    private void update() {
        for(Entry<CardPanel, Border> e:borders.entrySet())
            e.getKey().setBorder(e.getValue());
        borders.clear();
        
        Combat c = getGui().getGame().getCombat();
        if(c != null) {
            for(Player p:c.getAttackingPlayers())
                treat(p);
            for(Player p:c.getDefendingPlayers())
                treat(p);
        }
    }
    
    private void treat(Player pl) {
        if(!(getGui().getZonePanel(pl, Zones.BATTLEFIELD) instanceof ZonePanelImpl)) return;
        
        ZonePanelImpl z = (ZonePanelImpl) getGui().getZonePanel(pl, Zones.BATTLEFIELD);
        for(Entry<MagicObject, CardPanel> e:z.getShownCards().entrySet()) {
            CardObject c = (CardObject) e.getKey();
            CardPanel p = e.getValue();
            
            boolean combatant = isLegalAttacker(c) || isLegalBlocker(c) || isLegalDefendingPlaneswalker(c);
            borders.put(p, p.getBorder());
            p.setBorder(combatant? t:f);
            
        }
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        update();
    }
}
