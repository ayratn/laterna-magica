/**
 * ManaCostInformation.java
 * 
 * Created on 26.04.2010
 */

package net.slightlymagic.laterna.magica.cost.impl;


import java.util.Set;

import net.slightlymagic.laterna.magica.action.AbstractGameAction;
import net.slightlymagic.laterna.magica.action.GameAction;
import net.slightlymagic.laterna.magica.action.play.PlayAction;
import net.slightlymagic.laterna.magica.action.play.impl.AbstractPlayInformation;
import net.slightlymagic.laterna.magica.cost.ManaCost;
import net.slightlymagic.laterna.magica.mana.Mana;
import net.slightlymagic.laterna.magica.mana.ManaSequence;
import net.slightlymagic.laterna.magica.player.Player;



/**
 * The class ManaCostInformation.
 * 
 * @version V0.0 26.04.2010
 * @author Clemens Koza
 */
public class ManaCostInformation extends AbstractPlayInformation {
    private ManaSequence original, resolved;
    
    public ManaCostInformation(ManaSequence cost, PlayAction action) {
        super(action);
        this.original = cost;
    }
    
    @Override
    public void makeChoices() {
        resolved = getAction().getController().getActor().resolveCost(original);
    }
    
    @Override
    public GameAction getCost() {
        return new ManaCostImpl(getAction(), resolved);
    }
    
    @Override
    public GameAction getEffect() {
        return null;
    }
    
    private static class ManaCostImpl extends AbstractGameAction implements ManaCost {
        private PlayAction   action;
        private ManaSequence cost;
        
        public ManaCostImpl(PlayAction action, ManaSequence cost) {
            super(action.getGame());
            this.action = action;
            this.cost = cost;
        }
        
        public ManaSequence getCost() {
            return cost;
        }
        
        @Override
        public boolean execute() {
            Player p = action.getController();
            Set<Mana> mana = p.getActor().getManaToPay(this);
            if(mana == null) return false;
            
            for(Mana m:mana)
                p.getManaPool().removeMana(m);
            
            return true;
        }
    }
}
