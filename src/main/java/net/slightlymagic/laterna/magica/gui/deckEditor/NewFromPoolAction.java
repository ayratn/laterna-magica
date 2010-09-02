/**
 * NewAction.java
 * 
 * Created on 02.09.2010
 */

package net.slightlymagic.laterna.magica.gui.deckEditor;


import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import net.slightlymagic.laterna.magica.LaternaMagica;
import net.slightlymagic.laterna.magica.deck.Deck;
import net.slightlymagic.laterna.magica.deck.Deck.DeckType;
import net.slightlymagic.laterna.magica.deck.impl.DeckImpl;
import net.slightlymagic.laterna.magica.io.deck.DeckPersister;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The class NewAction.
 * 
 * @version V0.0 02.09.2010
 * @author Clemens Koza
 */
public class NewFromPoolAction extends AbstractAction {
    private static final long   serialVersionUID = 6972040349638780836L;
    
    private static final Logger log              = LoggerFactory.getLogger(OpenAction.class);
    
    private DeckEditorPane      pane;
    private DeckPersister       p;
    private JFileChooser        c;
    
    public NewFromPoolAction(DeckEditorPane pane, DeckPersister p) {
        this(pane, p, null);
    }
    
    public NewFromPoolAction(DeckEditorPane pane, DeckPersister p, JFileChooser c) {
        super("New deck from card pool...");
        this.pane = pane;
        this.p = p;
        this.c = c;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if(c == null) c = new JFileChooser(LaternaMagica.PROPS().getFile("/laterna/usr/decks"));
        if(c.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) return;
        File f = c.getSelectedFile();
        
        try {
            Deck pool = p.readDeck(new FileInputStream(f));
            
            Deck d = new DeckImpl();
            d.addPool(DeckType.MAIN_DECK);
            for(DeckType t:DeckType.values())
                if(pool.getPool(t) != null) {
                    d.addPool(t);
                    d.getPool(t).putAll(pool.getPool(t));
                }
            pane.openDeck(d);
        } catch(IOException ex) {
            log.error("Could not open pool", ex);
        }
    }
}
