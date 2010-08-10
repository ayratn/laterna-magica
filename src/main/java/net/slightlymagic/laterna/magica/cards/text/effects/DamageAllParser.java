/**
 * DamageAllParser.java
 * 
 * Created on 24.07.2010
 */

package net.slightlymagic.laterna.magica.cards.text.effects;


import static java.lang.Integer.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.ability.AbilityObject;
import net.slightlymagic.laterna.magica.action.AbstractGameAction;
import net.slightlymagic.laterna.magica.action.GameAction;
import net.slightlymagic.laterna.magica.action.play.PlayAction;
import net.slightlymagic.laterna.magica.action.play.PlayInformation;
import net.slightlymagic.laterna.magica.action.play.impl.AbstractPlayInformation;
import net.slightlymagic.laterna.magica.card.CardObject;
import net.slightlymagic.laterna.magica.cards.text.EffectParsers.EffectParser;
import net.slightlymagic.laterna.magica.characteristics.CardType;
import net.slightlymagic.laterna.magica.edit.CompoundEdit;
import net.slightlymagic.laterna.magica.effects.damage.DamagePermanentEvent;
import net.slightlymagic.laterna.magica.util.FactoryFunction;
import net.slightlymagic.laterna.magica.util.MagicaPredicates;

import com.google.common.base.Function;
import com.google.common.base.Predicate;


public class DamageAllParser implements EffectParser {
    private static final Pattern p = Pattern.compile("~ deals (\\d+) damage to all (.*)\\.");
    
    public Function<? super PlayAction, ? extends PlayInformation> parseEffect(String text) {
        Matcher m = p.matcher(text);
        if(!m.matches()) return null;
        
        DamageAllParser.DamageAllConfiguration c = new DamageAllConfiguration();
        
        //TODO parse the specification of what is damaged
        c.p = MagicaPredicates.card(MagicaPredicates.has(CardType.CREATURE));
        c.ammount = parseInt(m.group(1));
        
        return FactoryFunction.getInstance(DamageAllParser.DamageAllConfiguration.class, c, PlayAction.class,
                DamageAllParser.DamageAllInformation.class);
    }
    
    public boolean isManaEffect(String text) {
        return false;
    }
    
    public static class DamageAllConfiguration implements Serializable {
        private static final long      serialVersionUID = 3447507506734631924L;
        
        private Predicate<MagicObject> p;
        private int                    ammount;
        private boolean                combat           = false, preventable = true;
    }
    
    public static class DamageAllInformation extends AbstractPlayInformation {
        private DamageAllParser.DamageAllConfiguration c;
        
        public DamageAllInformation(DamageAllParser.DamageAllConfiguration c, PlayAction action) {
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
                    MagicObject source = getAction().getObject();
                    if(source instanceof AbilityObject) source = ((AbilityObject) source).getObject();
                    
                    List<MagicObject> ob = new ArrayList<MagicObject>();
                    
                    for(MagicObject o:getGame().getBattlefield().getCards())
                        if(c.p.apply(o)) ob.add(o);
                    
                    CompoundEdit ed = new CompoundEdit(getGame(), true, "Deal damage to matching permanents");
                    for(MagicObject o:ob)
                        new DamagePermanentEvent((CardObject) o, source, c.ammount, c.combat, c.preventable).execute();
                    ed.end();
                    
                    return true;
                }
            };
        }
    }
}
