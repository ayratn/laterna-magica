/**
 * PoolModel.java
 * 
 * Created on 30.07.2010
 */

package net.slightlymagic.laterna.magica.gui.deckEditor;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import net.slightlymagic.laterna.magica.LaternaMagica;
import net.slightlymagic.laterna.magica.card.CardTemplate;
import net.slightlymagic.laterna.magica.card.Printing;


import com.google.common.base.Function;
import com.google.common.collect.Collections2;


/**
 * The class PoolModel. A pool model contains certain printings, but has an infinite number of these, therefore add
 * and remove are no-ops.
 * 
 * @version V0.0 30.07.2010
 * @author Clemens Koza
 */
public class PoolModel extends AbstractTableModel implements CardPoolModel {
    private static final long       serialVersionUID = 4864856490229796392L;
    private static final Class<?>[] classes          = {String.class};
    private static final String[]   names            = {"Name"};
    
    private List<Printing>          pool;
    
    public PoolModel() {
        this(LaternaMagica.CARDS().getTemplates());
    }
    
    public PoolModel(Collection<CardTemplate> cards) {
        pool = new ArrayList<Printing>(Collections2.transform(cards, new Function<CardTemplate, Printing>() {
            public Printing apply(CardTemplate from) {
                List<Printing> l = from.getPrintings();
                return l.get(l.size() - 1);
            }
        }));
        Collections.sort(pool, DeckModel.c);
    }
    
    public PoolModel(List<Printing> pool) {
        this.pool = pool;
        Collections.sort(pool, DeckModel.c);
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
        return pool.size();
    }
    
    public Object getValueAt(int row, int column) {
        Printing p = pool.get(row);
        switch(column) {
            case 0:
                return p.getTemplate().toString();
            default:
                throw new AssertionError();
        }
    }
    
    public void add(Printing p) {}
    
    public void remove(Printing p) {}
}
