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
public class NewAction extends AbstractAction {
    private static final long serialVersionUID = 6972040349638780836L;
    
    private DeckEditorPane    pane;
    
    public NewAction(DeckEditorPane pane) {
        super("New Deck");
        this.pane = pane;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Deck d = new DeckImpl();
        d.addPool(DeckType.MAIN_DECK);
        pane.openDeck(d);
    }
}
