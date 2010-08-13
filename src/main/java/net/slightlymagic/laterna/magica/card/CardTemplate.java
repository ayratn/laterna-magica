/**
 * CardTemplate.java
 * 
 * Created on 04.09.2009
 */

package net.slightlymagic.laterna.magica.card;


import java.io.Serializable;
import java.util.List;


/**
 * <p>
 * The class CardTemplate.
 * </p>
 * 
 * <p>
 * A CardTemplate is not an instance, like <i>a</i> "Llanowar Elves" card, but represents the oracle of the card,
 * like <i>the</i> "Llanowar Elves" card.
 * </p>
 * 
 * @version V0.0 04.09.2009
 * @author Clemens Koza
 */
public interface CardTemplate extends Serializable {
    /**
     * <p>
     * Returns the layout of the card, as it affects the rules. The layout doesn't represent the exact form of the
     * printing. For example, all these are considered {@link #NORMAL}:
     * </p>
     * <ul>
     * <li>The pre-8th border</li>
     * <li>The post-8th border</li>
     * <li>The timeshifted border</li>
     * <li>The planar chaos alternate border</li>
     * <li>The future shifted border</li>
     * <li>The Planeswalker border</li>
     * </ul>
     * <p>
     * The reason for this is that all these card frames present a single set of card parts: they have one name,
     * mana cost, text box and so on. Different layouts indicate that the sets of card parts have a different.
     * meaning to the rules.
     * </p>
     */
    public static enum CardLayout {
        /**
         * <p>
         * A card that has only one set of characteristics. Objects that are not physical cards are normal.
         * </p>
         */
        NORMAL,
        /**
         * <p>
         * A split card (see {@magic.ruleRef 20100716/R708}). While on the stack, a split card has
         * only the set of characteristics that was played. Otherwise, it has both.
         * </p>
         */
        SPLIT,
        /**
         * <p>
         * A flip card (see {@magic.ruleRef 20100716/R709}). The first set of characteristics is
         * used normally. However, while on the battlefield and flipped, the alternative characteristic is used.
         * </p>
         */
        FLIP;
    }
    
    /**
     * <p>
     * Returns if this card is normal, split, or flip.
     * </p>
     */
    public CardLayout getType();
    
    /**
     * <p>
     * Returns all card parts "printed" on this card.
     * </p>
     */
    public List<CardParts> getCardParts();
    
    public List<Printing> getPrintings();
    
    /**
     * Returns a concatenation of all the parts' names, separated by "//". The result may, for example, be created
     * by
     * 
     * <pre>
     * return Joiner.on(&quot;//&quot;).join(getCardParts());
     * </pre>
     */
    @Override
    public String toString();
}
