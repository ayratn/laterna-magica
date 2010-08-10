/**
 * CastAction.java
 * 
 * Created on 16.04.2010
 */

package net.slightlymagic.laterna.magica.action.play;


import static net.slightlymagic.laterna.magica.util.MagicaPredicates.*;
import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.card.CardObject;
import net.slightlymagic.laterna.magica.characteristics.CardType;
import net.slightlymagic.laterna.magica.player.Player;

import com.google.common.base.Predicate;


/**
 * The class CastAction. The class CastAction is used to encapsulate the data about playing a spell.
 * 
 * @version V0.0 16.04.2010
 * @author Clemens Koza
 */
public class CastAction extends PlayAction {
    private static final Predicate<? super MagicObject> isLand = card(has(CardType.LAND));
    
    /**
     * @param controller The player that cast the spell
     * @param ob The spell card to cast
     */
    public CastAction(Player controller, CardObject ob) {
        super(controller, ob);
        if(ob != null && isLand.apply(ob)) throw new IllegalArgumentException(ob + " is a land");
    }
    
    /**
     * Returns the card that was cast as a spell
     */
    @Override
    public CardObject getObject() {
        return (CardObject) super.getObject();
    }
    
    @Override
    public String toString() {
        return "Cast \"" + getObject() + "\"";
    }
}
