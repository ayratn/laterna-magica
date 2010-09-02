/**
 * SaveAction.java
 * 
 * Created on 02.09.2010
 */

package net.slightlymagic.laterna.magica.gui.deckEditor;


import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import net.slightlymagic.laterna.magica.LaternaMagica;
import net.slightlymagic.laterna.magica.io.deck.DeckPersister;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The class SaveAction.
 * 
 * @version V0.0 02.09.2010
 * @author Clemens Koza
 */
public class SaveAction extends AbstractAction {
    private static final long   serialVersionUID = -47046556411079128L;
    
    private static final Logger log              = LoggerFactory.getLogger(SaveAction.class);
    
    private DeckEditorPane      pane;
    private DeckPersister       p;
    private JFileChooser        c;
    
    public SaveAction(DeckEditorPane pane, DeckPersister p) {
        this(pane, p, null);
    }
    
    public SaveAction(DeckEditorPane pane, DeckPersister p, JFileChooser c) {
        super("Save As...");
        this.pane = pane;
        this.p = p;
        this.c = c;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if(c == null) c = new JFileChooser(LaternaMagica.PROPS().getFile("/laterna/usr/decks"));
        if(c.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) return;
        File f = c.getSelectedFile();
        
        try {
            p.writeDeck(pane.getLowerDeck(), new FileOutputStream(f));
        } catch(IOException ex) {
            log.error("Could not open deck", ex);
        }
    }
}
