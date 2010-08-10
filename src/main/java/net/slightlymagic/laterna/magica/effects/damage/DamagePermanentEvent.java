/**
 * DamagePermanentEvent.java
 * 
 * Created on 12.07.2010
 */

package net.slightlymagic.laterna.magica.effects.damage;


import static com.google.common.base.Predicates.*;
import static com.google.common.base.Suppliers.*;
import static net.slightlymagic.laterna.magica.characteristics.CardType.*;
import static net.slightlymagic.laterna.magica.util.MagicaPredicates.*;
import static net.slightlymagic.laterna.magica.zone.Zone.Zones.*;
import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.card.CardObject;
import net.slightlymagic.laterna.magica.edit.CompoundEdit;

import com.google.common.base.Predicate;


/**
 * The class DamagePermanentEvent.
 * 
 * @version V0.0 12.07.2010
 * @author Clemens Koza
 */
public class DamagePermanentEvent extends DamageEvent {
    private static final Predicate<MagicObject> legalAffected  = and(isIn(ofInstance(BATTLEFIELD)),
                                                                       card(or(CREATURE, PLANESWALKER)));
    
    private static final Predicate<MagicObject> isPlaneswalker = card(has(PLANESWALKER));
    //TODO implement wither and lifelink
    private static final Predicate<MagicObject> hasWither      = alwaysFalse();
    private static final Predicate<MagicObject> hasLifelink    = alwaysFalse();
    
    private CardObject                          permanent;
    
    public DamagePermanentEvent(CardObject affected, MagicObject source, int ammount, boolean combat, boolean preventable) {
        super(affected, source, ammount, combat, preventable);
        if(!legalAffected.apply(affected)) throw new IllegalArgumentException(
                "affected must be a creature or planeswalker permanent: " + affected);
        permanent = affected;
    }
    
    public CardObject getPermanent() {
        return permanent;
    }
    
    @Override
    protected boolean execute0() {
        CompoundEdit ed = new CompoundEdit(getGame(), true, "Deal damage");
        
        //118.3b. Damage dealt to a planeswalker causes that many loyalty counters to be removed from that
        // planeswalker.
        if(isPlaneswalker.apply(getPermanent())) {
            //TODO remove counters
        }
        //118.3c. Damage dealt to a creature by a source with wither causes that many -1/-1 counters to be put on
        // that creature.
        else if(hasWither.apply(getSource())) {
            //TODO add counters
        }
        //118.3d. Damage dealt to a creature by a source without wither causes that much damage to be marked on
        // that creature.
        else {
            getPermanent().setMarkedDamage(getPermanent().getMarkedDamage() + getAmmount());
        }
        //118.3e. Damage dealt to an object or player by a source with lifelink causes that source's controller to
        // gain that much life, in addition to the damage's other results.
        if(hasLifelink.apply(getSource())) {
            getSource().getController().getLifeTotal().gainLife(getAmmount());
        }
        
        ed.end();
        
        return true;
    }
}
