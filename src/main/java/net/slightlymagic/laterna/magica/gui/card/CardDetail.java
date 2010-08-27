/**
 * CardDetail.java
 * 
 * Created on 29.07.2010
 */

package net.slightlymagic.laterna.magica.gui.card;


import static java.lang.String.*;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.text.BadLocationException;

import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.ability.Ability;
import net.slightlymagic.laterna.magica.card.CardObject;
import net.slightlymagic.laterna.magica.card.State;
import net.slightlymagic.laterna.magica.card.State.StateType;
import net.slightlymagic.laterna.magica.characteristic.CharacteristicSnapshot;
import net.slightlymagic.laterna.magica.characteristics.CardType;
import net.slightlymagic.laterna.magica.gui.util.SymbolTextCreator;
import net.slightlymagic.laterna.magica.zone.Zone.Zones;


/**
 * The class CardDetail.
 * 
 * @version V0.0 29.07.2010
 * @author Clemens Koza
 */
public class CardDetail extends CardPanel {
    private static final long      serialVersionUID = 831660638002643394L;
    
    private boolean                isTapped;
    private Dimension              untapped;
    private Dimension              tapped;
    
    private CharacteristicSnapshot c;
    private CardListener           l                = new CardListener();
    
    private float                  textSize         = 16;
    
    protected JLabel               name;
    protected JTextPane            mana;
    protected JLabel               types;
    protected JTextPane            text;
    protected JLabel               pt;
    
    public CardDetail() {
        this(new CharacteristicSnapshot());
    }
    
    public CardDetail(float textSize) {
        this(new CharacteristicSnapshot(), textSize);
    }
    
    public CardDetail(CharacteristicSnapshot c) {
        this(c, 14);
    }
    
    public CardDetail(CharacteristicSnapshot c, float textSize) {
        this.textSize = textSize;
        setTappedDimension(300, 200);
        setUntappedDimension(300, 200);
        setupComponents();
        setCard(c);
    }
    
    public void setUntappedDimension(int width, int height) {
        untapped = new Dimension(width, height);
        setTapped(isTapped);
    }
    
    public void setTappedDimension(int width, int height) {
        tapped = new Dimension(width, height);
        setTapped(isTapped);
    }
    
    public void setTapped(boolean isTapped) {
        this.isTapped = isTapped;
        Dimension d = isTapped? tapped:untapped;
        setMinimumSize(d);
        setPreferredSize(d);
        setMaximumSize(d);
        invalidate();
    }
    
    protected void setupComponents() {
        name = new JLabel();
        mana = new JTextPane();
        types = new JLabel();
        text = new JTextPane();
        pt = new JLabel();
        
        Font f = getFont();
        f = f.deriveFont(textSize);
        
        text.setBorder(null);
        mana.setEditable(false);
        
        for(JComponent l:new JComponent[] {name, mana, types, text, pt}) {
            l.setOpaque(false);
            l.setFont(f);
        }
        pt.setHorizontalAlignment(SwingConstants.TRAILING);
        
        setTapped(false);
        setLayout(new BorderLayout());
        
        JPanel name = new JPanel(new BorderLayout());
        name.add(this.name);
        name.add(mana, BorderLayout.EAST);
        
        JPanel north = new JPanel(new GridLayout(0, 1));
        north.add(name);
        north.add(types);
        
        JPanel south = new JPanel(new GridLayout(0, 1));
        south.add(pt);
        
        for(JComponent l:new JComponent[] {name, north, south})
            l.setOpaque(false);
        
        add(north, BorderLayout.NORTH);
        add(new JScrollPane(text));
        add(south, BorderLayout.SOUTH);
    }
    
    public void setCard(CharacteristicSnapshot c) {
        if(getCard() != null) {
            getCard().deleteObserver(l);
            if(getCard().getCard() instanceof CardObject && ((CardObject) getCard().getCard()).getState() != null) {
                State s = ((CardObject) getCard().getCard()).getState();
                s.removePropertyChangeListener(StateType.TAPPED.name(), l);
            }
        }
        this.c = c;
        if(getCard() != null) {
            getCard().addObserver(l);
            l.update(getCard(), null);
            if(getCard().getCard() instanceof CardObject && ((CardObject) getCard().getCard()).getState() != null) {
                State s = ((CardObject) getCard().getCard()).getState();
                s.addPropertyChangeListener(StateType.TAPPED.name(), l);
            }
        }
    }
    
    public CharacteristicSnapshot getCard() {
        return c;
    }
    
    private class CardListener implements PropertyChangeListener, Observer {
        public void propertyChange(PropertyChangeEvent evt) {
            update(getCard(), null);
        }
        
        public void update(Observable o, Object arg) {
            assert o == getCard();
            
            name.setText(getCard().getName());
            mana.setText("");
            SymbolTextCreator.formatRulesText(valueOf(getCard().getManaCost()), mana, (int) textSize);
            
            text.setText("");
            for(Iterator<Ability> it = getCard().getAbilities().getValues().iterator(); it.hasNext();) {
                Ability ab = it.next();
                SymbolTextCreator.formatRulesText(ab.toString(), text, (int) textSize);
                if(it.hasNext()) try {
                    text.getDocument().insertString(text.getDocument().getLength(), "\n", null);
                } catch(BadLocationException ex) {
                    throw new AssertionError(ex);
                }
            }
            
            String superTypes = getCard().getSuperTypes().valueString();
            String types = getCard().getTypes().valueString();
            String subTypes = getCard().getSubTypes().valueString();
            
            String typeString = (superTypes.length() == 0? "":superTypes + " ") + types
                    + (subTypes.length() == 0? "":" - " + subTypes);
            
            CardDetail.this.types.setText(typeString);
            
            if(getCard().getTypes().hasValue(CardType.CREATURE)) {
                pt.setText(getCard().getPower() + "/" + getCard().getToughness());
            } else if(getCard().getTypes().hasValue(CardType.PLANESWALKER)) {
                pt.setText("" + getCard().getLoyalty());
            } else pt.setText("");
            

            MagicObject card = getCard().getCard();
            if(card instanceof CardObject && card.getZone().getType() == Zones.BATTLEFIELD) {
                setTapped(((CardObject) card).getState().getState(StateType.TAPPED));
            }
        }
    }
}
