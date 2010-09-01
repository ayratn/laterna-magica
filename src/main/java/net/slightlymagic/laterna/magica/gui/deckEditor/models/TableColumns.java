/**
 * TableColumns.java
 * 
 * Created on 01.09.2010
 */

package net.slightlymagic.laterna.magica.gui.deckEditor.models;


/**
 * The class TableColumns.
 * 
 * @version V0.0 01.09.2010
 * @author Clemens Koza
 */
public interface TableColumns<T> {
    public int getColumnCount();
    
    public Class<?> getColumnClass(int column);
    
    public String getColumnName(int column);
    
    public Object getValueAt(T value, int column);
}
