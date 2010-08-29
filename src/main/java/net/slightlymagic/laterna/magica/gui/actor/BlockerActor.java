/**
 * BlockerActor.java
 * 
 * Created on 23.08.2010
 */

package net.slightlymagic.laterna.magica.gui.actor;


import static java.lang.String.*;

import javax.swing.Action;

import net.slightlymagic.laterna.magica.Combat;
import net.slightlymagic.laterna.magica.Combat.Attacker;
import net.slightlymagic.laterna.magica.Combat.Blocker;
import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.card.CardObject;

import org.jetlang.core.Callback;
import org.jetlang.core.Disposable;


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
        updateLabel();
        
        disposables.add(actor.channels.objects.subscribe(actor.channels.fiber, new CardCallback()));
        disposables.add(actor.channels.passPriority.subscribe(actor.channels.fiber, new PassPriorityCallback()));
    }
    
    private void updateLabel() {
        String s;
        if(attacker == null) {
            s = "Declare blockers - Select an attacker to block, then the blockers";
        } else {
            s = format("<html>Declare blockers - Select an attacker to block, then the blockers.<br/>"
                    + "Currently blocking %s<br/>" + "Click here to continue.</html>", attacker);
        }
        disposables.add(setName(s));
    }
    
    private Disposable setName(final String newName) {
        return new Disposable() {
            private String oldName;
            
            {
                oldName = (String) actor.getGui().getPassPriorityAction().getValue(Action.NAME);
                actor.getGui().getPassPriorityAction().putValue(Action.NAME, newName);
            }
            
            @Override
            public void dispose() {
                actor.getGui().getPassPriorityAction().putValue(Action.NAME, oldName);
            }
        };
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
