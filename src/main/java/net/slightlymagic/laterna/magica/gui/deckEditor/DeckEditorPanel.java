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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;

import net.slightlymagic.laterna.magica.card.CardTemplate;
import net.slightlymagic.laterna.magica.card.Printing;
import net.slightlymagic.laterna.magica.characteristic.CharacteristicSnapshot;
import net.slightlymagic.laterna.magica.gui.card.CardDetail;
import net.slightlymagic.laterna.magica.gui.card.CardImage;
import net.slightlymagic.laterna.magica.gui.card.CardPanel;
import net.slightlymagic.laterna.magica.gui.deckEditor.models.TemplateColumns;
import net.slightlymagic.laterna.magica.gui.deckEditor.models.pool.CardPoolModel;
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
    
    private JXTable           tUpper, tLower;
    private CardTemplateModel mUpper, mLower;
    private CardPanel         detail, image;
    private JComboBox         printings;
    
    private JPanel            nav;
    
    private Action            add              = new AddAction(), remove = new RemoveAction();
    
    public DeckEditorPanel() {
        super(new BorderLayout());
        
        TemplateColumns c = new TemplateColumns();
        mUpper = new CardTemplateModel(c);
        mLower = new CardTemplateModel(c);
        
        setupComponents();
        
        Select l = new Select();
        tUpper.getSelectionModel().addListSelectionListener(l);
        tLower.getSelectionModel().addListSelectionListener(l);
        printings.addActionListener(l);
    }
    
    public void setUpperPool(CardPoolModel m) {
        mUpper.setPoolModel(m);
        TemplateColumns c = new TemplateColumns();
        mUpper.setColumns(c, m instanceof PoolModel? -1:c.getColumnCount());
    }
    
    public void setLowerPool(CardPoolModel m) {
        mLower.setPoolModel(m);
        TemplateColumns c = new TemplateColumns();
        mLower.setColumns(c, m instanceof PoolModel? -1:c.getColumnCount());
    }
    
    public JPanel getNav() {
        return nav;
    }
    
    protected void setupComponents() {
        JXMultiSplitPane overall = new JXMultiSplitPane(new MultiSplitLayout(getOverallLayout()));
        add(overall);
        
        { //pool
            tUpper = new JXTable(mUpper);
            tUpper.setDefaultRenderer(ManaSequence.class, new DefaultTableCellRenderer());
            overall.add(new JScrollPane(tUpper), "pool");
        }
        
        { //deck
            JPanel p = new JPanel(new BorderLayout());
            
            nav = new JPanel(new GridLayout(1, 0));
            p.add(nav, BorderLayout.NORTH);
            nav.add(new JButton(add));
            nav.add(new JButton(remove));
            
            tLower = new JXTable(mLower);
            tLower.setDefaultRenderer(ManaSequence.class, new DefaultTableCellRenderer());
            p.add(new JScrollPane(tLower));
            overall.add(p, "deck");
        }
        
        JPanel p = new JPanel(new BorderLayout());
        image = new CardImage();
        p.add(image);
        p.add(printings = new JComboBox(), BorderLayout.SOUTH);
        printings.setRenderer(new PrintingRenderer());
        mLower.addTableModelListener(new TableModelListener() {
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
    
    private void updatePrintings(CardTemplate t, CardTemplateModel source) {
        if(t == null) {
            printings.setModel(new DefaultComboBoxModel());
            printings.setSelectedIndex(-1);
        } else {
            Set<Printing> set = new HashSet<Printing>();
            set.addAll(mUpper.getPrintings(t).keySet());
            set.addAll(mLower.getPrintings(t).keySet());
            
            Printing[] p = set.toArray(new Printing[set.size()]);
            sort(p, PrintingComparators.MULTIVERSE_INSTANCE);
            
            printings.setModel(new DefaultComboBoxModel(p));
            
            Printing val = null;
            for(Printing pr:p)
                if(source.getPoolModel().getCount(pr) > 0) {
                    val = pr;
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
            if(sm == tUpper.getSelectionModel()) {
                t = tUpper;
                tm = mUpper;
            } else if(sm == tLower.getSelectionModel()) {
                t = tLower;
                tm = mLower;
            } else return;
            
            if(sm.getMinSelectionIndex() == -1) {
                updatePrintings(null, null);
            } else {
                int index = sm.getLeadSelectionIndex();
                index = t.convertRowIndexToModel(index);
                updatePrintings(tm.getRow(index), tm);
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
            if(p != null && mUpper.getPoolModel().getCount(p) > 0) {
                mUpper.getPoolModel().remove(p);
                mLower.getPoolModel().add(p);
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
            if(p != null && mLower.getPoolModel().getCount(p) > 0) {
                mLower.getPoolModel().remove(p);
                mUpper.getPoolModel().add(p);
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
                int cPool = mUpper.getPoolModel() == null? 0:mUpper.getPoolModel().getCount(p);
                int cDeck = mLower.getPoolModel() == null? 0:mLower.getPoolModel().getCount(p);
                boolean pPool = mUpper.getPoolModel() instanceof PoolModel;
                boolean pDeck = mLower.getPoolModel() instanceof PoolModel;
                
                setText(format("%s/%s %s %s", pPool && cPool > 0? "-":cPool, pDeck && cDeck > 0? "-":cDeck,
                        p.getExpansion(), p.getRarity()));
            }
            return this;
        }
    }
}
