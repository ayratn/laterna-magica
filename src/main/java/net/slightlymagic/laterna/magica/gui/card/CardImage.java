/**
 * CardImage.java
 * 
 * Created on 29.07.2010
 */

package net.slightlymagic.laterna.magica.gui.card;


import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import net.slightlymagic.laterna.magica.characteristic.CardSnapshot;
import net.slightlymagic.laterna.magica.gui.util.ImageCache;


/**
 * The class CardImage.
 * 
 * @version V0.0 29.07.2010
 * @author Clemens Koza
 */
public class CardImage extends CardPanel implements Observer {
    private static final long serialVersionUID = -4234267342253070966L;
    
    private JLabel            l;
    private CardSnapshot      c;
    
    public CardImage() {
        setLayout(new BorderLayout());
        add(l = new JLabel());
        setCard(null);
    }
    
    public void dispose() {
        setCard(null);
    }
    
    public void setCard(CardSnapshot c) {
        if(this.c != null) this.c.deleteObserver(this);
        this.c = c;
        if(this.c != null) this.c.addObserver(this);
        update(c, null);
    }
    
    public CardSnapshot getCard() {
        return c;
    }
    
    public void update(Observable o, Object arg) {
        assert c == o;
        ImageCache ic = ImageCache.getInstance();
        BufferedImage im = null;
        if(c != null) im = ic.getImage(ic.getCardURI(c.getPrinting()));
        //the card back
        if(im == null) im = ic.getImage(ic.getCardURI(0));
        l.setIcon(im == null? null:new ImageIcon(im));
    }
}
