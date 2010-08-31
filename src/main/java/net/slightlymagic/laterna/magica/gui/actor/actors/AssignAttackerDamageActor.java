/**
 * AssignAttackerDamageActor.java
 * 
 * Created on 23.08.2010
 */

package net.slightlymagic.laterna.magica.gui.actor.actors;


import static java.lang.String.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

import net.slightlymagic.laterna.magica.Combat.Attacker;
import net.slightlymagic.laterna.magica.Combat.BlockAssignment;
import net.slightlymagic.laterna.magica.Combat.Blocker;
import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.card.CardObject;
import net.slightlymagic.laterna.magica.gui.actor.GuiActor;
import net.slightlymagic.laterna.magica.gui.actor.GuiMagicActor;
import net.slightlymagic.laterna.magica.gui.card.CardPanel;
import net.slightlymagic.laterna.magica.gui.zone.ZoneCardsPanel;
import net.slightlymagic.laterna.magica.impl.CombatUtil;
import net.slightlymagic.laterna.magica.player.Player;
import net.slightlymagic.laterna.magica.zone.Zone.Zones;

import org.jetlang.core.Callback;


/**
 * The class AssignAttackerDamageActor.
 * 
 * @version V0.0 23.08.2010
 * @author Clemens Koza
 */
public class AssignAttackerDamageActor extends GuiActor {
    private Attacker                         attacker;
    private int                              damageLeft;
    private List<CardObject>                 order;
    private Map<CardObject, BlockAssignment> assignments;
    

    private static final Border              blocker        = BorderFactory.createLineBorder(Color.RED, 4);
    private static final Border              furtherBlocker = BorderFactory.createLineBorder(Color.RED, 2);
    private static final Border              other          = BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2);
    
    public AssignAttackerDamageActor(GuiMagicActor actor, Attacker attacker) {
        super(actor);
        this.attacker = attacker;
        damageLeft = CombatUtil.getAmmount(attacker);
        
        order = new ArrayList<CardObject>();
        for(Blocker b:attacker.getDamageAssignmentOrder())
            order.add(b.getBlocker());
        assignments = new HashMap<CardObject, BlockAssignment>();
        for(BlockAssignment ass:attacker.getBlockers().values())
            assignments.put(ass.getBlocker().getBlocker(), ass);
    }
    
    @Override
    public void start() {
        d.add(actor.channels.objects.subscribe(actor.channels.fiber, new CardCallback()));
        
        for(Entry<MagicObject, CardPanel> e:getBattlefield().entrySet()) {
            if(assignments.containsKey(e.getKey())) d.add(setBorder(e.getValue(), furtherBlocker));
            else d.add(setBorder(e.getValue(), other));
        }
        //TODO handle defenders
        
        update();
        d.add(setEnabled(false));
    }
    
    private void update() {
        d.add(setName(format("<html><center>Assign %s's damage to blockers and defender<br/>"
                + "%d damage left</center></html>", attacker, damageLeft)));
        Map<MagicObject, CardPanel> m = getBattlefield();
        boolean allLethal = true;
        for(CardObject c:order) {
            m.get(c).setBorder(blocker);
            if(!CombatUtil.isLethal(assignments.get(c).getBlocker())) {
                allLethal = false;
                break;
            }
        }
        if(allLethal) {
            //TODO handle defenders
        }
    }
    
    private Map<MagicObject, CardPanel> getBattlefield() {
        Map<MagicObject, CardPanel> cards = new HashMap<MagicObject, CardPanel>();
        for(Player p:getGui().getGame().getPlayers())
            if(getGui().getZonePanel(p, Zones.BATTLEFIELD) instanceof ZoneCardsPanel) cards.putAll(((ZoneCardsPanel) getGui().getZonePanel(
                    p, Zones.BATTLEFIELD)).getShownCards());
        return cards;
    }
    
    private class CardCallback implements Callback<MagicObject> {
        @Override
        public void onMessage(MagicObject c) {
            log.debug("Received: " + c);
            BlockAssignment b = assignments.get(c);
            if(b != null) {
                b.setAttackerAssignedDamage(b.getAttackerAssignedDamage() + 1);
                damageLeft--;
                update();
                if(damageLeft == 0) actor.channels.actions.publish(null);
            }
        }
    }
}
