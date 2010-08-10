/**
 * CopiesFormat.java
 * 
 * Created on 29.10.2009
 */

package net.slightlymagic.laterna.magica.deck.impl;


import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.slightlymagic.laterna.magica.card.CardParts;
import net.slightlymagic.laterna.magica.card.CardTemplate;
import net.slightlymagic.laterna.magica.card.Printing;
import net.slightlymagic.laterna.magica.characteristics.SuperType;
import net.slightlymagic.laterna.magica.deck.Deck;
import net.slightlymagic.laterna.magica.deck.Deck.DeckType;

import com.google.common.base.Predicate;


/**
 * <p>
 * The class CopiesFormat. The copies format checks if a deck contains at most the specified number of copies of a
 * single card, except for basic lands, which may be contained any number of times.
 * </p>
 * <p>
 * This class considers the {@linkplain DeckType#MAIN_DECK main deck}, {@linkplain DeckType#SIDEBOARD sideboard}
 * and {@linkplain DeckType#EDH_GENERAL Elder Dragon Highlander General} pools. These are the card pools that
 * contain regular magic cards.
 * </p>
 * 
 * @version V0.0 29.10.2009
 * @author Clemens Koza
 */
public class CopiesFormat implements Predicate<Deck> {
    private int numCopies;
    
    public CopiesFormat() {
        this(4);
    }
    
    public CopiesFormat(int numCopies) {
        this.numCopies = numCopies;
    }
    
    public boolean apply(Deck o) {
        Map<CardTemplate, Integer> numbers = new HashMap<CardTemplate, Integer>();
        add(o.getPool(DeckType.MAIN_DECK), numbers);
        add(o.getPool(DeckType.SIDEBOARD), numbers);
        add(o.getPool(DeckType.EDH_GENERAL), numbers);
        main: for(Entry<CardTemplate, Integer> e:numbers.entrySet()) {
            if(e.getValue().intValue() <= numCopies) continue main;
            for(CardParts p:e.getKey().getCardParts())
                if(p.getSuperTypes().contains(SuperType.BASIC)) continue main;
            return false;
        }
        return true;
    }
    
    private static void add(Map<Printing, Integer> src, Map<CardTemplate, Integer> dst) {
        if(src == null) return;
        for(Entry<Printing, Integer> e:src.entrySet()) {
            Integer dstValue = dst.get(e.getKey());
            if(dstValue == null) dst.put(e.getKey().getTemplate(), e.getValue());
            else dst.put(e.getKey().getTemplate(), e.getValue().intValue() + dstValue.intValue());
        }
    }
}
