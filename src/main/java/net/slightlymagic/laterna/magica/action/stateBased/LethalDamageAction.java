/**
 * LethalDamageAction.java
 * 
 * Created on 12.07.2010
 */

package net.slightlymagic.laterna.magica.action.stateBased;


import static com.google.common.base.Predicates.*;
import static com.google.common.base.Suppliers.*;
import static net.slightlymagic.laterna.magica.characteristics.CardType.*;
import static net.slightlymagic.laterna.magica.util.MagicaPredicates.*;
import static net.slightlymagic.laterna.magica.zone.Zone.Zones.*;

import java.util.ArrayList;
import java.util.List;

import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.action.AbstractGameAction;
import net.slightlymagic.laterna.magica.card.CardObject;
import net.slightlymagic.laterna.magica.edit.CompoundEdit;
import net.slightlymagic.laterna.magica.effects.DestroyPermanentEvent;

import com.google.common.base.Predicate;


/**
 * The class LethalDamageAction.
 * 
 * @version V0.0 12.07.2010
 * @author Clemens Koza
 */
public class LethalDamageAction extends AbstractGameAction implements StateBasedAction {
    private static final Predicate<MagicObject> isCreaturePermanent = and(isIn(ofInstance(BATTLEFIELD)),
                                                                            card(or(CREATURE)));
    
    public LethalDamageAction(Game game) {
        super(game);
    }
    
    @Override
    public boolean execute() {
        List<CardObject> ob = new ArrayList<CardObject>();
        
        for(MagicObject o:getGame().getBattlefield().getCards()) {
            if(!isCreaturePermanent.apply(o)) continue;
            CardObject co = (CardObject) o;
            if(co.getMarkedDamage() >= co.getCharacteristics().get(0).getToughness()) ob.add(co);
        }
        
        CompoundEdit ed = new CompoundEdit(getGame(), true, "Destroy matching permanents");
        for(CardObject co:ob) {
            new DestroyPermanentEvent(co).execute();
        }
        ed.end();
        
        return !ob.isEmpty();
    }
}
