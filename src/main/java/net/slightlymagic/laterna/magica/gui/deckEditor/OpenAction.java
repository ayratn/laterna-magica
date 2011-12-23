/**
 * OpenAction.java
 * 
 * Created on 02.09.2010
 */

package net.slightlymagic.laterna.magica.gui.deckEditor;


import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;

import net.slightlymagic.laterna.magica.deck.Deck;


/**
 * The class OpenAction.
 * 
 * @version V0.0 02.09.2010
 * @author Clemens Koza
 */
public class OpenAction extends AbstractAction {
    private static final long serialVersionUID = 6972040349638780836L;
    
    private DeckEditorPane    pane;
    private DeckIO            io;
    
    public OpenAction(DeckEditorPane pane, DeckIO io) {
        super("Open...");
        this.pane = pane;
        this.io = io;
    }
    
    public void actionPerformed(ActionEvent e) {
        File f = io.open();
        Deck d = io.open(f);
        if(d == null) return;
        
        if(f.getName().endsWith(".pool")) pane.openPool(d);
        else pane.openDeck(d);
    }
}
