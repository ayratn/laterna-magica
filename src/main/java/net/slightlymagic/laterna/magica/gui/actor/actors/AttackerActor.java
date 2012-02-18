/**
 * AttackerActor.java
 * 
 * Created on 23.08.2010
 */

package net.slightlymagic.laterna.magica.gui.actor.actors;


import static java.lang.String.*;
import net.slightlymagic.laterna.magica.Combat;
import net.slightlymagic.laterna.magica.Combat.Attacker;
import net.slightlymagic.laterna.magica.Combat.Defender;
import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.card.CardObject;
import net.slightlymagic.laterna.magica.gui.actor.GuiActor;
import net.slightlymagic.laterna.magica.gui.actor.GuiCallback;
import net.slightlymagic.laterna.magica.gui.actor.GuiMagicActor;
import net.slightlymagic.laterna.magica.player.Player;

import org.jetlang.core.Callback;


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
        super.start();
        updateLabel();
        
        d.add(actor.channels.objects.subscribe(actor.channels.fiber, new CardCallback()));
        d.add(actor.channels.players.subscribe(actor.channels.fiber, new PlayerCallback()));
        d.add(actor.channels.passPriority.subscribe(actor.channels.fiber, new PassPriorityCallback()));
    }
    
    @Override
    protected void concede() {
        actor.channels.actions.publish(null);
    }
    
    private void updateLabel() {
        String s = format(
                "<html><center>Declare attackers - Select a defender to attack, then the attackers.<br/>"
                        + "Currently attacking %s<br/>" + "Click here to continue.</center</html>", defender);
        d.add(setName(s));
    }
    
    private class CardCallback extends GuiCallback<MagicObject> {
        @Override
        public void onMessage0(MagicObject c) {
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
    
    private class PlayerCallback extends GuiCallback<Player> {
        @Override
        public void onMessage0(Player p) {
            log.debug("Received: " + p);
            try {
                defender = combat.getDefender(p);
                return;
            } catch(IllegalArgumentException ex) {}
        }
    }
    
    private class PassPriorityCallback implements Callback<Void> {
        public void onMessage(Void v) {
            log.debug("Received pass priority");
            actor.channels.actions.publish(null);
        }
    }
}
