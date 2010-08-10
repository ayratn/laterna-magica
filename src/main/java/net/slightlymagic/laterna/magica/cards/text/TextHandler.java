/**
 * TextHandler.java
 * 
 * Created on 24.04.2010
 */

package net.slightlymagic.laterna.magica.cards.text;


import static net.slightlymagic.laterna.magica.LaternaMagica.*;

import java.util.ArrayList;
import java.util.List;

import net.slightlymagic.laterna.magica.cards.InvalidCardException;
import net.slightlymagic.laterna.magica.cards.text.TextCardCompiler.LineContext;
import net.slightlymagic.laterna.magica.cards.text.TextCardCompiler.LineHandler;
import net.slightlymagic.treeProperties.TreeProperty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TextHandler implements LineHandler {
    private static final Logger              log          = LoggerFactory.getLogger(TextHandler.class);
    
    private static final String              PARSER_CLASS = "/laterna/res/cards/uncompiled/text/abilities/class";
    private static final List<AbilityParser> abilityParsers;
    
    static {
        abilityParsers = new ArrayList<AbilityParser>();
        
        List<TreeProperty> classes = PROPS().getAllProperty(PARSER_CLASS);
        //loop through each class
        for(TreeProperty pr:classes) {
            //get an object of the compiler class
            String clazz = (String) pr.getValue();
            AbilityParser parser;
            try {
                parser = (AbilityParser) Class.forName(clazz).newInstance();
            } catch(Exception ex) {
                log.warn(clazz + " couldn't be instantiated", ex);
                continue;
            }
            abilityParsers.add(parser);
        }
    }
    
    public String getKey() {
        return "text";
    }
    
    public void apply(LineContext from) throws InvalidCardException {
        for(AbilityParser parser:abilityParsers)
            if(parser.apply(from)) return;
        from.getContext().warn(
                "\"" + from.getContext().getPart() + "\": \"" + from.getValue()
                        + "\" could not be parsed as an ability");
    }
    
    public static interface AbilityParser {
        public boolean apply(LineContext context) throws InvalidCardException;
    }
}
