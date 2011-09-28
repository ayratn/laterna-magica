/**
 * DeckIO.java
 * 
 * Created on 03.09.2010
 */

package net.slightlymagic.laterna.magica.gui.deckEditor;


import static net.slightlymagic.laterna.magica.LaternaMagica.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JFileChooser;

import net.slightlymagic.laterna.magica.deck.Deck;
import net.slightlymagic.laterna.magica.io.deck.DeckPersister;
import net.slightlymagic.laterna.magica.io.deck.DeckPersisterImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The class DeckIO.
 * 
 * @version V0.0 03.09.2010
 * @author Clemens Koza
 */
public class DeckIO {
    private static final Logger log = LoggerFactory.getLogger(DeckIO.class);
    
    private JFileChooser        c;
    private DeckPersister       p;
    
    public DeckIO() {
        this(new DeckPersisterImpl());
    }
    
    public DeckIO(DeckPersister p) {
        this.p = p;
    }
    
    private JFileChooser getFileChooser() {
        if(c == null) c = new JFileChooser(MAGICA_CONFIG().getDecksFolder());
        return c;
    }
    
    public File open() {
        if(getFileChooser().showOpenDialog(null) != JFileChooser.APPROVE_OPTION) return null;
        return getFileChooser().getSelectedFile();
    }
    
    public File save() {
        if(getFileChooser().showSaveDialog(null) != JFileChooser.APPROVE_OPTION) return null;
        return getFileChooser().getSelectedFile();
    }
    
    public Deck open(File f) {
        if(f == null) return null;
        try {
            FileInputStream is = new FileInputStream(f);
            try {
                return p.readDeck(is);
            } finally {
                try {
                    is.close();
                } catch(IOException ex) {
                    log.warn("While closing", ex);
                }
            }
        } catch(IOException ex) {
            log.error("Could not open deck", ex);
        }
        return null;
    }
    
    public void save(File f, Deck d) {
        if(f == null) return;
        try {
            FileOutputStream os = new FileOutputStream(f);
            try {
                p.writeDeck(d, os);
            } finally {
                try {
                    os.close();
                } catch(IOException ex) {
                    log.warn("While closing", ex);
                }
            }
        } catch(IOException ex) {
            log.error("Could not save deck", ex);
        }
    }
}
