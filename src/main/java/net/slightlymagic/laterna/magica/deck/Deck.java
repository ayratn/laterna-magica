/**
 * Deck.java
 * 
 * Created on 23.10.2009
 */

package net.slightlymagic.laterna.magica.deck;


import java.util.Map;

import net.slightlymagic.laterna.magica.card.Printing;


/**
 * <p>
 * The class Deck. A deck is the container for multiple sets of cards, every one having a distinct meaning.
 * </p>
 * 
 * @version V0.0 23.10.2009
 * @author Clemens Koza
 */
public interface Deck {
    public static enum DeckType {
        /**
         * <p>
         * A card pool meant for cards that are not really part of the deck. For example, if building a deck after
         * a draft, this card pool contains all excess cards. This pool is also used by decks that don't really
         * represent decks but only card pools.
         * </p>
         */
        POOL,
        /**
         * <p>
         * The main deck is the card pool that becomes a player's library at the beginning of a game.
         * </p>
         */
        MAIN_DECK,
        /**
         * <p>
         * The sideboard is the card pool that a player may use to adjust his main deck before a game starts.
         * </p>
         */
        SIDEBOARD,
        /**
         * <p>
         * The vanguard deck contains the player's single vanguard card in the vanguard casual variant.
         * </p>
         */
        VANGUARD_DECK,
        /**
         * <p>
         * The planar deck contains a player's plane cards in the planar magic casual variant.
         * </p>
         */
        PLANAR_DECK,
        /**
         * <p>
         * The EDH general contains a player's general, a legendary creature card, in the elder dragon highlander
         * casual variant.
         * </p>
         */
        EDH_GENERAL;
    }
    
    /**
     * <p>
     * Creates the pool for the specified type.
     * </p>
     */
    public void addPool(DeckType pool);
    
    /**
     * <p>
     * Removes the pool for the specified type.
     * </p>
     */
    public void removePool(DeckType pool);
    
    /**
     * <p>
     * Returns the mapping of cards to card count in a specific card pool of the deck. A pool may be {@code null}.
     * </p>
     */
    public Map<Printing, Integer> getPool(DeckType pool);
}
