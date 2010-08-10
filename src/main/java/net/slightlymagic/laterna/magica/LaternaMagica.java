/**
 * LaternaMagica.java
 * 
 * Created on 28.10.2009
 */

package net.slightlymagic.laterna.magica;


import java.io.IOException;

import net.slightlymagic.laterna.magica.cards.AllCards;
import net.slightlymagic.treeProperties.PropertyTree;
import net.slightlymagic.utils.Configurator;
import net.slightlymagic.utils.PropertyTreeConfigurator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The class LaternaMagica.
 * 
 * @version V0.0 28.10.2009
 * @author Clemens Koza
 */
public class LaternaMagica {
    private static final Logger log = LoggerFactory.getLogger(LaternaMagica.class);
    
    private static PropertyTree PROPS;
    private static AllCards     CARDS;
    
    /**
     * Sets the configuration properties specified in {@link Utils}, and then calls {@link Utils#init()}.
     */
    public static void init() throws IOException {
        Configurator c = new Configurator().configure().execute();
        
        PROPS = c.getConfigurator(PropertyTreeConfigurator.class).getTree();
        CARDS = new AllCards();
        
        boolean compile = PROPS().getBoolean("/net.slightlymagic.laterna/res/cards/compileOnStart", false);
        if(!compile) {
            try {
                log.info("Loading...");
                CARDS.load();
            } catch(Exception ex) {
                log.error("Error loading compiled cards; cards were not completely loaded.\n"
                        + "LaternaMagica will now try to recreate the cards.", ex);
                compile = true;
            }
        }
        if(compile) {
            log.info("Compiling...");
            CARDS.compile();
        }
    }
    
    public static PropertyTree PROPS() {
        return PROPS;
    }
    
    public static AllCards CARDS() {
        return CARDS;
    }
}
