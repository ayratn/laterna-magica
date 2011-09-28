/**
 * TemplateColumns.java
 * 
 * Created on 01.09.2010
 */

package net.slightlymagic.laterna.magica.gui.deckEditor.models;


import net.slightlymagic.laterna.magica.card.Card;
import net.slightlymagic.laterna.magica.card.SimpleCardParts;


/**
 * The class TemplateColumns.
 * 
 * @version V0.0 01.09.2010
 * @author Clemens Koza
 */
public class TemplateColumns implements TableColumns<Card> {
    private final TableColumns<? super SimpleCardParts> columns;
    
    public TemplateColumns() {
        this(new DefaultPartsColumns());
    }
    
    public TemplateColumns(TableColumns<? super SimpleCardParts> columns) {
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
    
    public Object getValueAt(Card value, int column) {
        return columns.getValueAt(value.getSimpleCardParts().get(0), column);
    }
}
