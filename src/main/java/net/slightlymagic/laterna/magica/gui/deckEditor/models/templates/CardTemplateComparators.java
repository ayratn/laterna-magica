/**
 * CardTemplateComparators.java
 * 
 * Created on 01.09.2010
 */

package net.slightlymagic.laterna.magica.gui.deckEditor.models.templates;


import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import net.slightlymagic.laterna.magica.card.CardParts;
import net.slightlymagic.laterna.magica.card.CardTemplate;
import net.slightlymagic.laterna.magica.characteristics.MagicColor;


/**
 * The class CardTemplateComparators.
 * 
 * @version V0.0 01.09.2010
 * @author Clemens Koza
 */
public enum CardTemplateComparators implements Comparator<CardTemplate> {
    INSTANCE;
    
    public int compare(CardTemplate o1, CardTemplate o2) {
        int i = getColor(o1) - getColor(o2);
        if(i != 0) return i;
        return o1.toString().compareTo(o2.toString());
    }
    
    private int getColor(CardTemplate pr) {
        Set<MagicColor> set = new HashSet<MagicColor>();
        for(CardParts p:pr.getCardParts()) {
            set.addAll(p.getColors());
        }
        if(set.size() < 1) return 5;
        else if(set.size() > 1) return 6;
        else return set.iterator().next().ordinal();
    }
}
