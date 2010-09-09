/**
 * TriggerParsers.java
 * 
 * Created on 09.09.2010
 */

package net.slightlymagic.laterna.magica.cards.text;


import static net.slightlymagic.laterna.magica.LaternaMagica.*;

import java.util.ArrayList;
import java.util.List;

import net.slightlymagic.laterna.magica.action.play.TriggerAction;
import net.slightlymagic.treeProperties.TreeProperty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;


/**
 * The class TriggerParsers.
 * 
 * @version V0.0 09.09.2010
 * @author Clemens Koza
 */
public class TriggerParsers {
    private static final Logger              log          = LoggerFactory.getLogger(EffectParsers.class);
    
    private static final String              PARSER_CLASS = "/laterna/res/cards/uncompiled/text/triggers/class";
    private static final List<TriggerParser> triggerParsers;
    
    static {
        triggerParsers = new ArrayList<TriggerParser>();
        
        List<TreeProperty> classes = PROPS().getAllProperty(PARSER_CLASS);
        //loop through each class
        for(TreeProperty pr:classes) {
            //get an object of the compiler class
            String clazz = (String) pr.getValue();
            TriggerParser parser;
            try {
                parser = (TriggerParser) Class.forName(clazz).newInstance();
            } catch(Exception ex) {
                log.warn(clazz + " couldn't be instantiated", ex);
                continue;
            }
            triggerParsers.add(parser);
        }
    }
    
    public static Predicate<? super TriggerAction> getTrigger(String text) {
        for(TriggerParser parser:triggerParsers) {
            Predicate<? super TriggerAction> trigger = parser.parseTrigger(text);
            if(trigger != null) return trigger;
        }
        return null;
    }
    
    public static interface TriggerParser {
        public Predicate<? super TriggerAction> parseTrigger(String text);
    }
}
