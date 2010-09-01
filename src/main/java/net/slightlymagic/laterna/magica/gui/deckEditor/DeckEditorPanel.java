/**
 * DeckEditorPanel.java
 * 
 * Created on 30.07.2010
 */

package net.slightlymagic.laterna.magica.gui.deckEditor;


import static java.lang.String.*;
import static java.util.Arrays.*;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;

import net.slightlymagic.laterna.magica.LaternaMagica;
import net.slightlymagic.laterna.magica.card.Printing;
import net.slightlymagic.laterna.magica.characteristic.CharacteristicSnapshot;
import net.slightlymagic.laterna.magica.gui.card.CardDetail;
import net.slightlymagic.laterna.magica.gui.card.CardImage;
import net.slightlymagic.laterna.magica.gui.card.CardPanel;
import net.slightlymagic.laterna.magica.gui.deckEditor.models.PrintingColumns;
import net.slightlymagic.laterna.magica.gui.deckEditor.models.TemplateColumns;
import net.slightlymagic.laterna.magica.gui.deckEditor.models.pool.CardPoolModel;
import net.slightlymagic.laterna.magica.gui.deckEditor.models.pool.DeckModel;
import net.slightlymagic.laterna.magica.gui.deckEditor.models.pool.PoolModel;
import net.slightlymagic.laterna.magica.gui.deckEditor.models.pool.PrintingComparators;
import net.slightlymagic.laterna.magica.gui.deckEditor.models.templates.CardTemplateModel;
import net.slightlymagic.laterna.magica.mana.ManaSequence;

import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.MultiSplitLayout;
import org.jdesktop.swingx.MultiSplitLayout.Node;


/**
 * The class DeckEditorPanel.
 * 
 * @version V0.0 30.07.2010
 * @author Clemens Koza
 */
public class DeckEditorPanel extends JPanel {
    private static final long serialVersionUID = 3153843403566818756L;
    
    public static void main(String[] args) throws IOException {
        LaternaMagica.init();
        
        JFrame jf = new JFrame();
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        jf.add(new DeckEditorPanel(null, null));
        
        jf.pack();
        jf.setVisible(true);
    }
    
    private JXTable tPool, tDeck;
    private CardTemplateModel mPool, mDeck;
    private CardPanel         detail, image;
    private JComboBox         printings;
    
    private Action            add = new AddAction(), remove = new RemoveAction();
    
    public DeckEditorPanel(CardPoolModel pool, CardPoolModel deck) {
        super(new BorderLayout());
        TemplateColumns c = new TemplateColumns();
        
        if(pool == null) pool = new PoolModel(new PrintingColumns());
        mPool = new CardTemplateModel(c, pool instanceof PoolModel? -1:c.getColumnCount(), pool);
        if(deck == null) deck = new DeckModel(new PrintingColumns(), new HashMap<Printing, Integer>(), -1);
        mDeck = new CardTemplateModel(c, deck instanceof PoolModel? -1:c.getColumnCount(), pool);
        
        setupComponents();
        
        Select l = new Select();
        tPool.getSelectionModel().addListSelectionListener(l);
        tDeck.getSelectionModel().addListSelectionListener(l);
        printings.addActionListener(l);
    }
    
    protected void setupComponents() {
        JXMultiSplitPane overall = new JXMultiSplitPane(new MultiSplitLayout(getOverallLayout()));
        add(overall);
        
        { //pool
            tPool = new JXTable(mPool);
            tPool.setDefaultRenderer(ManaSequence.class, new DefaultTableCellRenderer());
            overall.add(new JScrollPane(tPool), "pool");
        }
        
        { //deck
            JPanel p = new JPanel(new BorderLayout());
            //add some navigation in north
            
            JPanel buttons = new JPanel(new GridLayout(1, 0));
            p.add(buttons, BorderLayout.NORTH);
            buttons.add(new JButton(add));
            buttons.add(new JButton(remove));
            
            tDeck = new JXTable(mDeck);
            tDeck.setDefaultRenderer(ManaSequence.class, new DefaultTableCellRenderer());
            p.add(new JScrollPane(tDeck));
            overall.add(p, "deck");
        }
        
        JPanel p = new JPanel(new BorderLayout());
        image = new CardImage();
        p.add(image);
        p.add(printings = new JComboBox(), BorderLayout.SOUTH);
        printings.setRenderer(new PrintingRenderer());
        mDeck.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if(e.getType() == TableModelEvent.UPDATE) printings.repaint();
            }
        });
        overall.add(p, "picture");
        
        detail = new CardDetail(18);
        overall.add(detail, "detail");
    }
    
    private static Node getOverallLayout() {
        StringWriter sw = new StringWriter();
        PrintWriter w = new PrintWriter(sw);
        w.println("(ROW");
        w.println(" (COLUMN weight=1");
        w.println("  (LEAF name=pool weight=0.5)");
        w.println("  (LEAF name=deck weight=0.5)");
        w.println(" )");
        w.println(" (COLUMN weight=0");
        w.println("  (LEAF name=picture weight=0.5)");
        w.println("  (LEAF name=detail  weight=0.5)");
        w.println(" )");
        w.println(")");
        return MultiSplitLayout.parseModel(sw.toString());
    }
    
    private void updatePrintings(Map<Printing, Integer> printings) {
        if(printings == null) {
            this.printings.setModel(new DefaultComboBoxModel());
            this.printings.setSelectedIndex(-1);
        } else {
            Printing[] p = printings.keySet().toArray(new Printing[printings.keySet().size()]);
            sort(p, PrintingComparators.MULTIVERSE_INSTANCE);
            this.printings.setModel(new DefaultComboBoxModel(p));
            Printing val = null;
            for(Entry<Printing, Integer> e:printings.entrySet())
                if(e.getValue() != null && e.getValue() > 0) {
                    val = e.getKey();
                    break;
                }
            this.printings.setSelectedItem(val);
        }
    }
    
    private void showCard(Printing p) {
        CharacteristicSnapshot sn;
        if(p == null) sn = null;
        else {
            sn = new CharacteristicSnapshot();
            sn.setParts(p.getTemplate().getCardParts().get(0), p);
        }
        image.setCard(sn);
        detail.setCard(sn);
    }
    
    private class Select implements ListSelectionListener, ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(printings.getSelectedIndex() == -1) showCard(null);
            else {
                showCard((Printing) printings.getSelectedItem());
            }
        }
        
        @Override
        public void valueChanged(ListSelectionEvent e) {
            ListSelectionModel sm = (ListSelectionModel) e.getSource();
            JXTable t;
            CardTemplateModel tm;
            if(sm == tPool.getSelectionModel()) {
                t = tPool;
                tm = mPool;
            } else if(sm == tDeck.getSelectionModel()) {
                t = tDeck;
                tm = mDeck;
            } else return;
            
            if(sm.getMinSelectionIndex() == -1) {
                updatePrintings(null);
            } else {
                int index = sm.getLeadSelectionIndex();
                index = t.convertRowIndexToModel(index);
                updatePrintings(tm.getPrintings(tm.getRow(index)));
            }
        }
    }
    
    private class AddAction extends AbstractAction {
        private static final long serialVersionUID = 4503673134555310503L;
        
        public AddAction() {
            super("\u25BC");
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            Printing p = (Printing) printings.getSelectedItem();
            if(p != null && mPool.getPoolModel().getCount(p) > 0) {
                mPool.getPoolModel().remove(p);
                mDeck.getPoolModel().add(p);
            }
        }
    }
    
    private class RemoveAction extends AbstractAction {
        private static final long serialVersionUID = 5916550065818336693L;
        
        public RemoveAction() {
            super("\u25B2");
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            Printing p = (Printing) printings.getSelectedItem();
            if(p != null && mDeck.getPoolModel().getCount(p) > 0) {
                mDeck.getPoolModel().remove(p);
                mPool.getPoolModel().add(p);
            }
        }
    }
    
    private class PrintingRenderer extends DefaultListCellRenderer {
        private static final long serialVersionUID = 2099388301828978204L;
        
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if(value instanceof Printing) {
                Printing p = (Printing) value;
                int count = mDeck.getPoolModel().getCount(p);
                setText(format("%dx %s %s", count, p.getExpansion(), p.getRarity()));
            }
            return this;
        }
    }
}
