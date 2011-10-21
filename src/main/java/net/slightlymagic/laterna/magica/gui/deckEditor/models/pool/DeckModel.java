/**
 * DeckModel.java
 * 
 * Created on 30.07.2010
 */

package net.slightlymagic.laterna.magica.gui.deckEditor.models.pool;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.slightlymagic.laterna.magica.card.Printing;
import net.slightlymagic.laterna.magica.deck.Deck;
import net.slightlymagic.laterna.magica.deck.DeckType;
import net.slightlymagic.laterna.magica.gui.deckEditor.models.TableColumns;


/**
 * The class DeckModel. A deck model contains a specific number of every printing.
 * 
 * @version V0.0 30.07.2010
 * @author Clemens Koza
 */
public class DeckModel extends AbstractCardPoolModel {
    private static final long            serialVersionUID = 389522808287349833L;
    
    private final List<Printing>         keys;
    private final Map<Printing, Integer> pool;
    
    protected final int                  countColumn;
    
    public DeckModel(TableColumns<? super Printing> columns, Deck d, DeckType t, int countColumn) {
        this(columns, d.getPool(t), countColumn);
    }
    
    public DeckModel(TableColumns<? super Printing> columns, Map<Printing, Integer> pool, int countColumn) {
        super(columns);
        if(pool == null) throw new IllegalArgumentException();
        this.countColumn = countColumn;
        this.pool = pool;
        keys = new ArrayList<Printing>();
        refreshKeys();
    }
    
    public int getRowCount() {
        return keys.size();
    }
    
    public Printing getRow(int row) {
        return keys.get(row);
    }
    
    public int getCount(Printing p) {
        Integer i = pool.get(p);
        return i == null? 0:i;
    }
    
    @Override
    public int getColumnCount() {
        if(countColumn == -1) return super.getColumnCount();
        else return super.getColumnCount() + 1;
    }
    
    @Override
    public Class<?> getColumnClass(int column) {
        if(countColumn == -1) return super.getColumnClass(column);
        else if(column == countColumn) return Integer.class;
        else return super.getColumnClass(column > countColumn? column - 1:column);
    }
    
    @Override
    public String getColumnName(int column) {
        if(countColumn == -1) return super.getColumnName(column);
        else if(column == countColumn) return "Count";
        else return super.getColumnName(column > countColumn? column - 1:column);
    }
    
    @Override
    public Object getValueAt(Printing p, int column) {
        if(countColumn == -1) return super.getValueAt(p, column);
        else if(column == countColumn) return getCount(p);
        else return super.getValueAt(p, column > countColumn? column - 1:column);
    }
    
    private void refreshKeys() {
        keys.clear();
        keys.addAll(pool.keySet());
        Collections.sort(keys, PrintingComparators.COLOR_NAME_INSTANCE);
        fireTableDataChanged();
    }
    
    public void add(Printing p) {
        Integer count = pool.put(p, 1);
        int index = Collections.binarySearch(keys, p, PrintingComparators.COLOR_NAME_INSTANCE);
        if(count == null) {
            //transform to the insertion
            index = -index - 1;
            keys.add(index, p);
            fireTableRowsInserted(index, index);
        } else {
            pool.put(p, count.intValue() + 1);
            fireTableCellUpdated(index, countColumn);
        }
    }
    
    public void remove(Printing p) {
        Integer count = pool.remove(p);
        if(count == null) return;
        int index = Collections.binarySearch(keys, p, PrintingComparators.COLOR_NAME_INSTANCE);
        if(count.intValue() == 1) {
            keys.remove(index);
            fireTableRowsDeleted(index, index);
        } else {
            pool.put(p, count.intValue() - 1);
            fireTableCellUpdated(index, countColumn);
        }
    }
}
