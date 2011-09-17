/**
 * ManaCostParser.java
 * 
 * Created on 24.07.2010
 */

package net.slightlymagic.laterna.magica.cards.text.costs;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.action.GameAction;
import net.slightlymagic.laterna.magica.action.play.ActivateAction;
import net.slightlymagic.laterna.magica.action.play.PlayInformation;
import net.slightlymagic.laterna.magica.action.play.impl.AbstractPlayInformation;
import net.slightlymagic.laterna.magica.cards.text.CostParser;
import net.slightlymagic.laterna.magica.effects.SacrificePermanentEvent;
import net.slightlymagic.laterna.magica.util.FactoryFunction;

import com.google.common.base.Function;


public class SacrificeSelfParser implements CostParser {
    private static final Pattern p = Pattern.compile("[Ss]acrifice ~");
    
    
    public Function<? super ActivateAction, ? extends PlayInformation> parseCost(String text) {
        Matcher m = p.matcher(text);
        if(!m.matches()) return null;
        
        return FactoryFunction.getInstance(ActivateAction.class, SacrificeSelfInformation.class);
    }
    
    public static class SacrificeSelfInformation extends AbstractPlayInformation {
        private MagicObject permanent;
        
        public SacrificeSelfInformation(ActivateAction action) {
            super(action);
            permanent = action.getObject().getObject();
        }
        
        @Override
        public GameAction getCost() {
            return new SacrificePermanentEvent(permanent);
        }
        
        @Override
        public GameAction getEffect() {
            return null;
        }
    }
}
