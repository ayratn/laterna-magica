/**
 * TestSymbolTextCreator.java
 * 
 * Created on 11.04.2010
 */

package net.slightlymagic.laterna.magica.test;


import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.text.StyledDocument;

import net.slightlymagic.laterna.magica.LaternaMagica;
import net.slightlymagic.laterna.magica.gui.util.SymbolTextCreator;

import org.junit.Ignore;


/**
 * The class TestSymbolTextCreator.
 * 
 * @version V0.0 11.04.2010
 * @author Clemens Koza
 */
@Ignore
public class TestSymbolTextCreator {
    public static void main(String[] args) throws Exception {
        LaternaMagica.init();
        
        JTextPane p = new JTextPane();
        StyledDocument document = p.getStyledDocument();
        
        SymbolTextCreator.formatRulesText(
                "(Dryad Arbor isn't a spell, it's affected by summoning sickness, and it has \"{T}: Add {G} to your mana pool.\")",
                p, 10);
        document.insertString(document.getLength(), "\n", null);
        SymbolTextCreator.formatRulesText("Dryad Arbor is green.", p, 10);
        document.insertString(document.getLength(), "\n", null);
        SymbolTextCreator.formatFlavorText(
                "\"Touch no tree, break no branch, and speak only the question you wish answered.\"\n-Von Yomm, elder druid, to her initiates",
                p, 10);
        
        JFrame jf = new JFrame();
        jf.setLayout(new BoxLayout(jf.getContentPane(), BoxLayout.Y_AXIS));
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        jf.add(p);
        
        jf.pack();
        jf.setVisible(true);
    }
}
