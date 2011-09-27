/**
 * TextHandler.java
 * 
 * Created on 24.04.2010
 */

package net.slightlymagic.laterna.magica.cards.text;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.slightlymagic.laterna.magica.action.play.ActivateAction;
import net.slightlymagic.laterna.magica.action.play.PlayAction;
import net.slightlymagic.laterna.magica.action.play.PlayInformation;
import net.slightlymagic.laterna.magica.action.play.TriggerAction;
import net.slightlymagic.laterna.magica.cards.InvalidCardException;
import net.slightlymagic.laterna.magica.cards.text.TextCardCompiler.LineContext;
import net.slightlymagic.laterna.magica.cards.text.TextCardCompiler.LineHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Predicate;


public class TextHandler implements LineHandler {
    private static final Logger              log       = LoggerFactory.getLogger(TextHandler.class);
    private static TextHandler               instance;
    
    private final Map<String, AbilityParser> abilities = new HashMap<String, AbilityParser>();
    private final Map<String, CostParser>    costs     = new HashMap<String, CostParser>();
    private final Map<String, EffectParser>  effects   = new HashMap<String, EffectParser>();
    private final Map<String, TriggerParser> triggers  = new HashMap<String, TriggerParser>();
    
    public TextHandler() {
        if(instance != null) throw new AssertionError("Multiple instances of TextHandler");
        instance = this;
    }
    
    public static TextHandler textHandler() {
        return instance;
    }
    
    public void setAbility(String key, AbilityParser ability) {
        abilities.put(key, ability);
    }
    
    public void setCost(String key, CostParser cost) {
        costs.put(key, cost);
    }
    
    public void setEffect(String key, EffectParser effect) {
        effects.put(key, effect);
    }
    
    public void setTrigger(String key, TriggerParser trigger) {
        triggers.put(key, trigger);
    }
    
    public List<Function<? super ActivateAction, ? extends PlayInformation>> getCosts(String text) {
        String[] texts = text.split(",\\s*");
        List<Function<? super ActivateAction, ? extends PlayInformation>> result = new ArrayList<Function<? super ActivateAction, ? extends PlayInformation>>();
        for(String cost:texts) {
            for(CostParser parser:costs.values()) {
                Function<? super ActivateAction, ? extends PlayInformation> fn = parser.parseCost(cost);
                if(fn != null) {
                    result.add(fn);
                    break;
                }
            }
        }
        if(result.isEmpty()) {
            log.debug("Could not parse \"" + text + "\"");
            return null;
        }
        return result;
    }
    
    public Predicate<? super TriggerAction> getTrigger(String text) {
        for(TriggerParser parser:triggers.values()) {
            Predicate<? super TriggerAction> trigger = parser.parseTrigger(text);
            if(trigger != null) return trigger;
        }
        return null;
    }
    
    public List<Function<? super PlayAction, ? extends PlayInformation>> getEffects(String text) {
        String[] texts = text.split("[,\\.]\\s*");
        List<Function<? super PlayAction, ? extends PlayInformation>> result = new ArrayList<Function<? super PlayAction, ? extends PlayInformation>>();
        for(String effect:texts) {
            for(EffectParser parser:effects.values()) {
                Function<? super PlayAction, ? extends PlayInformation> fn = parser.parseEffect(effect);
                if(fn != null) {
                    result.add(fn);
                    break;
                }
            }
        }
        if(result.isEmpty()) {
            log.debug("Could not parse \"" + text + "\"");
            return null;
        }
        return result;
    }
    
    public boolean isManaEffect(String text) {
        String[] texts = text.split("[,\\.]\\s*");
        for(String effect:texts)
            for(EffectParser parser:effects.values())
                //TODO optimize
                if(parser.parseEffect(effect) != null && parser.isManaEffect(effect)) return true;
        return false;
    }
    
    public void apply(LineContext from) throws InvalidCardException {
        for(AbilityParser parser:abilities.values())
            if(parser.apply(from)) return;
        from.getContext().warn(
                "\"" + from.getContext().getPart() + "\": \"" + from.getValue()
                        + "\" could not be parsed as an ability");
    }
    
    public static interface AbilityParser {
        public boolean apply(LineContext context) throws InvalidCardException;
    }
}
