/**
 * DeckEditorPane.java
 * 
 * Created on 01.09.2010
 */

package net.slightlymagic.laterna.magica.gui.deckEditor;


import static net.slightlymagic.laterna.magica.deck.DeckType.*;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRootPane;

import net.slightlymagic.laterna.magica.LaternaMagica;
import net.slightlymagic.laterna.magica.card.Printing;
import net.slightlymagic.laterna.magica.cards.AllCards;
import net.slightlymagic.laterna.magica.deck.Deck;
import net.slightlymagic.laterna.magica.deck.DeckType;
import net.slightlymagic.laterna.magica.gui.deckEditor.models.pool.CardPoolModel;
import net.slightlymagic.laterna.magica.gui.deckEditor.models.pool.DeckModel;
import net.slightlymagic.laterna.magica.gui.deckEditor.models.pool.PoolModel;
import disbotics.config.configuration.ConfigurationException;


/**
 * The class DeckEditorPane.
 * 
 * @version V0.0 01.09.2010
 * @author Clemens Koza
 */
public class DeckEditorPane extends JRootPane {
    private static final long serialVersionUID = 2798376461471841461L;
    
    public static void main(String[] args) throws IOException, ConfigurationException {
        LaternaMagica.init();
        
        JFrame jf = new JFrame();
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        DeckEditorPane p = new DeckEditorPane();
        jf.add(p);
        
        jf.pack();
        jf.setVisible(true);
    }
    
    private DeckEditorPanel p;
    private JComboBox       upperPool, lowerPool;
    private Deck            upperDeck, lowerDeck;
    
    public DeckEditorPane() {
        setupComponents();
        
        Select l = new Select();
        upperPool.addActionListener(l);
        lowerPool.addActionListener(l);
    }
    
    protected void setupComponents() {
        getContentPane().add(p = new DeckEditorPanel());
        {
            JPanel nav = p.getNav();
            Component add = nav.getComponent(0);
            Component remove = nav.getComponent(1);
            
            nav.removeAll();
            nav.setLayout(new GridLayout(2, 0));
            nav.add(remove);
            nav.add(upperPool = new JComboBox());
            nav.add(add);
            nav.add(lowerPool = new JComboBox());
        }
        
        JMenuBar bar = new JMenuBar();
        setJMenuBar(bar);
        JMenu file = new JMenu("File");
        bar.add(file);
        
        DeckIO io = new DeckIO();
        file.add(new OpenAction(this, io));
        file.add(new SaveAction(this, io));
        file.addSeparator();
        file.add(new NewAction(this));
        file.add(new NewFromPoolAction(this, io));
        file.add(new NewPoolAction(this));
    }
    
    
    /**
     * Convenience method for loading a deck. If the deck has a {@link DeckType#POOL POOL} card pool, it is loaded
     * via {@link #openDeckWithPool(Deck)}. Otherwise, its opened via {@link #openDeck(Collection, Deck)} with
     * {@linkplain AllCards#getPrintings() all printings}.
     */
    public void openDeck(Deck deck) {
        if(deck.getPool(POOL) == null) openDeck(LaternaMagica.CARDS().getPrintings(), deck);
        else openDeckWithPool(deck);
    }
    
    /**
     * Convenience method for loading a pool deck. A pool deck will always be opened with the whole card pool in
     * the top region.
     */
    public void openPool(Deck deck) {
        openDeck(LaternaMagica.CARDS().getPrintings(), deck);
    }
    
    /**
     * Convenience method for loading a deck containing a pool.
     */
    public void openDeckWithPool(Deck deck) {
        setUpperDeck(deck);
        setLowerDeck(deck);
    }
    
    /**
     * Convenience method for loading a deck with a specified unlimited card pool.
     */
    public void openDeck(Collection<Printing> pool, Deck deck) {
        setUpperPool(pool);
        setLowerDeck(deck);
    }
    
    
    public void setUpperDeck(Deck d) {
        upperDeck = d;
        if(d == null) upperPool.setModel(new DefaultComboBoxModel());
        else {
            List<DeckType> pools = new ArrayList<DeckType>();
            for(DeckType t:DeckType.values())
                if(d.getPool(t) != null) pools.add(t);
            upperPool.setModel(new DefaultComboBoxModel(pools.toArray()));
            
            upperPool.setSelectedIndex(-1);
            if(pools.contains(POOL)) upperPool.setSelectedItem(POOL);
        }
    }
    
    public void setUpperPool(Collection<Printing> p) {
        setUpperDeck(null);
        setUpperModel(new PoolModel(null, p));
    }
    
    private void setUpperModel(CardPoolModel m) {
        p.setUpperPool(m);
    }
    
    public Deck getUpperDeck() {
        return upperDeck;
    }
    
    
    public void setLowerDeck(Deck d) {
        lowerDeck = d;
        if(d == null) lowerPool.setModel(new DefaultComboBoxModel());
        else {
            List<DeckType> pools = new ArrayList<DeckType>();
            for(DeckType t:DeckType.values())
                if(d.getPool(t) != null) pools.add(t);
            lowerPool.setModel(new DefaultComboBoxModel(pools.toArray()));
            
            lowerPool.setSelectedIndex(-1);
            if(pools.contains(MAIN_DECK)) lowerPool.setSelectedItem(MAIN_DECK);
            else if(pools.contains(POOL)) lowerPool.setSelectedItem(POOL);
        }
    }
    
    public void setLowerPool(Collection<Printing> p) {
        setLowerDeck(null);
        setLowerModel(new PoolModel(null, p));
    }
    
    private void setLowerModel(CardPoolModel m) {
        p.setLowerPool(m);
    }
    
    public Deck getLowerDeck() {
        return lowerDeck;
    }
    
    
    private class Select implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getSource() == upperPool) {
                if(upperDeck == null) return;
                DeckType t = (DeckType) upperPool.getSelectedItem();
                setUpperModel(t == null? null:new DeckModel(null, upperDeck, t, -1));
            } else if(e.getSource() == lowerPool) {
                if(lowerDeck == null) return;
                DeckType t = (DeckType) lowerPool.getSelectedItem();
                setLowerModel(t == null? null:new DeckModel(null, lowerDeck, t, -1));
            }
        }
    }
}
