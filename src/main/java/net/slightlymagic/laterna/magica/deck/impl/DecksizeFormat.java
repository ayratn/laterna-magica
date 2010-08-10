/**
 * DecksizeFormat.java
 * 
 * Created on 29.10.2009
 */

package net.slightlymagic.laterna.magica.deck.impl;


import net.slightlymagic.laterna.magica.deck.Deck;
import net.slightlymagic.laterna.magica.deck.Deck.DeckType;

import com.google.common.base.Predicate;


/**
 * <p>
 * The class DecksizeFormat. This class restricts the decksize of one part of a deck between a minimum and maximum
 * number of cards.
 * </p>
 * 
 * @version V0.0 29.10.2009
 * @author Clemens Koza
 */
public class DecksizeFormat implements Predicate<Deck> {
    private int      min, max;
    private DeckType type;
    
    public DecksizeFormat(DeckType type, int min, int max) {
        this.type = type;
        this.min = min;
        this.max = max;
    }
    
    public boolean apply(Deck o) {
        int count = 0;
        if(o.getPool(type) != null) for(Integer i:o.getPool(type).values()) {
            count += i;
            if(count > max) return false;
        }
        return count >= min && count <= max;
    }
}
