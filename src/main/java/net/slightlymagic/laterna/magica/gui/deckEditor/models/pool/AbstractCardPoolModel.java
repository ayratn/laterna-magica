/**
 * AbstractCardPoolModel.java
 * 
 * Created on 01.09.2010
 */

package net.slightlymagic.laterna.magica.gui.deckEditor.models.pool;


import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import net.slightlymagic.laterna.magica.card.Printing;
import net.slightlymagic.laterna.magica.gui.deckEditor.models.TableColumns;

import com.google.common.collect.AbstractIterator;


/**
 * The class AbstractCardPoolModel.
 * 
 * @version V0.0 01.09.2010
 * @author Clemens Koza
 */
public abstract class AbstractCardPoolModel extends AbstractTableModel implements CardPoolModel {
    private static final long                    serialVersionUID = -3899609398431935993L;
    
    private final TableColumns<? super Printing> columns;
    
    public AbstractCardPoolModel(TableColumns<? super Printing> columns) {
        this.columns = columns;
    }
    
    public int getColumnCount() {
        return columns.getColumnCount();
    }
    
    @Override
    public Class<?> getColumnClass(int column) {
        return columns.getColumnClass(column);
    }
    
    @Override
    public String getColumnName(int column) {
        return columns.getColumnName(column);
    }
    
    public Object getValueAt(Printing p, int column) {
        return columns.getValueAt(p, column);
    }
    
    public Object getValueAt(int row, int column) {
        Printing p = getRow(row);
        return getValueAt(p, column);
    }
    
    public Map<Printing, Integer> getPrintings() {
        return new AbstractMap<Printing, Integer>() {
            private final Set<Entry<Printing, Integer>> entrySet;
            
            {
                entrySet = new AbstractSet<Entry<Printing, Integer>>() {
                    @Override
                    public int size() {
                        return getRowCount();
                    }
                    
                    @Override
                    public Iterator<Entry<Printing, Integer>> iterator() {
                        return new AbstractIterator<Entry<Printing, Integer>>() {
                            private int index = 0;
                            
                            @Override
                            protected Entry<Printing, Integer> computeNext() {
                                if(index == getRowCount()) return endOfData();
                                final Printing p = getRow(index++);
                                return new Entry<Printing, Integer>() {
                                    public Printing getKey() {
                                        return p;
                                    }
                                    
                                    public Integer getValue() {
                                        return getCount(p);
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
                if(!(key instanceof Printing)) return null;
                return getCount((Printing) key);
            }
            
            @Override
            public Set<Entry<Printing, Integer>> entrySet() {
                return entrySet;
            }
        };
    }
}
