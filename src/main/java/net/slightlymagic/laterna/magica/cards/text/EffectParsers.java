/**
 * CostParsers.java
 * 
 * Created on 28.04.2010
 */

package net.slightlymagic.laterna.magica.cards.text;


import static net.slightlymagic.laterna.magica.LaternaMagica.*;

import java.util.ArrayList;
import java.util.List;

import net.slightlymagic.laterna.magica.action.play.PlayAction;
import net.slightlymagic.laterna.magica.action.play.PlayInformation;
import net.slightlymagic.treeProperties.TreeProperty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;


/**
 * The class CostParsers.
 * 
 * @version V0.0 28.04.2010
 * @author Clemens Koza
 */
public class EffectParsers {
    private static final Logger             log          = LoggerFactory.getLogger(EffectParsers.class);
    
    private static final String             PARSER_CLASS = "/laterna/res/cards/uncompiled/text/effects/class";
    private static final List<EffectParser> effectParsers;
    
    static {
        effectParsers = new ArrayList<EffectParser>();
        
        List<TreeProperty> classes = PROPS().getAllProperty(PARSER_CLASS);
        //loop through each class
        for(TreeProperty pr:classes) {
            //get an object of the compiler class
            String clazz = (String) pr.getValue();
            EffectParser parser;
            try {
                parser = (EffectParser) Class.forName(clazz).newInstance();
            } catch(Exception ex) {
                log.warn(clazz + " couldn't be instantiated", ex);
                continue;
            }
            effectParsers.add(parser);
        }
    }
    
    public static List<Function<? super PlayAction, ? extends PlayInformation>> getEffects(String text) {
        String[] texts = text.split(",\\s*");
        List<Function<? super PlayAction, ? extends PlayInformation>> result = new ArrayList<Function<? super PlayAction, ? extends PlayInformation>>();
        for(String effect:texts) {
            for(EffectParser parser:effectParsers) {
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
    
    public static boolean isManaEffect(String text) {
        String[] texts = text.split(",\\s*");
        for(String effect:texts)
            for(EffectParser parser:effectParsers)
                //TODO optimize
                if(parser.parseEffect(text) != null && parser.isManaEffect(effect)) return true;
        return false;
    }
    
    public static interface EffectParser {
        public Function<? super PlayAction, ? extends PlayInformation> parseEffect(String text);
        
        /**
         * The result is undefined if the parser can't parse the text
         */
        public boolean isManaEffect(String text);
    }
}
