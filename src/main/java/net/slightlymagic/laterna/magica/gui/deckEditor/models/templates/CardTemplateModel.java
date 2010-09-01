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


/**
 * The class CardTemplateModel.
 * 
 * @version V0.0 01.09.2010
 * @author Clemens Koza
 */
public class CardTemplateModel extends AbstractTableModel {
    private static final long                        serialVersionUID = -1900903016161043459L;
    
    private final CardPoolModel                      model;
    private final List<CardTemplate>                 keys;
    
    private final TableColumns<? super CardTemplate> columns;
    
    public CardTemplateModel(TableColumns<? super CardTemplate> columns, CardPoolModel model) {
        this.columns = columns;
        this.model = model;
        keys = new ArrayList<CardTemplate>();
        refreshKeys();
        model.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if(e.getType() != TableModelEvent.UPDATE) refreshKeys();
            }
        });
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
    
    public Object getValueAt(CardTemplate t, int column) {
        return columns.getValueAt(t, column);
    }
    
    public Object getValueAt(int row, int column) {
        CardTemplate t = getRow(row);
        return getValueAt(t, column);
    }
    
    public Map<Printing, Integer> getPrintings(final CardTemplate template) {
        return new AbstractMap<Printing, Integer>() {
            private final Set<Entry<Printing, Integer>> entrySet;
            
            {
                entrySet = new AbstractSet<Entry<Printing, Integer>>() {
                    @Override
                    public int size() {
                        return template.getPrintings().size();
                    }
                    
                    @Override
                    public Iterator<Entry<Printing, Integer>> iterator() {
                        return new AbstractIterator<Entry<Printing, Integer>>() {
                            private Iterator<Printing> delegate = template.getPrintings().iterator();
                            
                            @Override
                            protected Entry<Printing, Integer> computeNext() {
                                if(!delegate.hasNext()) return endOfData();
                                final Printing p = delegate.next();
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
                if(!(key instanceof Printing) || ((Printing) key).getTemplate() != template) return null;
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
        for(Printing p:getPoolModel().getPrintings().keySet())
            set.add(p.getTemplate());
        keys.clear();
        keys.addAll(set);
        Collections.sort(keys, CardTemplateComparators.INSTANCE);
        fireTableDataChanged();
    }
}
