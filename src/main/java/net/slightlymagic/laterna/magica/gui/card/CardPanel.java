/**
 * CardPanel.java
 * 
 * Created on 27.08.2010
 */

package net.slightlymagic.laterna.magica.gui.card;


import javax.swing.JPanel;

import net.slightlymagic.laterna.magica.characteristic.CardSnapshot;

import org.jdesktop.swingx.painter.Painter;


/**
 * The class CardPanel. This class acts as a common superclass for all components that display a card. It adds a
 * {@code Painter<CharacteristicSnapshot>} property, which may but doesn't have to be honored by subclasses.
 * 
 * @version V0.0 27.08.2010
 * @author Clemens Koza
 */
public abstract class CardPanel extends JPanel implements CardDisplay {
    private static final long     serialVersionUID = -8076890635020744259L;
    
    private Painter<CardSnapshot> painter;
    
    public void setPainter(Painter<CardSnapshot> painter) {
        this.painter = painter;
    }
    
    public Painter<CardSnapshot> getPainter() {
        return painter;
    }
}
