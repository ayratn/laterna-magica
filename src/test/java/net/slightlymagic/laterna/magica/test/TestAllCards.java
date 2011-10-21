/**
 * TestAllCards.java
 * 
 * Created on 03.04.2010
 */

package net.slightlymagic.laterna.magica.test;


import java.io.IOException;

import net.slightlymagic.laterna.magica.LaternaInit;
import net.slightlymagic.laterna.magica.card.Card;
import net.slightlymagic.laterna.magica.cards.AllCards;
import net.slightlymagic.laterna.magica.characteristic.CardSnapshot;

import org.junit.Test;


/**
 * The class TestAllCards.
 * 
 * @version V0.0 03.04.2010
 * @author Clemens Koza
 */
public class TestAllCards {
    private static AllCards cards;
    
    static {
        try {
            LaternaInit.init();
            cards = new AllCards();
        } catch(Exception ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    @Test
    public void testCompile() throws IOException {
        cards.compile();
    }
    
    @Test
    public void testRead() {
        CardSnapshot s = null;
        for(Card c:cards.getTemplates())
            s = c.getSimpleCardParts().get(0).getCharacteristics(s);
    }
    
    public static void print(CardSnapshot c) {
        System.out.printf("%s - %s%n", c.getName(), c.getManaCost());
        System.out.println(c.getColors().valueString());
        System.out.printf("%s %s - %s%n", c.getSuperTypes().valueString(), c.getTypes().valueString(),
                c.getSubTypes().valueString());
        System.out.println(c.getAbilities().valueString());
        System.out.printf("%d/%d%n", c.getPower(), c.getToughness());
        System.out.println();
    }
}
