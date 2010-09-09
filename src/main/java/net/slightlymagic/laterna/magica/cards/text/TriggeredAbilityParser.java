/**
 * TriggeredAbilityParser.java
 * 
 * Created on 09.09.2010
 */

package net.slightlymagic.laterna.magica.cards.text;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.slightlymagic.laterna.magica.ability.TriggeredAbility;
import net.slightlymagic.laterna.magica.ability.impl.TriggeredAbilityImpl;
import net.slightlymagic.laterna.magica.action.play.PlayAction;
import net.slightlymagic.laterna.magica.action.play.PlayInformation;
import net.slightlymagic.laterna.magica.action.play.TriggerAction;
import net.slightlymagic.laterna.magica.action.play.impl.PlayInformationFunction;
import net.slightlymagic.laterna.magica.cards.InvalidCardException;
import net.slightlymagic.laterna.magica.cards.text.TextCardCompiler.LineContext;
import net.slightlymagic.laterna.magica.cards.text.TextHandler.AbilityParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Predicate;


/**
 * The class TriggeredAbilityParser.
 * 
 * @version V0.0 09.09.2010
 * @author Clemens Koza
 */
public class TriggeredAbilityParser implements AbilityParser {
    private static final Logger  log = LoggerFactory.getLogger(TriggeredAbilityParser.class);
    
    private static final Pattern p   = Pattern.compile("(.*?),\\s*(.*)");
    
    public TriggeredAbility getAbility(String text) {
        Matcher m = p.matcher(text);
        if(!m.matches()) throw new IllegalArgumentException("Not a triggered ability");
        
        log.debug("Found triggered ability: \"" + text + "\"");
        Predicate<? super TriggerAction> trigger = TriggerParsers.getTrigger(m.group(1));
        if(trigger == null) throw new IllegalArgumentException("Trigger could not be parsed");
        List<Function<? super PlayAction, ? extends PlayInformation>> effects = EffectParsers.getEffects(m.group(2));
        if(effects == null) throw new IllegalArgumentException("Effect could not be parsed");
        boolean manaAbility = EffectParsers.isManaEffect(m.group(2));
        
        List<Function<? super TriggerAction, ? extends PlayInformation>> all = new ArrayList<Function<? super TriggerAction, ? extends PlayInformation>>();
        all.addAll(effects);
        return new TriggeredAbilityImpl(manaAbility, trigger, new PlayInformationFunction<TriggerAction>(all),
                text);
    }
    
    public boolean apply(LineContext context) throws InvalidCardException {
        try {
            context.getContext().getPart().getAbilities().add(getAbility(context.getValue()));
            return true;
        } catch(IllegalArgumentException ex) {
            return false;
        }
    }
}
