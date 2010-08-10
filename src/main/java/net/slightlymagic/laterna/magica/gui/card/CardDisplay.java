/**
 * CardDisplay.java
 * 
 * Created on 02.04.2010
 */

package net.slightlymagic.laterna.magica.gui.card;


import java.util.Observable;

import net.slightlymagic.laterna.magica.characteristic.CharacteristicSnapshot;


/**
 * The class CardDisplay. A card display shows information from a {@link CharacteristicSnapshot} and will therefore
 * usually {@link Observable observe} it.
 * 
 * @version V0.0 02.04.2010
 * @author Clemens Koza
 */
public interface CardDisplay {
    /**
     * This method is optional. That is, it may throw an {@link UnsupportedOperationException}.
     */
    public void setCard(CharacteristicSnapshot c);
    
    public CharacteristicSnapshot getCard();
}
