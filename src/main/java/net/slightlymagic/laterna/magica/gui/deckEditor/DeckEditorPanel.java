/**
 * DeckEditorPanel.java
 * 
 * Created on 30.07.2010
 */

package net.slightlymagic.laterna.magica.gui.deckEditor;


import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import net.slightlymagic.laterna.magica.LaternaMagica;



/**
 * The class DeckEditorPanel.
 * 
 * @version V0.0 30.07.2010
 * @author Clemens Koza
 */
public class DeckEditorPanel {
    public static void main(String[] args) throws IOException {
        LaternaMagica.init();
        
        JFrame jf = new JFrame();
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        jf.add(new JScrollPane(new JTable(new PoolModel())));
        
        jf.pack();
        jf.setVisible(true);
    }
}
