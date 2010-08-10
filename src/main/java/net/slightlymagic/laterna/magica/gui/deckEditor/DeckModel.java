/**
 * DeckModel.java
 * 
 * Created on 30.07.2010
 */

package net.slightlymagic.laterna.magica.gui.deckEditor;


import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import net.slightlymagic.laterna.magica.card.CardParts;
import net.slightlymagic.laterna.magica.card.Printing;
import net.slightlymagic.laterna.magica.characteristics.MagicColor;
import net.slightlymagic.laterna.magica.deck.Deck;
import net.slightlymagic.laterna.magica.deck.Deck.DeckType;



/**
 * The class DeckModel. A deck model contains a specific number of every printing.
 * 
 * @version V0.0 30.07.2010
 * @author Clemens Koza
 */
public class DeckModel extends AbstractTableModel implements CardPoolModel {
    private static final long                serialVersionUID = 389522808287349833L;
    
    public static final Comparator<Printing> c                = new PrintingComparator();
    private static final int                 COUNT_COLUMN     = 1;
    private static final Class<?>[]          classes          = {String.class, Integer.class};
    private static final String[]            names            = {"Name", "Count"};
    
    private List<Printing>                   keys;
    private Map<Printing, Integer>           pool;
    
    public DeckModel(Deck d, DeckType t) {
        this(d.getPool(t));
    }
    
    public DeckModel(Map<Printing, Integer> pool) {
        this.pool = pool;
        refreshKeys();
    }
    
    public int getColumnCount() {
        return names.length;
    }
    
    @Override
    public Class<?> getColumnClass(int column) {
        return classes[column];
    }
    
    @Override
    public String getColumnName(int column) {
        return names[column];
    }
    
    public int getRowCount() {
        return keys.size();
    }
    
    public Object getValueAt(int row, int column) {
        Printing p = keys.get(row);
        switch(column) {
            case 0:
                return p.getTemplate().toString();
            case 1:
                return pool.get(p);
            default:
                throw new AssertionError();
        }
    }
    
    private void refreshKeys() {
        keys.clear();
        keys.addAll(pool.keySet());
        Collections.sort(keys, c);
        fireTableDataChanged();
    }
    
    public void add(Printing p) {
        Integer count = pool.put(p, 1);
        int index = Collections.binarySearch(keys, p, c);
        if(count == null) {
            //transform to the insertion
            index = -index - 1;
            keys.add(index, p);
            fireTableRowsInserted(index, index);
        } else {
            pool.put(p, count.intValue() + 1);
            fireTableCellUpdated(index, COUNT_COLUMN);
        }
    }
    
    public void remove(Printing p) {
        Integer count = pool.remove(p);
        if(count == null) return;
        int index = Collections.binarySearch(keys, p, c);
        if(count.intValue() == 1) {
            keys.remove(index);
            fireTableRowsDeleted(index, index);
        } else {
            pool.put(p, count.intValue() - 1);
            fireTableCellUpdated(index, COUNT_COLUMN);
        }
    }
    
    private static final class PrintingComparator implements Comparator<Printing> {
        public int compare(Printing o1, Printing o2) {
            int i = getColor(o1) - getColor(o2);
            if(i != 0) return i;
            return o1.getTemplate().toString().compareTo(o2.getTemplate().toString());
        }
        
        private int getColor(Printing pr) {
            Set<MagicColor> set = new HashSet<MagicColor>();
            for(CardParts p:pr.getTemplate().getCardParts()) {
                set.addAll(p.getColors());
            }
            if(set.size() < 1) return 5;
            else if(set.size() > 1) return 6;
            else return set.iterator().next().ordinal();
        }
    }
}
