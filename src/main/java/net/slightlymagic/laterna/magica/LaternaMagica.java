/**
 * LaternaMagica.java
 * 
 * Created on 28.10.2009
 */

package net.slightlymagic.laterna.magica;


import java.io.IOException;
import java.net.URL;

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
    
    public static void init() throws IOException {
        URL url = LaternaMagica.class.getResource("/config.properties");
        System.out.println("Configuring from Resource /config.properties");
        System.out.println("Resolved to " + url);
        Configurator c = new Configurator().configure(url).execute();
        
        PROPS = c.getConfigurator(PropertyTreeConfigurator.class).getTree();
        CARDS = new AllCards();
        
        boolean compile = PROPS().getBoolean("/laterna/res/cards/compileOnStart", false);
        if(!compile) {
            try {
                log.info("Loading...");
                CARDS.load();
            } catch(Exception ex) {
                log.error("Error loading compiled cards; cards were not completely loaded.\n"
                        + "LaternaMagica will now try to recreate the cards.\n"
                        + "Below is the reason why loading failed", ex);
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
