/**
 * ActivatedAbilityParser.java
 * 
 * Created on 25.04.2010
 */

package net.slightlymagic.laterna.magica.cards.text;


import static net.slightlymagic.laterna.magica.cards.text.TextHandler.*;

import java.util.List;

import net.slightlymagic.laterna.magica.action.play.PlayAction;
import net.slightlymagic.laterna.magica.action.play.PlayInformation;
import net.slightlymagic.laterna.magica.cards.InvalidCardException;
import net.slightlymagic.laterna.magica.cards.text.TextCardCompiler.LineContext;
import net.slightlymagic.laterna.magica.cards.text.TextHandler.AbilityParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;


/**
 * The class ActivatedAbilityParser.
 * 
 * @version V0.0 25.04.2010
 * @author Clemens Koza
 */
public class SpellEffectParser implements AbilityParser {
    private static final Logger log = LoggerFactory.getLogger(SpellEffectParser.class);
    
    public List<? extends Function<? super PlayAction, ? extends PlayInformation>> getEffects(String text) {
        log.debug("Found spell effect: \"" + text + "\"");
        List<? extends Function<? super PlayAction, ? extends PlayInformation>> effects = textHandler().getEffects(
                text);
        if(effects == null) throw new IllegalArgumentException("Effect could not be parsed");
        
        return effects;
    }
    
    public boolean apply(LineContext context) throws InvalidCardException {
        try {
            context.getContext().getCastInfo().getDelegates().addAll(getEffects(context.getValue()));
            return true;
        } catch(IllegalArgumentException ex) {
            return false;
        }
    }
}
