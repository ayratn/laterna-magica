/**
 * DestroyAllParser.java
 * 
 * Created on 24.07.2010
 */

package net.slightlymagic.laterna.magica.cards.text.effects;


import static java.lang.Integer.*;
import static java.lang.String.*;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.slightlymagic.laterna.magica.action.AbstractGameAction;
import net.slightlymagic.laterna.magica.action.GameAction;
import net.slightlymagic.laterna.magica.action.play.PlayAction;
import net.slightlymagic.laterna.magica.action.play.PlayInformation;
import net.slightlymagic.laterna.magica.action.play.impl.AbstractPlayInformation;
import net.slightlymagic.laterna.magica.cards.text.EffectParser;
import net.slightlymagic.laterna.magica.util.FactoryFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;


public class DrawCardsParser implements EffectParser {
    private static final Logger  log = LoggerFactory.getLogger(DrawCardsParser.class);
    
    private static final Pattern p   = Pattern.compile("[Dd]raw (a card|(\\d+) cards|(.*?) cards)");
    
    public Function<? super PlayAction, ? extends PlayInformation> parseEffect(String text) {
        Matcher m = p.matcher(text);
        if(!m.matches()) return null;
        if(m.group(3) != null) {
            //e.g. "Draw two cards" instead of "Draw 2 cards"
            log.error(format("Ability uses the text \"%s\"; convert it to a number, e.g. \"two\" --> 2",
                    m.group(3)));
            return null;
        }
        
        DrawCardsConfiguration c = new DrawCardsConfiguration();
        
        if(m.group(2) == null) c.cards = 1;
        else c.cards = parseInt(m.group(2));
        
        return FactoryFunction.getInstance(DrawCardsConfiguration.class, c, PlayAction.class,
                DrawCardsInformation.class);
    }
    
    public boolean isManaEffect(String text) {
        return false;
    }
    
    public static class DrawCardsConfiguration implements Serializable {
        private static final long serialVersionUID = 5371171920906340326L;
        
        private int               cards;
    }
    
    public static class DrawCardsInformation extends AbstractPlayInformation {
        private DrawCardsConfiguration c;
        
        public DrawCardsInformation(DrawCardsConfiguration c, PlayAction action) {
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
                    for(int i = 0; i < c.cards; i++)
                        if(!getAction().getController().drawCard()) return false;
                    return true;
                }
            };
        }
    }
}
