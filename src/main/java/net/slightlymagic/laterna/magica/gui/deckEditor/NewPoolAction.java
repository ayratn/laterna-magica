/**
 * NewAction.java
 * 
 * Created on 02.09.2010
 */

package net.slightlymagic.laterna.magica.gui.deckEditor;


import static net.slightlymagic.laterna.magica.deck.DeckType.*;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import net.slightlymagic.laterna.magica.LaternaMagica;
import net.slightlymagic.laterna.magica.deck.Deck;


/**
 * The class NewAction.
 * 
 * @version V0.0 02.09.2010
 * @author Clemens Koza
 */
public class NewPoolAction extends AbstractAction {
    private static final long serialVersionUID = 6972040349638780836L;
    
    private DeckEditorPane    pane;
    
    public NewPoolAction(DeckEditorPane pane) {
        super("New card pool");
        this.pane = pane;
    }
    
    public void actionPerformed(ActionEvent e) {
        Deck d = new Deck();
        d.addPool(POOL);
        pane.openDeck(LaternaMagica.CARDS().getPrintings(), d);
    }
}
