/**
 * TestAllCards.java
 * 
 * Created on 03.04.2010
 */

package net.slightlymagic.laterna.magica.test;


import java.io.IOException;

import net.slightlymagic.laterna.magica.LaternaMagica;
import net.slightlymagic.laterna.magica.card.CardTemplate;
import net.slightlymagic.laterna.magica.cards.AllCards;
import net.slightlymagic.laterna.magica.characteristic.CharacteristicSnapshot;

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
            LaternaMagica.init();
            cards = new AllCards();
        } catch(IOException ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    @Test
    public void testCompile() throws IOException {
        cards.compile();
    }
    
    @Test
    public void testRead() {
        for(CardTemplate t:cards.getTemplates())
            print(t.getCardParts().get(0).getCharacteristics(new CharacteristicSnapshot()));
    }
    
    public static void print(CharacteristicSnapshot c) {
        System.out.printf("%s - %s%n", c.getName(), c.getManaCost());
        System.out.println(c.getColors().valueString());
        System.out.printf("%s %s - %s%n", c.getSuperTypes().valueString(), c.getTypes().valueString(),
                c.getSubTypes().valueString());
        System.out.println(c.getAbilities().valueString());
        System.out.printf("%d/%d%n", c.getPower(), c.getToughness());
        System.out.println();
    }
}
