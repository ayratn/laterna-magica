/**
 * TapSymbolCost.java
 * 
 * Created on 16.04.2010
 */

package net.slightlymagic.laterna.magica.cost.impl;


import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.ability.AbilityObject;
import net.slightlymagic.laterna.magica.action.AbstractGameAction;
import net.slightlymagic.laterna.magica.action.GameAction;
import net.slightlymagic.laterna.magica.action.play.PlayAction;
import net.slightlymagic.laterna.magica.action.play.impl.AbstractPlayInformation;
import net.slightlymagic.laterna.magica.card.CardObject;
import net.slightlymagic.laterna.magica.card.State.StateType;
import net.slightlymagic.laterna.magica.zone.Zone.Zones;


/**
 * The class TapSymbolCost.
 * 
 * @version V0.0 16.04.2010
 * @author Clemens Koza
 */
public class TapSymbolInformation extends AbstractPlayInformation {
    CardObject card;
    
    public TapSymbolInformation(PlayAction action) {
        super(action);
        MagicObject o = getAction().getObject();
        if(o instanceof CardObject) card = (CardObject) o;
        else if(o instanceof AbilityObject) {
            AbilityObject ability = (AbilityObject) o;
            if(ability.getObject() instanceof CardObject) card = (CardObject) ability.getObject();
            else throw new IllegalArgumentException();
        } else throw new IllegalArgumentException();
    }
    
    @Override
    public GameAction getCost() {
        return new TapSymbolCost(card);
    }
    
    @Override
    public GameAction getEffect() {
        return null;
    }
    
    private static class TapSymbolCost extends AbstractGameAction {
        private CardObject o;
        
        public TapSymbolCost(CardObject o) {
            super(o.getGame());
            this.o = o;
        }
        
        @Override
        public boolean execute() {
            //TODO implement summoning sickness & Haste
            if(o.getZone().getType() != Zones.BATTLEFIELD) return false;
            if(o.getState().getState(StateType.TAPPED)) return false;
            
            o.getState().setState(StateType.TAPPED, true);
            return true;
        }
    }
}
