/**
 * BlockerActor.java
 * 
 * Created on 23.08.2010
 */

package net.slightlymagic.laterna.magica.gui.actor.actors;


import static java.lang.String.*;
import net.slightlymagic.laterna.magica.Combat;
import net.slightlymagic.laterna.magica.Combat.Attacker;
import net.slightlymagic.laterna.magica.Combat.Blocker;
import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.card.CardObject;
import net.slightlymagic.laterna.magica.gui.actor.GuiActor;
import net.slightlymagic.laterna.magica.gui.actor.GuiMagicActor;

import org.jetlang.core.Callback;


/**
 * The class BlockerActor.
 * 
 * @version V0.0 23.08.2010
 * @author Clemens Koza
 */
public class BlockerActor extends GuiActor {
    private Combat   combat;
    
    private Attacker attacker;
    
    public BlockerActor(GuiMagicActor actor) {
        super(actor);
        combat = actor.getGame().getCombat();
    }
    
    @Override
    public void start() {
        super.start();
        updateLabel();
        
        d.add(actor.channels.objects.subscribe(actor.channels.fiber, new CardCallback()));
        d.add(actor.channels.passPriority.subscribe(actor.channels.fiber, new PassPriorityCallback()));
    }
    
    @Override
    protected void concede() {
        actor.channels.actions.publish(null);
    }
    
    private void updateLabel() {
        String s;
        if(attacker == null) {
            s = "<html><center>Declare blockers - Select an attacker to block, then the blockers<br/>"
                    + "Click here to continue.</center></html>";
        } else {
            s = format("<html><center>Declare blockers - Select an attacker to block, then the blockers<br/>"
                    + "Currently blocking %s<br/>" + "Click here to continue.</center></html>", attacker);
        }
        d.add(setName(s));
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
                    updateLabel();
                    return;
                }
            } catch(IllegalArgumentException ex) {}
            if(attacker != null) try {
                Blocker b = combat.getBlocker(card);
                if(b == null) b = combat.declareBlocker(card);
                log.debug(valueOf(b));
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
