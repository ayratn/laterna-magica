/**
 * EntersBattlefieldParser.java
 * 
 * Created on 09.09.2010
 */

package net.slightlymagic.laterna.magica.cards.text.triggers;


import net.slightlymagic.laterna.magica.action.play.TriggerAction;
import net.slightlymagic.laterna.magica.cards.text.TriggerParser;
import net.slightlymagic.laterna.magica.impl.MoveCardEvent;
import net.slightlymagic.laterna.magica.zone.Zone.Zones;

import com.google.common.base.Predicate;


/**
 * The class EntersBattlefieldParser.
 * 
 * @version V0.0 09.09.2010
 * @author Clemens Koza
 */
public class EntersBattlefieldParser implements TriggerParser {
    @Override
    public Predicate<? super TriggerAction> parseTrigger(String text) {
        if(!text.equalsIgnoreCase("When ~ enters the battlefield")) return null;
        return EntersBattlefieldPredicate.INSTANCE;
    }
    
    private static enum EntersBattlefieldPredicate implements Predicate<TriggerAction> {
        INSTANCE;
        
        public boolean apply(TriggerAction input) {
            //the event is not a card changing zones
            if(!(input.getTrigger() instanceof MoveCardEvent)) return false;
            MoveCardEvent ev = (MoveCardEvent) input.getTrigger();
            //the event is not about the card the ability is on
            if(ev.getCard() != input.getObject().getObject()) return false;
            //the event is not to the battlefield
            if(ev.getTo() == null || ev.getTo().getType() != Zones.BATTLEFIELD) return false;
            return true;
        }
    }
}
