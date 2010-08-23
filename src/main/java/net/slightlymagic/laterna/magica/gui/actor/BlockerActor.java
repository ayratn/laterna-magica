/**
 * BlockerActor.java
 * 
 * Created on 23.08.2010
 */

package net.slightlymagic.laterna.magica.gui.actor;


import net.slightlymagic.laterna.magica.Combat;
import net.slightlymagic.laterna.magica.Combat.Attacker;
import net.slightlymagic.laterna.magica.Combat.Blocker;
import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.card.CardObject;

import org.jetlang.core.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The class BlockerActor.
 * 
 * @version V0.0 23.08.2010
 * @author Clemens Koza
 */
public class BlockerActor extends GuiActor {
    private static final Logger log = LoggerFactory.getLogger(BlockerActor.class);
    
    private Combat              combat;
    
    private Attacker            attacker;
    
    public BlockerActor(GuiMagicActor actor) {
        super(actor);
        combat = actor.getGame().getCombat();
    }
    
    @Override
    public void start() {
        disposables.add(actor.channels.objects.subscribe(actor.channels.fiber, new CardCallback()));
        disposables.add(actor.channels.passPriority.subscribe(actor.channels.fiber, new PassPriorityCallback()));
    }
    
    private class CardCallback implements Callback<MagicObject> {
        @Override
        public void onMessage(MagicObject c) {
            log.debug("Received: " + c);
            if(!(c instanceof CardObject)) return;
            CardObject card = (CardObject) c;
            try {
                Attacker a = combat.getAttacker(card);
                if(a != null) {
                    attacker = a;
                    return;
                }
            } catch(IllegalArgumentException ex) {}
            try {
                Blocker b = combat.getBlocker(card);
                if(b == null) b = combat.declareBlocker(card);
                combat.assignBlocker(b, attacker);
                return;
            } catch(IllegalArgumentException ex) {}
        }
    }
    
    private class PassPriorityCallback implements Callback<Void> {
        @Override
        public void onMessage(Void v) {
            log.debug("Received pass priority");
            actor.channels.actions.publish(null);
        }
    }
}
