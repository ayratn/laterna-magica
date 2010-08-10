/**
 * CardCharacteristics.java
 * 
 * Created on 21.04.2010
 */

package net.slightlymagic.laterna.magica.characteristic;


import net.slightlymagic.laterna.magica.card.CardParts;


/**
 * The class CardCharacteristics.
 * 
 * @version V0.0 21.04.2010
 * @author Clemens Koza
 */
public interface CardCharacteristics extends ObjectCharacteristics {
    /**
     * <p>
     * Returns the card parts that is the base of this characteristics.
     * </p>
     */
    public CardParts getParts();
}
