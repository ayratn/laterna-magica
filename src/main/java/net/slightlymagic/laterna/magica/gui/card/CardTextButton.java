/**
 * CardTextButton.java
 * 
 * Created on 07.04.2010
 */

package net.slightlymagic.laterna.magica.gui.card;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;

import net.slightlymagic.laterna.magica.characteristic.CardSnapshot;


/**
 * The class CardTextButton.
 * 
 * @version V0.0 07.04.2010
 * @author Clemens Koza
 */
public class CardTextButton extends CardPanel {
    private static final long serialVersionUID = 831660638002643394L;
    
    private JButton           b;
    private CardDetail        d;
    
    public CardTextButton() {
        this(null);
    }
    
    public CardTextButton(CardSnapshot c) {
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        setLayout(new BorderLayout());
        add(b = new JButton());
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fireActionPerformed();
            }
        });
        
        b.setBorder(null);
        b.setLayout(new BorderLayout());
        b.add(d = new CardDetail(10));
        
        d.setTappedDimension(80, 60);
        d.setUntappedDimension(60, 80);
        d.setOpaque(false);
        
        setCard(c);
    }
    
    public void dispose() {
        setCard(null);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(getPainter() != null) getPainter().paint((Graphics2D) g, getCard(), getWidth(), getHeight());
    }
    
    protected void fireActionPerformed() {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "");
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for(int i = listeners.length - 2; i >= 0; i -= 2) {
            if(listeners[i] == ActionListener.class) {
                ((ActionListener) listeners[i + 1]).actionPerformed(e);
            }
        }
    }
    
    public void addActionListener(ActionListener l) {
        listenerList.add(ActionListener.class, l);
    }
    
    public void removeActionListener(ActionListener l) {
        listenerList.remove(ActionListener.class, l);
    }
    
    public void setCard(CardSnapshot c) {
        d.setCard(c);
    }
    
    public CardSnapshot getCard() {
        return d.getCard();
    }
}
