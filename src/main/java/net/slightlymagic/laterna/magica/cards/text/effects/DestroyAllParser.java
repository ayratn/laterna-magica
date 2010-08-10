/**
 * DestroyAllParser.java
 * 
 * Created on 24.07.2010
 */

package net.slightlymagic.laterna.magica.cards.text.effects;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.action.AbstractGameAction;
import net.slightlymagic.laterna.magica.action.GameAction;
import net.slightlymagic.laterna.magica.action.play.PlayAction;
import net.slightlymagic.laterna.magica.action.play.PlayInformation;
import net.slightlymagic.laterna.magica.action.play.impl.AbstractPlayInformation;
import net.slightlymagic.laterna.magica.cards.text.EffectParsers.EffectParser;
import net.slightlymagic.laterna.magica.characteristics.CardType;
import net.slightlymagic.laterna.magica.edit.CompoundEdit;
import net.slightlymagic.laterna.magica.effects.DestroyPermanentEvent;
import net.slightlymagic.laterna.magica.util.FactoryFunction;
import net.slightlymagic.laterna.magica.util.MagicaPredicates;


import com.google.common.base.Function;
import com.google.common.base.Predicate;


public class DestroyAllParser implements EffectParser {
    private static final Pattern p = Pattern.compile("Destroy all (.*)\\.( They can't be regenerated\\.)?");
    
    public Function<? super PlayAction, ? extends PlayInformation> parseEffect(String text) {
        Matcher m = p.matcher(text);
        if(!m.matches()) return null;
        
        DestroyAllParser.DestroyAllConfiguration c = new DestroyAllConfiguration();
        
        //TODO parse the specification of what is destroyed
        c.p = MagicaPredicates.card(MagicaPredicates.has(CardType.CREATURE));
        c.regen = m.group(2) == null;
        
        return FactoryFunction.getInstance(DestroyAllParser.DestroyAllConfiguration.class, c, PlayAction.class,
                DestroyAllParser.DestroyAllInformation.class);
    }
    
    public boolean isManaEffect(String text) {
        return false;
    }
    
    public static class DestroyAllConfiguration implements Serializable {
        private static final long      serialVersionUID = 5371171920906340326L;
        
        private Predicate<MagicObject> p;
        private boolean                regen;
    }
    
    public static class DestroyAllInformation extends AbstractPlayInformation {
        private DestroyAllParser.DestroyAllConfiguration c;
        
        public DestroyAllInformation(DestroyAllParser.DestroyAllConfiguration c, PlayAction action) {
            super(action);
            this.c = c;
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
                    List<MagicObject> ob = new ArrayList<MagicObject>();
                    
                    for(MagicObject o:getGame().getBattlefield().getCards())
                        if(c.p.apply(o)) ob.add(o);
                    
                    CompoundEdit ed = new CompoundEdit(getGame(), true, "Destroy matching permanents");
                    for(MagicObject o:ob)
                        new DestroyPermanentEvent(o, c.regen).execute();
                    ed.end();
                    
                    return true;
                }
            };
        }
    }
}
