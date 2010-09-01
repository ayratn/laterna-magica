/**
 * TemplateColumns.java
 * 
 * Created on 01.09.2010
 */

package net.slightlymagic.laterna.magica.gui.deckEditor.models;


import net.slightlymagic.laterna.magica.card.CardParts;
import net.slightlymagic.laterna.magica.card.CardTemplate;


/**
 * The class TemplateColumns.
 * 
 * @version V0.0 01.09.2010
 * @author Clemens Koza
 */
public class TemplateColumns implements TableColumns<CardTemplate> {
    private final TableColumns<? super CardParts> columns;
    
    public TemplateColumns() {
        this(new DefaultPartsColumns());
    }
    
    public TemplateColumns(TableColumns<? super CardParts> columns) {
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
    
    public Object getValueAt(CardTemplate value, int column) {
        return columns.getValueAt(value.getCardParts().get(0), column);
    }
}
