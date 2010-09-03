/**
 * NewAction.java
 * 
 * Created on 02.09.2010
 */

package net.slightlymagic.laterna.magica.gui.deckEditor;


import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import net.slightlymagic.laterna.magica.deck.Deck;
import net.slightlymagic.laterna.magica.deck.Deck.DeckType;
import net.slightlymagic.laterna.magica.deck.impl.DeckImpl;


/**
 * The class NewAction.
 * 
 * @version V0.0 02.09.2010
 * @author Clemens Koza
 */
public class NewFromPoolAction extends AbstractAction {
    private static final long serialVersionUID = 6972040349638780836L;
    
    private DeckEditorPane    pane;
    private DeckIO            io;
    
    public NewFromPoolAction(DeckEditorPane pane, DeckIO io) {
        super("New deck from card pool...");
        this.pane = pane;
        this.io = io;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Deck pool = io.open(io.open());
        if(pool == null) return;
        
        Deck d = new DeckImpl();
        d.addPool(DeckType.MAIN_DECK);
        for(DeckType t:DeckType.values())
            if(pool.getPool(t) != null) {
                d.addPool(t);
                d.getPool(t).putAll(pool.getPool(t));
            }
        pane.openDeck(d);
    }
}
