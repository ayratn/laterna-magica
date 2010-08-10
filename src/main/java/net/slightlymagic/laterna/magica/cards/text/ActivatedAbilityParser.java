/**
 * ActivatedAbilityParser.java
 * 
 * Created on 25.04.2010
 */

package net.slightlymagic.laterna.magica.cards.text;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.slightlymagic.laterna.magica.ability.ActivatedAbility;
import net.slightlymagic.laterna.magica.ability.impl.ActivatedAbilityImpl;
import net.slightlymagic.laterna.magica.action.play.ActivateAction;
import net.slightlymagic.laterna.magica.action.play.PlayInformation;
import net.slightlymagic.laterna.magica.action.play.impl.PlayInformationFunction;
import net.slightlymagic.laterna.magica.cards.InvalidCardException;
import net.slightlymagic.laterna.magica.cards.text.TextCardCompiler.LineContext;
import net.slightlymagic.laterna.magica.cards.text.TextHandler.AbilityParser;
import net.slightlymagic.laterna.magica.util.MagicaUtils;
import net.slightlymagic.laterna.magica.zone.Zone.Zones;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Predicate;


/**
 * The class ActivatedAbilityParser.
 * 
 * @version V0.0 25.04.2010
 * @author Clemens Koza
 */
public class ActivatedAbilityParser implements AbilityParser {
    private static final Logger                    log   = LoggerFactory.getLogger(ActivatedAbilityParser.class);
    
    private static final Pattern                   p     = Pattern.compile("(.*?):\\s*(.*)");
    private static final Predicate<ActivateAction> legal = new Legal();
    
    public ActivatedAbility getAbility(String text) {
        Matcher m = p.matcher(text);
        if(!m.matches()) throw new IllegalArgumentException("Not an activated ability");
        
        log.debug("Found activated ability: \"" + text + "\"");
        List<? extends Function<? super ActivateAction, ? extends PlayInformation>> costs = CostParsers.getCosts(m.group(1));
        if(costs == null) throw new IllegalArgumentException("Cost could not be parsed");
        List<? extends Function<? super ActivateAction, ? extends PlayInformation>> effects = EffectParsers.getEffects(m.group(2));
        if(effects == null) throw new IllegalArgumentException("Effect could not be parsed");
        boolean manaAbility = EffectParsers.isManaEffect(m.group(2));
        
        List<Function<? super ActivateAction, ? extends PlayInformation>> all = new ArrayList<Function<? super ActivateAction, ? extends PlayInformation>>();
        all.addAll(costs);
        all.addAll(effects);
        return new ActivatedAbilityImpl(manaAbility, new PlayInformationFunction<ActivateAction>(all), legal, text);
    }
    
    
    public boolean apply(LineContext context) throws InvalidCardException {
        try {
            context.getContext().getPart().getAbilities().add(getAbility(context.getValue()));
            return true;
        } catch(IllegalArgumentException ex) {
            return false;
        }
    }
    
    private static final class Legal implements Predicate<ActivateAction>, Serializable {
        private static final long serialVersionUID = -1750538944000847487L;
        
        
        public boolean apply(ActivateAction input) {
            //the card must be on the battlefield
            if(input.getObject().getObject().getZone().getType() != Zones.BATTLEFIELD) return false;
            //the player must have priority
            if(!MagicaUtils.canPlayInstant(input.getController())) return false;
            //the card must be controlled by the player
            if(input.getObject().getObject().getController() != input.getController()) return false;
            return true;
        }
    }
}
