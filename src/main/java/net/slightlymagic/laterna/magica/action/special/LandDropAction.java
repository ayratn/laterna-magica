/**
 * LandDropAction.java
 * 
 * Created on 30.03.2010
 */

package net.slightlymagic.laterna.magica.action.special;


import static net.slightlymagic.laterna.magica.util.MagicaPredicates.*;
import static net.slightlymagic.laterna.magica.util.MagicaUtils.*;
import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.action.play.SpecialAction;
import net.slightlymagic.laterna.magica.card.CardObject;
import net.slightlymagic.laterna.magica.characteristics.CardType;
import net.slightlymagic.laterna.magica.counter.Counter;
import net.slightlymagic.laterna.magica.edit.CompoundEdit;
import net.slightlymagic.laterna.magica.player.Player;
import net.slightlymagic.laterna.magica.util.MagicaUtils;

import com.google.common.base.Predicate;


/**
 * The class LandDropAction.
 * 
 * @version V0.0 30.03.2010
 * @author Clemens Koza
 */
public class LandDropAction extends SpecialAction {
    private static final Predicate<? super MagicObject> isLand = card(has(CardType.LAND));
    
    private CardObject                                  land;
    
    public LandDropAction(CardObject land) {
        super(MagicaUtils.you(land), land);
        if(!isLand.apply(land)) throw new IllegalArgumentException(land + " is not a land");
        this.land = land;
    }
    
    public CardObject getLand() {
        return land;
    }
    
    @Override
    public boolean isLegalTiming() {
        //TODO take effects into account that change when lands can be played
        if(!canPlaySorcery(getLand().getOwner())) return false;
        return true;
    }
    
    @Override
    protected boolean isLegalState() {
        Counter c = getLand().getOwner().getCounter(Player.LAND_DROP_COUNTER);
        //TODO take effects into account that change how many lands can be played per turn
        if(c.getCount() > 0) return false;
        if(getLand().getZone() != getLand().getOwner().getHand()) return false;
        return true;
    }
    
    @Override
    protected boolean execute0() {
        CompoundEdit e = new CompoundEdit(getGame(), true, "Play land");
        Counter c = getLand().getOwner().getCounter(Player.LAND_DROP_COUNTER);
        c.increase();
        getLand().setZone(getGame().getBattlefield());
        e.end();
        return true;
    };
    
    @Override
    public String toString() {
        return "Play " + land + " as a land";
    }
}
