/**
 * AddManaParser.java
 * 
 * Created on 24.07.2010
 */

package net.slightlymagic.laterna.magica.cards.text.effects;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.slightlymagic.laterna.magica.action.AbstractGameAction;
import net.slightlymagic.laterna.magica.action.GameAction;
import net.slightlymagic.laterna.magica.action.play.PlayAction;
import net.slightlymagic.laterna.magica.action.play.PlayInformation;
import net.slightlymagic.laterna.magica.action.play.impl.AbstractPlayInformation;
import net.slightlymagic.laterna.magica.cards.text.EffectParsers.EffectParser;
import net.slightlymagic.laterna.magica.mana.ManaSequence;
import net.slightlymagic.laterna.magica.mana.impl.ManaFactoryImpl;
import net.slightlymagic.laterna.magica.util.FactoryFunction;
import net.slightlymagic.laterna.magica.util.MagicaUtils;


import com.google.common.base.Function;


public class AddManaParser implements EffectParser {
    private static final Pattern p = Pattern.compile("[Aa]dd (.*) to your mana pool\\.");
    
    public Function<? super PlayAction, ? extends PlayInformation> parseEffect(String text) {
        Matcher m = p.matcher(text);
        if(!m.matches()) return null;
        ManaSequence mana = ManaFactoryImpl.INSTANCE.parseSequence(m.group(1));
        
        return FactoryFunction.getInstance(ManaSequence.class, mana, PlayAction.class,
                AddManaParser.AddManaInformation.class);
    }
    
    public boolean isManaEffect(String text) {
        return true;
    }
    
    public static class AddManaInformation extends AbstractPlayInformation {
        private ManaSequence mana;
        
        public AddManaInformation(ManaSequence mana, PlayAction action) {
            super(action);
            this.mana = mana;
        }
        
        
        @Override
        public GameAction getCost() {
            return null;
        }
        
        
        @Override
        public GameAction getEffect() {
            return new AbstractGameAction(getGame()) {
                
                @Override
                public boolean execute() {
                    MagicaUtils.addMana(getAction().getController(), mana);
                    return true;
                }
            };
        }
    }
}
