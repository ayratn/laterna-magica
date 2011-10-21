/**
 * LaternaMagica.java
 * 
 * Created on 28.10.2009
 */

package net.slightlymagic.laterna.magica;


import java.awt.Dimension;

import javax.swing.JFrame;

import net.slightlymagic.laterna.magica.cards.AllCards;
import net.slightlymagic.laterna.magica.config.MagicaConfig;
import net.slightlymagic.laterna.magica.gui.main.MainPane;


/**
 * The class LaternaMagica.
 * 
 * @version V0.0 28.10.2009
 * @author Clemens Koza
 */
public class LaternaMagica {
    private static MagicaConfig MAGICA_CONFIG;
    private static AllCards     CARDS;
    
    public static void main(String[] args) throws Exception {
        LaternaInit.init();
        
        JFrame jf = new JFrame();
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setTitle("Laterna Magica");
        
        jf.add(new MainPane());
        
        jf.pack();
        Dimension screen = jf.getToolkit().getScreenSize();
        Dimension window = jf.getSize();
        jf.setLocation((screen.width - window.width) / 2, (screen.height - window.height) / 2);
        
        jf.setVisible(true);
    }
    
    
    public static void init() throws Exception {
        MAGICA_CONFIG = LaternaTools.CONFIG().getConfig("magica");
        CARDS = CardFormats.getAllCards();
    }
    
    public static MagicaConfig MAGICA_CONFIG() {
        return MAGICA_CONFIG;
    }
    
    public static AllCards CARDS() {
        return CARDS;
    }
}
