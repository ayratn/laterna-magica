/**
 * DefaultPartsColumns.java
 * 
 * Created on 01.09.2010
 */

package net.slightlymagic.laterna.magica.gui.deckEditor.models;


import static net.slightlymagic.laterna.magica.characteristic.CardType.*;
import net.slightlymagic.laterna.magica.card.SimpleCardParts;
import net.slightlymagic.laterna.magica.mana.ManaSequence;

import com.google.common.base.Joiner;


/**
 * The class DefaultPartsColumns.
 * 
 * @version V0.0 01.09.2010
 * @author Clemens Koza
 */
public class DefaultPartsColumns implements TableColumns<SimpleCardParts> {
    private static final String[]   names   = {"Name", "Cost", "Type", "P/T/L"};
    private static final Class<?>[] classes = {String.class, ManaSequence.class, String.class, String.class};
    
    public String getColumnName(int column) {
        return names[column];
    }
    
    public java.lang.Class<?> getColumnClass(int column) {
        return classes[column];
    }
    
    public int getColumnCount() {
        return classes.length;
    }
    
    public Object getValueAt(SimpleCardParts value, int column) {
        switch(column) {
            case 0: //name
                return value.getName();
            case 1: //cost
                return value.getManaCost();
            case 2: //type
                StringBuilder sb = new StringBuilder();
                Joiner j = Joiner.on(' ');
                j.appendTo(sb, value.getSuperTypes());
                if(!value.getSuperTypes().isEmpty()) sb.append(' ');
                j.appendTo(sb, value.getTypes());
                if(!value.getSubTypes().isEmpty()) sb.append(" - ");
                j.appendTo(sb, value.getSubTypes());
                return sb.toString();
            case 3: //p/t/l
                if(value.getTypes().contains(CREATURE)) return value.getPower() + "/" + value.getToughness();
                if(value.getTypes().contains(PLANESWALKER)) return value.getLoyalty();
                else return "";
            default:
                throw new IndexOutOfBoundsException();
        }
    }
}
