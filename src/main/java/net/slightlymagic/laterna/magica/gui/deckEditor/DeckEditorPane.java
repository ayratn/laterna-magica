/**
 * DeckEditorPane.java
 * 
 * Created on 01.09.2010
 */

package net.slightlymagic.laterna.magica.gui.deckEditor;


import static net.slightlymagic.laterna.magica.deck.Deck.DeckType.*;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;

import net.slightlymagic.laterna.magica.LaternaMagica;
import net.slightlymagic.laterna.magica.card.Printing;
import net.slightlymagic.laterna.magica.deck.Deck;
import net.slightlymagic.laterna.magica.deck.Deck.DeckType;
import net.slightlymagic.laterna.magica.deck.impl.DeckImpl;
import net.slightlymagic.laterna.magica.gui.deckEditor.models.pool.CardPoolModel;
import net.slightlymagic.laterna.magica.gui.deckEditor.models.pool.DeckModel;
import net.slightlymagic.laterna.magica.gui.deckEditor.models.pool.PoolModel;


/**
 * The class DeckEditorPane.
 * 
 * @version V0.0 01.09.2010
 * @author Clemens Koza
 */
public class DeckEditorPane extends JRootPane {
    private static final long serialVersionUID = 2798376461471841461L;
    
    public static void main(String[] args) throws IOException {
        LaternaMagica.init();
        
        JFrame jf = new JFrame();
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        Deck d = new DeckImpl();
        d.addPool(POOL);
        d.addPool(MAIN_DECK);
        put(d, POOL, "Forest", 20);
        put(d, POOL, "Llanowar Elves", 4);
        put(d, POOL, "Grizzly Bears", 4);
        put(d, POOL, "Trained Armodon", 4);
        put(d, POOL, "Enormous Baloth", 4);
        
        DeckEditorPane p = new DeckEditorPane();
        p.setUpperDeck(d);
        p.setLowerDeck(d);
        jf.add(p);
        
        jf.pack();
        jf.setVisible(true);
    }
    
    private static void put(Deck d, DeckType t, String name, int num) {
        Map<Printing, Integer> pool = d.getPool(t);
        List<Printing> list = LaternaMagica.CARDS().getCard(name).getPrintings();
        for(int i = 0; i < num; i++)
            put(pool, list);
    }
    
    /**
     * Randomly adds one of the printings to the pool
     */
    private static void put(Map<Printing, Integer> pool, List<Printing> list) {
        Printing p = list.get((int) (Math.random() * list.size()));
        Integer old = pool.put(p, 1);
        //If there already were cards, add 1
        if(old != null) pool.put(p, old + 1);
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
        }
    }
    
    public void setLowerPool(Collection<Printing> p) {
        setLowerDeck(null);
        setLowerModel(new PoolModel(null, p));
    }
    
    private void setLowerModel(CardPoolModel m) {
        p.setLowerPool(m);
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
