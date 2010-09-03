/**
 * ActionActor.java
 * 
 * Created on 23.08.2010
 */

package net.slightlymagic.laterna.magica.gui.actor.actors;


import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.action.play.ActivateAction;
import net.slightlymagic.laterna.magica.action.play.PlayAction;
import net.slightlymagic.laterna.magica.characteristics.MagicColor;
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
public class ManaActor extends GuiActor {
    private int[] amounts;
    
    public ManaActor(GuiMagicActor actor, int[] amounts) {
        super(actor);
        this.amounts = amounts;
    }
    
    @Override
    public void start() {
        super.start();
        StringBuilder sb = new StringBuilder();
        if(amounts[6] > 0) sb.append("{" + amounts[6] + "}");
        for(int i = 0; i < 5; i++)
            for(int j = 0; j < amounts[i]; j++)
                sb.append("{" + MagicColor.values()[i].getShortChar() + "}");
        for(int i = 0; i < amounts[5]; i++)
            sb.append("{S}");
        
        d.add(setName("Activate mana abilities, or press here to cancel. Mana left to pay: " + sb.toString()));
        d.add(actor.channels.objects.subscribe(actor.channels.fiber, new CardCallback()));
        d.add(actor.channels.passPriority.subscribe(actor.channels.fiber, new PassPriorityCallback()));
    }
    
    @Override
    protected void concede() {
        actor.channels.actions.publish(null);
    }
    
    private class CardCallback implements Callback<MagicObject> {
        @Override
        public void onMessage(MagicObject c) {
            log.debug("Received: " + c);
            PlayAction a = GuiUtil.getActionOptional(actor.getPlayer(), c);
            if(a instanceof ActivateAction) actor.channels.actions.publish(a);
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
