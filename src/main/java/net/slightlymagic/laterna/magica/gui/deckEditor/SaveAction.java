/**
 * SaveAction.java
 * 
 * Created on 02.09.2010
 */

package net.slightlymagic.laterna.magica.gui.deckEditor;


import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;


/**
 * The class SaveAction.
 * 
 * @version V0.0 02.09.2010
 * @author Clemens Koza
 */
public class SaveAction extends AbstractAction {
    private static final long serialVersionUID = -47046556411079128L;
    
    private DeckEditorPane    pane;
    private DeckIO            io;
    
    public SaveAction(DeckEditorPane pane, DeckIO io) {
        super("Save As...");
        this.pane = pane;
        this.io = io;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        io.save(io.save(), pane.getLowerDeck());
    }
}
