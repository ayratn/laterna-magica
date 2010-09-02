/**
 * NewAction.java
 * 
 * Created on 02.09.2010
 */

package net.slightlymagic.laterna.magica.gui.deckEditor;


import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import net.slightlymagic.laterna.magica.LaternaMagica;
import net.slightlymagic.laterna.magica.deck.Deck;
import net.slightlymagic.laterna.magica.deck.Deck.DeckType;
import net.slightlymagic.laterna.magica.deck.impl.DeckImpl;


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
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Deck d = new DeckImpl();
        d.addPool(DeckType.POOL);
        pane.openDeck(LaternaMagica.CARDS().getPrintings(), d);
    }
}
