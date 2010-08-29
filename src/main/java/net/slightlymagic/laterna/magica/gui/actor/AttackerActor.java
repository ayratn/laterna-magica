/**
 * AttackerActor.java
 * 
 * Created on 23.08.2010
 */

package net.slightlymagic.laterna.magica.gui.actor;


import static java.lang.String.*;

import javax.swing.Action;

import net.slightlymagic.laterna.magica.Combat;
import net.slightlymagic.laterna.magica.Combat.Attacker;
import net.slightlymagic.laterna.magica.Combat.Defender;
import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.card.CardObject;
import net.slightlymagic.laterna.magica.player.Player;

import org.jetlang.core.Callback;
import org.jetlang.core.Disposable;


/**
 * The class AttackerActor.
 * 
 * @version V0.0 23.08.2010
 * @author Clemens Koza
 */
public class AttackerActor extends GuiActor {
    private Combat   combat;
    
    private Defender defender;
    
    public AttackerActor(GuiMagicActor actor) {
        super(actor);
        combat = actor.getGame().getCombat();
        defender = combat.getDefender(combat.getDefendingPlayers().get(0));
    }
    
    @Override
    public void start() {
        updateLabel();
        
        disposables.add(actor.channels.objects.subscribe(actor.channels.fiber, new CardCallback()));
        disposables.add(actor.channels.players.subscribe(actor.channels.fiber, new PlayerCallback()));
        disposables.add(actor.channels.passPriority.subscribe(actor.channels.fiber, new PassPriorityCallback()));
    }
    
    private void updateLabel() {
        String s = format(
                "<html><center>Declare attackers - Select a defender to attack, then the attackers.<br/>"
                        + "Currently attacking %s<br/>" + "Click here to continue.</center</html>", defender);
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
                defender = combat.getDefender(card);
                updateLabel();
                return;
            } catch(IllegalArgumentException ex) {}
            try {
                Attacker a = combat.getAttacker(card);
                if(a == null) a = combat.declareAttacker(card);
                combat.assignAttacker(a, defender);
                return;
            } catch(IllegalArgumentException ex) {}
        }
    }
    
    private class PlayerCallback implements Callback<Player> {
        @Override
        public void onMessage(Player p) {
            log.debug("Received: " + p);
            try {
                defender = combat.getDefender(p);
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
