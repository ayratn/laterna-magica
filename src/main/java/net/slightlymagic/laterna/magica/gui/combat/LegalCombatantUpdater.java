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
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

import net.slightlymagic.beans.properties.bound.PropertyChangeMapListener.MapEvent;
import net.slightlymagic.laterna.magica.Combat;
import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.card.CardObject;
import net.slightlymagic.laterna.magica.gui.Gui;
import net.slightlymagic.laterna.magica.gui.card.CardPanel;
import net.slightlymagic.laterna.magica.gui.zone.ZoneCardsPanel;
import net.slightlymagic.laterna.magica.player.Player;
import net.slightlymagic.laterna.magica.zone.Zone.Zones;


/**
 * The class LegalCombatantUpdater.
 * 
 * @version V0.0 27.08.2010
 * @author Clemens Koza
 */
public class LegalCombatantUpdater implements PropertyChangeListener {
    private final Gui gui;
    private Border    normal;
    private Border    nonCombatant, legalCombatant, actualCombatant;
    
    public LegalCombatantUpdater(Gui gui) {
        this.gui = gui;
        normal = BorderFactory.createLineBorder(Color.BLACK, 2);
        nonCombatant = BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2);
        legalCombatant = BorderFactory.createLineBorder(Color.RED, 2);
        actualCombatant = BorderFactory.createLineBorder(Color.RED, 4);
        getGui().getGame().addPropertyChangeListener("combat", this);
        updateCombatants();
    }
    
    public Gui getGui() {
        return gui;
    }
    
    private void updateCombatants() {
        Combat combat = getGui().getGame().getCombat();
        if(combat != null) {
            combat.addPropertyChangeListener("attackers", this);
            combat.addPropertyChangeListener("blockers", this);
            combat.addPropertyChangeListener("defenders", this);
        }
        for(Player p:getGui().getGame().getPlayers())
            treat(p);
    }
    
    private void treat(Player pl) {
        if(!(getGui().getZonePanel(pl, Zones.BATTLEFIELD) instanceof ZoneCardsPanel)) return;
        
        ZoneCardsPanel z = (ZoneCardsPanel) getGui().getZonePanel(pl, Zones.BATTLEFIELD);
        for(Entry<MagicObject, CardPanel> e:z.getShownCards().entrySet()) {
            treat((CardObject) e.getKey());
        }
    }
    
    private void treat(CardObject c) {
        if(!(getGui().getZonePanel(c.getController(), Zones.BATTLEFIELD) instanceof ZoneCardsPanel)) return;
        ZoneCardsPanel z = (ZoneCardsPanel) getGui().getZonePanel(c.getController(), Zones.BATTLEFIELD);
        
        CardPanel p = z.getShownCards().get(c);
        
        Combat combat = getGui().getGame().getCombat();
        if(combat == null) {
            p.setBorder(normal);
        } else {
            Boolean b = null;
            if(isLegalAttacker(c)) b = combat.getAttacker(c) != null;
            else if(isLegalBlocker(c)) b = combat.getBlocker(c) != null;
            else if(isLegalDefendingPlaneswalker(c)) b = combat.getDefender(c) != null;
            
            if(b == null) p.setBorder(nonCombatant);
            else if(b) p.setBorder(actualCombatant);
            else p.setBorder(legalCombatant);
        }
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getSource() instanceof Game) updateCombatants();
        else if(evt.getSource() instanceof Combat) {
            MapEvent<?, ?> ev = (MapEvent<?, ?>) evt;
            if(ev.getKey() instanceof CardObject) treat((CardObject) ev.getKey());
        }
    }
}
