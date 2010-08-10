/**
 * CardTextButton.java
 * 
 * Created on 07.04.2010
 */

package net.slightlymagic.laterna.magica.gui.card;


import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JButton;

import net.slightlymagic.laterna.magica.characteristic.CharacteristicSnapshot;


/**
 * The class CardTextButton.
 * 
 * @version V0.0 07.04.2010
 * @author Clemens Koza
 */
public class CardTextButton extends JButton implements CardDisplay {
    private static final long serialVersionUID = 831660638002643394L;
    
    private CardDetail        d;
    
    public CardTextButton() {
        this(new CharacteristicSnapshot());
    }
    
    public CardTextButton(CharacteristicSnapshot c) {
        d = new CardDetail(10);
        d.setTappedDimension(80, 60);
        d.setUntappedDimension(60, 80);
        d.invalidate();
        d.setOpaque(false);
        
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        setLayout(new BorderLayout());
        add(d);
        
        setCard(c);
    }
    
    public void setCard(CharacteristicSnapshot c) {
        d.setCard(c);
    }
    
    public CharacteristicSnapshot getCard() {
        return d.getCard();
    }
}
