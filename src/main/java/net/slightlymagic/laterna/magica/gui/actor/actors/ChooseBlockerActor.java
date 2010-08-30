/**
 * ChooseBlockerActor.java
 * 
 * Created on 23.08.2010
 */

package net.slightlymagic.laterna.magica.gui.actor.actors;


import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

import net.slightlymagic.laterna.magica.Combat.Blocker;
import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.card.CardObject;
import net.slightlymagic.laterna.magica.gui.actor.GuiActor;
import net.slightlymagic.laterna.magica.gui.actor.GuiMagicActor;
import net.slightlymagic.laterna.magica.gui.card.CardPanel;
import net.slightlymagic.laterna.magica.gui.zone.ZonePanelImpl;
import net.slightlymagic.laterna.magica.zone.Zone.Zones;

import org.jetlang.core.Callback;


/**
 * The class ChooseBlockerActor.
 * 
 * @version V0.0 23.08.2010
 * @author Clemens Koza
 */
public class ChooseBlockerActor extends GuiActor {
    private Map<CardObject, Blocker> choices;
    private static final Border      blocker = BorderFactory.createLineBorder(Color.RED, 4);
    private static final Border      other   = BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2);
    
    public ChooseBlockerActor(GuiMagicActor actor, Collection<? extends Blocker> choices) {
        super(actor);
        this.choices = new HashMap<CardObject, Blocker>();
        for(Blocker a:choices) {
            this.choices.put(a.getBlocker(), a);
        }
    }
    
    @Override
    public void start() {
        disposables.add(actor.channels.objects.subscribe(actor.channels.fiber, new CardCallback()));
        
        disposables.add(setName("Choose attacker to assign damage for"));
        disposables.add(setEnabled(false));
        if(getGui().getZonePanel(Zones.BATTLEFIELD) instanceof ZonePanelImpl) {
            ZonePanelImpl p = (ZonePanelImpl) getGui().getZonePanel(Zones.BATTLEFIELD);
            
            for(Entry<MagicObject, CardPanel> e:p.getShownCards().entrySet()) {
                if(choices.containsKey(e.getKey())) disposables.add(setBorder(e.getValue(), blocker));
                else disposables.add(setBorder(e.getValue(), other));
            }
        }
    }
    
    private class CardCallback implements Callback<MagicObject> {
        @Override
        public void onMessage(MagicObject c) {
            log.debug("Received: " + c);
            Blocker b = choices.get(c);
            if(b != null) actor.channels.blockers.publish(b);
        }
    }
}
