/**
 * PrintingColumns.java
 * 
 * Created on 01.09.2010
 */

package net.slightlymagic.laterna.magica.gui.deckEditor.models;


import net.slightlymagic.laterna.magica.card.CardTemplate;
import net.slightlymagic.laterna.magica.card.Printing;


/**
 * The class PrintingColumns.
 * 
 * @version V0.0 01.09.2010
 * @author Clemens Koza
 */
public class PrintingColumns implements TableColumns<Printing> {
    private final TableColumns<? super CardTemplate> columns;
    
    public PrintingColumns() {
        this(new TemplateColumns());
    }
    
    public PrintingColumns(TableColumns<? super CardTemplate> columns) {
        this.columns = columns;
    }
    
    public int getColumnCount() {
        return columns.getColumnCount();
    }
    
    public Class<?> getColumnClass(int column) {
        return columns.getColumnClass(column);
    }
    
    public String getColumnName(int column) {
        return columns.getColumnName(column);
    }
    
    public Object getValueAt(Printing value, int column) {
        return columns.getValueAt(value.getTemplate(), column);
    }
}
