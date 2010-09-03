/**
 * AssignAttackerDamageActor.java
 * 
 * Created on 23.08.2010
 */

package net.slightlymagic.laterna.magica.gui.actor.actors;


import static java.lang.String.*;
import static java.util.Collections.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

import net.slightlymagic.laterna.magica.Combat.AttackAssignment;
import net.slightlymagic.laterna.magica.Combat.Attacker;
import net.slightlymagic.laterna.magica.Combat.BlockAssignment;
import net.slightlymagic.laterna.magica.Combat.Blocker;
import net.slightlymagic.laterna.magica.Combat.Defender;
import net.slightlymagic.laterna.magica.Combat.PlaneswalkerDefender;
import net.slightlymagic.laterna.magica.Combat.PlayerDefender;
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
    private Map<Object, AttackAssignment>    assignment;
    

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
        
        Defender d = attacker.getAttackerAssignment().getDefender();
        Object key;
        if(d instanceof PlayerDefender) key = ((PlayerDefender) d).getDefendingPlayer();
        else if(d instanceof PlaneswalkerDefender) key = ((PlaneswalkerDefender) d).getDefendingPlaneswalker();
        else throw new AssertionError(d);
        assignment = singletonMap(key, attacker.getAttackerAssignment());
    }
    
    @Override
    public void start() {
        super.start();
        d.add(actor.channels.objects.subscribe(actor.channels.fiber, new CardCallback()));
        d.add(actor.channels.players.subscribe(actor.channels.fiber, new PlayerCallback()));
        
        for(Entry<MagicObject, CardPanel> e:getBattlefield().entrySet()) {
            if(assignments.containsKey(e.getKey())) d.add(setBorder(e.getValue(), furtherBlocker));
            else if(assignment.containsKey(e.getKey())) d.add(setBorder(e.getValue(), furtherBlocker));
            else d.add(setBorder(e.getValue(), other));
        }
        Object key = assignment.keySet().iterator().next();
        if(key instanceof Player) {
            d.add(setBorder(getGui().getPlayerPanel((Player) key).getButton(), furtherBlocker));
        }
        
        update();
        d.add(setEnabled(false));
    }
    
    @Override
    protected void concede() {
        actor.channels.actions.publish(null);
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
        if(allLethal && (order.isEmpty() || CombatUtil.hasTrample(attacker))) {
            Object key = assignment.keySet().iterator().next();
            if(key instanceof Player) {
                d.add(setBorder(getGui().getPlayerPanel((Player) key).getButton(), blocker));
            } else if(key instanceof CardObject) {
                d.add(setBorder(m.get(key), blocker));
            } else throw new AssertionError();
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
                return;
            }
            AttackAssignment a = assignment.get(c);
            if(a != null) {
                a.setAttackerAssignedDamage(a.getAttackerAssignedDamage() + 1);
                damageLeft--;
                update();
                if(damageLeft == 0) actor.channels.actions.publish(null);
                return;
            }
        }
    }
    
    private class PlayerCallback implements Callback<Player> {
        @Override
        public void onMessage(Player p) {
            log.debug("Received: " + p);
            AttackAssignment a = assignment.get(p);
            if(a != null) {
                a.setAttackerAssignedDamage(a.getAttackerAssignedDamage() + 1);
                damageLeft--;
                update();
                if(damageLeft == 0) actor.channels.actions.publish(null);
                return;
            }
        }
    }
}
