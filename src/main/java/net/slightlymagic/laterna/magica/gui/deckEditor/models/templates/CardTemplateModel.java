/**
 * CardTemplateModel.java
 * 
 * Created on 01.09.2010
 */

package net.slightlymagic.laterna.magica.gui.deckEditor.models.templates;


import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import net.slightlymagic.laterna.magica.card.CardTemplate;
import net.slightlymagic.laterna.magica.card.Printing;
import net.slightlymagic.laterna.magica.gui.deckEditor.models.TableColumns;
import net.slightlymagic.laterna.magica.gui.deckEditor.models.pool.CardPoolModel;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;


/**
 * The class CardTemplateModel.
 * 
 * @version V0.0 01.09.2010
 * @author Clemens Koza
 */
public class CardTemplateModel extends AbstractTableModel {
    private static final long                  serialVersionUID = -1900903016161043459L;
    
    private CardPoolModel                      model;
    private TableColumns<? super CardTemplate> columns;
    private int                                countColumn;
    
    private final List<CardTemplate>           keys;
    private final TableModelListener           l;
    
    public CardTemplateModel(TableColumns<? super CardTemplate> columns) {
        this(columns, -1);
    }
    
    public CardTemplateModel(TableColumns<? super CardTemplate> columns, int countColumn) {
        this.countColumn = countColumn;
        this.columns = columns;
        keys = new ArrayList<CardTemplate>();
        l = new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if(e.getType() != TableModelEvent.UPDATE) refreshKeys();
                else if(CardTemplateModel.this.countColumn != -1) {
                    int first = e.getFirstRow(), last = e.getLastRow();
                    if(first == TableModelEvent.HEADER_ROW) return;
                    if(first != last) fireTableDataChanged();
                    else fireTableCellUpdated(keys.indexOf(getPoolModel().getRow(first).getTemplate()),
                            CardTemplateModel.this.countColumn);
                }
            }
        };
    }
    
    public void setColumns(TableColumns<? super CardTemplate> columns) {
        setColumns(columns, -1);
    }
    
    public void setColumns(TableColumns<? super CardTemplate> columns, int countColumn) {
        this.columns = columns;
        this.countColumn = countColumn;
        fireTableStructureChanged();
    }
    
    public void setPoolModel(CardPoolModel model) {
        if(this.model != null) this.model.removeTableModelListener(l);
        this.model = model;
        if(this.model != null) this.model.addTableModelListener(l);
        refreshKeys();
    }
    
    public CardPoolModel getPoolModel() {
        return model;
    }
    
    public void add(Printing p) {
        getPoolModel().add(p);
    }
    
    public void remove(Printing p) {
        getPoolModel().remove(p);
    }
    
    public CardTemplate getRow(int row) {
        return keys.get(row);
    }
    
    @Override
    public int getRowCount() {
        return keys.size();
    }
    
    @Override
    public int getColumnCount() {
        if(countColumn == -1) return columns.getColumnCount();
        else return columns.getColumnCount() + 1;
    }
    
    @Override
    public Class<?> getColumnClass(int column) {
        if(countColumn == -1) return columns.getColumnClass(column);
        else if(column == countColumn) return Integer.class;
        else return columns.getColumnClass(column > countColumn? column - 1:column);
    }
    
    @Override
    public String getColumnName(int column) {
        if(countColumn == -1) return columns.getColumnName(column);
        else if(column == countColumn) return "Count";
        else return columns.getColumnName(column > countColumn? column - 1:column);
    }
    
    public Object getValueAt(CardTemplate t, int column) {
        if(countColumn == -1) return columns.getValueAt(t, column);
        else if(column == countColumn) return getCount(t);
        else return columns.getValueAt(t, column > countColumn? column - 1:column);
    }
    
    public Object getValueAt(int row, int column) {
        CardTemplate t = getRow(row);
        return getValueAt(t, column);
    }
    
    public int getCount(CardTemplate template) {
        int count = 0;
        for(Printing p:template.getPrintings())
            count += getPoolModel().getCount(p);
        return count;
    }
    
    public Map<Printing, Integer> getPrintings(final CardTemplate template) {
        return new AbstractMap<Printing, Integer>() {
            private final Set<Entry<Printing, Integer>> entrySet;
            
            {
                entrySet = new AbstractSet<Entry<Printing, Integer>>() {
                    @Override
                    public int size() {
                        if(getPoolModel() == null) return 0;
                        int count = 0;
                        for(Printing p:template.getPrintings())
                            if(getPoolModel().getPrintings().containsKey(p)) count++;
                        return count;
                    }
                    
                    @Override
                    public Iterator<Entry<Printing, Integer>> iterator() {
                        if(getPoolModel() == null) return Iterators.emptyIterator();
                        return new AbstractIterator<Entry<Printing, Integer>>() {
                            private Iterator<Printing> delegate = template.getPrintings().iterator();
                            
                            @Override
                            protected Entry<Printing, Integer> computeNext() {
                                if(!delegate.hasNext()) return endOfData();
                                final Printing p = delegate.next();
                                if(!getPoolModel().getPrintings().containsKey(p)) return computeNext();
                                return new Entry<Printing, Integer>() {
                                    public Printing getKey() {
                                        return p;
                                    }
                                    
                                    public Integer getValue() {
                                        return getPoolModel().getCount(p);
                                    }
                                    
                                    public Integer setValue(Integer value) {
                                        throw new UnsupportedOperationException();
                                    }
                                    
                                    @Override
                                    public int hashCode() {
                                        Object key = getKey(), val = getValue();
                                        return (key == null? 0:key.hashCode()) ^ (val == null? 0:val.hashCode());
                                    }
                                    
                                    @Override
                                    public boolean equals(Object o) {
                                        if(!(o instanceof Entry<?, ?>)) return false;
                                        Entry<?, ?> other = (Entry<?, ?>) o;
                                        
                                        Object key = getKey(), val = getValue();
                                        if(key == null? other.getKey() != null:!key.equals(other.getKey())) return false;
                                        if(val == null? other.getKey() != null:!val.equals(other.getKey())) return false;
                                        return true;
                                    }
                                };
                            }
                        };
                    }
                };
            }
            
            @Override
            public Integer get(Object key) {
                if(getPoolModel() == null || !(key instanceof Printing)
                        || ((Printing) key).getTemplate() != template) return null;
                return getPoolModel().getCount((Printing) key);
            }
            
            @Override
            public Set<Entry<Printing, Integer>> entrySet() {
                return entrySet;
            }
        };
    }
    
    private void refreshKeys() {
        HashSet<CardTemplate> set = new HashSet<CardTemplate>();
        if(getPoolModel() != null) for(Printing p:getPoolModel().getPrintings().keySet())
            set.add(p.getTemplate());
        keys.clear();
        keys.addAll(set);
        Collections.sort(keys, CardTemplateComparators.INSTANCE);
        fireTableDataChanged();
    }
}
