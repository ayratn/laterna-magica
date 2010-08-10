/**
 * CardImage.java
 * 
 * Created on 29.07.2010
 */

package net.slightlymagic.laterna.magica.gui.card;


import java.awt.image.BufferedImage;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import net.slightlymagic.laterna.magica.characteristic.CharacteristicSnapshot;
import net.slightlymagic.laterna.magica.gui.util.ImageCache;



/**
 * The class CardImage.
 * 
 * @version V0.0 29.07.2010
 * @author Clemens Koza
 */
public class CardImage extends JLabel implements CardDisplay, Observer {
    private static final long      serialVersionUID = -4234267342253070966L;
    
    private CharacteristicSnapshot c;
    
    public CardImage() {
        setCard(null);
    }
    
    public void setCard(CharacteristicSnapshot c) {
        if(this.c != null) this.c.deleteObserver(this);
        this.c = c;
        if(this.c != null) this.c.addObserver(this);
        update(c, null);
    }
    
    public CharacteristicSnapshot getCard() {
        return c;
    }
    
    public void update(Observable o, Object arg) {
        assert c == o;
        BufferedImage im = null;
        if(c != null) im = ImageCache.getInstance().getCard(c.getPrinting());
        //the card back
        if(im == null) im = ImageCache.getInstance().getCard(0);
        setIcon(im == null? null:new ImageIcon(im));
    }
}
