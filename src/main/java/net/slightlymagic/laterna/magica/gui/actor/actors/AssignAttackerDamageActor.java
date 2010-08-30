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
import net.slightlymagic.laterna.magica.gui.zone.ZonePanelImpl;
import net.slightlymagic.laterna.magica.impl.CombatUtil;
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
        disposables.add(actor.channels.objects.subscribe(actor.channels.fiber, new CardCallback()));
        
        disposables.add(setEnabled(false));
        if(getGui().getZonePanel(Zones.BATTLEFIELD) instanceof ZonePanelImpl) {
            ZonePanelImpl p = (ZonePanelImpl) getGui().getZonePanel(Zones.BATTLEFIELD);
            
            for(Entry<MagicObject, CardPanel> e:p.getShownCards().entrySet()) {
                if(assignments.containsKey(e.getKey())) disposables.add(setBorder(e.getValue(), furtherBlocker));
                else disposables.add(setBorder(e.getValue(), other));
            }
            //TODO handle defenders
        }
        update();
    }
    
    private void update() {
        disposables.add(setName(format("<html><center>Assign %s's damage to blockers and defender<br/>"
                + "%d damage left</center></html>", attacker, damageLeft)));
        if(getGui().getZonePanel(Zones.BATTLEFIELD) instanceof ZonePanelImpl) {
            ZonePanelImpl p = (ZonePanelImpl) getGui().getZonePanel(Zones.BATTLEFIELD);
            Map<MagicObject, CardPanel> m = p.getShownCards();
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
    }
    
    private class CardCallback implements Callback<MagicObject> {
        @Override
        public void onMessage(MagicObject c) {
            log.debug("Received: " + c);
            BlockAssignment b = assignments.get(c);
            if(b != null) {
                b.setAttackerAssignedDamage(b.getAttackerAssignedDamage() + 1);
                damageLeft--;
                if(damageLeft == 0) actor.channels.actions.publish(null);
            }
        }
    }
}
