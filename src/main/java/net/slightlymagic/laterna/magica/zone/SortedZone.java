/**
 * SortedZone.java
 * 
 * Created on 05.09.2009
 */

package net.slightlymagic.laterna.magica.zone;


import java.util.List;

import net.slightlymagic.laterna.magica.MagicObject;



/**
 * The class SortedZone.
 * 
 * @version V0.0 05.09.2009
 * @author Clemens Koza
 */
public interface SortedZone extends Zone {
    /**
     * Returns the cards in the Zone as a List. The "topmost" card has the highest index.
     * <ul>
     * <li>In the library, that is the card a player would normally draw</li>
     * <li>In the graveyard, that is the card that was put onto it last</li>
     * <li>On the stack, that is the object that would resolve next</li>
     * </ul>
     */
    public List<MagicObject> getCards();
    
    /**
     * Shuffles the cards in the Zone
     */
    public void shuffle();
}
