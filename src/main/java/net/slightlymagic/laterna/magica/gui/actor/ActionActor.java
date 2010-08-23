/**
 * ActionActor.java
 * 
 * Created on 23.08.2010
 */

package net.slightlymagic.laterna.magica.gui.actor;


import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.action.play.PlayAction;
import net.slightlymagic.laterna.magica.gui.util.GuiUtil;

import org.jetlang.core.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The class ActionActor.
 * 
 * @version V0.0 23.08.2010
 * @author Clemens Koza
 */
public class ActionActor extends GuiActor {
    private static final Logger log = LoggerFactory.getLogger(ActionActor.class);
    
    public ActionActor(GuiMagicActor actor) {
        super(actor);
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
            PlayAction a = GuiUtil.getActionOptional(actor.getPlayer(), c);
            if(a != null) actor.channels.actions.publish(a);
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