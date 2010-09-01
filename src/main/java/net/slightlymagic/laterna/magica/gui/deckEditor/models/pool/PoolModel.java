/**
 * PoolModel.java
 * 
 * Created on 30.07.2010
 */

package net.slightlymagic.laterna.magica.gui.deckEditor.models.pool;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.slightlymagic.laterna.magica.LaternaMagica;
import net.slightlymagic.laterna.magica.card.Printing;
import net.slightlymagic.laterna.magica.gui.deckEditor.models.TableColumns;


/**
 * The class PoolModel. A pool model contains certain printings, but has an infinite number of these, therefore add
 * and remove are no-ops.
 * 
 * @version V0.0 30.07.2010
 * @author Clemens Koza
 */
public class PoolModel extends AbstractCardPoolModel {
    private static final long       serialVersionUID = 4864856490229796392L;
    private static final Class<?>[] classes          = {String.class};
    private static final String[]   names            = {"Name"};
    
    private final List<Printing>    pool;
    
    public PoolModel(TableColumns<? super Printing> columns) {
        this(columns, LaternaMagica.CARDS().getPrintings());
    }
    
    public PoolModel(TableColumns<? super Printing> columns, Collection<Printing> pool) {
        super(columns);
        this.pool = new ArrayList<Printing>(pool);
        Collections.sort(this.pool, PrintingComparators.COLOR_NAME_INSTANCE);
    }
    
    @Override
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
    
    public Printing getRow(int row) {
        return pool.get(row);
    }
    
    public int getCount(Printing p) {
        return (Collections.binarySearch(pool, p, PrintingComparators.COLOR_NAME_INSTANCE) >= 0)? 1:0;
    }
    
    public int getRowCount() {
        return pool.size();
    }
    
    @Override
    public Object getValueAt(int row, int column) {
        Printing p = pool.get(row);
        switch(column) {
            case 0:
                return p.getTemplate().toString();
            default:
                throw new IndexOutOfBoundsException();
        }
    }
    
    public void add(Printing p) {}
    
    public void remove(Printing p) {}
}
