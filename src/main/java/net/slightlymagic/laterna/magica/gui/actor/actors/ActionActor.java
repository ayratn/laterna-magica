/**
 * ActionActor.java
 * 
 * Created on 23.08.2010
 */

package net.slightlymagic.laterna.magica.gui.actor.actors;


import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.action.play.PlayAction;
import net.slightlymagic.laterna.magica.gui.actor.GuiActor;
import net.slightlymagic.laterna.magica.gui.actor.GuiMagicActor;
import net.slightlymagic.laterna.magica.gui.util.GuiUtil;

import org.jetlang.core.Callback;


/**
 * The class ActionActor.
 * 
 * @version V0.0 23.08.2010
 * @author Clemens Koza
 */
public class ActionActor extends GuiActor {
    public ActionActor(GuiMagicActor actor) {
        super(actor);
    }
    
    @Override
    public void start() {
        super.start();
        d.add(actor.channels.objects.subscribe(actor.channels.fiber, new CardCallback()));
        d.add(actor.channels.passPriority.subscribe(actor.channels.fiber, new PassPriorityCallback()));
    }
    
    @Override
    protected void concede() {
        actor.channels.actions.publish(null);
    }
    
    private class CardCallback implements Callback<MagicObject> {
        public void onMessage(MagicObject c) {
            log.debug("Received: " + c);
            PlayAction a = GuiUtil.getActionOptional(actor.getPlayer(), c);
            if(a != null) actor.channels.actions.publish(a);
        }
    }
    
    private class PassPriorityCallback implements Callback<Void> {
        public void onMessage(Void v) {
            log.debug("Received pass priority");
            actor.channels.actions.publish(null);
        }
    }
}
