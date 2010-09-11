/**
 * LegalCombatantUpdater.java
 * 
 * Created on 27.08.2010
 */

package net.slightlymagic.laterna.magica.gui.combat;


import static java.awt.Color.*;
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
    
    private static final int ILLEGAL = 0x01, LEGAL = 0x02, ACTUAL = 0x04;
    private static final int ATTACKER = 0x10, BLOCKER = 0x20, DEFENDER = 0x40;
    
    private static Border getBorder(int key) {
        int thickness;
        switch(key & 0x0F) {
            case ILLEGAL:
                return BorderFactory.createLineBorder(LIGHT_GRAY, 2);
            case LEGAL:
                thickness = 2;
            break;
            case ACTUAL:
                thickness = 4;
            break;
            default:
                throw new IllegalArgumentException("" + key);
        }
        Color color;
        switch(key & 0xF0) {
            case ATTACKER:
                color = RED;
            break;
            case BLOCKER:
                color = GREEN;
            break;
            case DEFENDER:
                color = BLUE;
            break;
            default:
                throw new IllegalArgumentException("" + key);
        }
        return BorderFactory.createLineBorder(color, thickness);
    }
    
    public LegalCombatantUpdater(Gui gui) {
        this.gui = gui;
        normal = BorderFactory.createLineBorder(Color.BLACK, 2);
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
            int key;
            if(isLegalAttacker(c)) {
                key = ATTACKER | (combat.getAttacker(c) != null? ACTUAL:LEGAL);
            } else if(isLegalBlocker(c)) {
                key = BLOCKER | (combat.getBlocker(c) != null? ACTUAL:LEGAL);
            } else if(isLegalDefendingPlaneswalker(c)) {
                key = DEFENDER | (combat.getDefender(c) != null? ACTUAL:LEGAL);
            } else key = ILLEGAL;
            
            p.setBorder(getBorder(key));
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
