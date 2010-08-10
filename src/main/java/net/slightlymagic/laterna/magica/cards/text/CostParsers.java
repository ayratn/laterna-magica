/**
 * CostParsers.java
 * 
 * Created on 28.04.2010
 */

package net.slightlymagic.laterna.magica.cards.text;


import static net.slightlymagic.laterna.magica.LaternaMagica.*;

import java.util.ArrayList;
import java.util.List;

import net.slightlymagic.laterna.magica.action.play.ActivateAction;
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
public class CostParsers {
    private static final Logger           log          = LoggerFactory.getLogger(CostParsers.class);
    
    private static final String           PARSER_CLASS = "/net.slightlymagic.laterna/res/cards/uncompiled/text/costs/class";
    private static final List<CostParser> costParsers;
    
    static {
        costParsers = new ArrayList<CostParser>();
        
        List<TreeProperty> classes = PROPS().getAllProperty(PARSER_CLASS);
        //loop through each class
        for(TreeProperty pr:classes) {
            //get an object of the compiler class
            String clazz = (String) pr.getValue();
            CostParser parser;
            try {
                parser = (CostParser) Class.forName(clazz).newInstance();
            } catch(Exception ex) {
                log.warn(clazz + " couldn't be instantiated", ex);
                continue;
            }
            costParsers.add(parser);
        }
    }
    
    public static List<Function<? super ActivateAction, ? extends PlayInformation>> getCosts(String text) {
        String[] texts = text.split(",\\s*");
        List<Function<? super ActivateAction, ? extends PlayInformation>> result = new ArrayList<Function<? super ActivateAction, ? extends PlayInformation>>();
        for(String cost:texts) {
            for(CostParser parser:costParsers) {
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
    
    public interface CostParser {
        public Function<? super ActivateAction, ? extends PlayInformation> parseCost(String text);
    }
}
