/**
 * CardObjectImpl.java
 * 
 * Created on 21.04.2010
 */

package net.slightlymagic.laterna.magica.card.impl;


import static com.google.common.base.Suppliers.*;
import static java.lang.Integer.*;
import static net.slightlymagic.laterna.magica.util.MagicaPredicates.*;
import static net.slightlymagic.laterna.magica.zone.Zone.Zones.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.slightlymagic.beans.properties.Property;
import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.action.play.CastAction;
import net.slightlymagic.laterna.magica.action.play.PlayAction;
import net.slightlymagic.laterna.magica.action.play.PlayInformation;
import net.slightlymagic.laterna.magica.card.CardObject;
import net.slightlymagic.laterna.magica.card.CardParts;
import net.slightlymagic.laterna.magica.card.CardTemplate;
import net.slightlymagic.laterna.magica.card.Printing;
import net.slightlymagic.laterna.magica.card.State;
import net.slightlymagic.laterna.magica.characteristic.CardCharacteristics;
import net.slightlymagic.laterna.magica.characteristic.impl.CardCharacteristicsImpl;
import net.slightlymagic.laterna.magica.event.MoveCardListener;
import net.slightlymagic.laterna.magica.impl.MagicObjectImpl;
import net.slightlymagic.laterna.magica.impl.MoveCardEvent;
import net.slightlymagic.laterna.magica.zone.Zone.Zones;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;


/**
 * The class CardObjectImpl.
 * 
 * @version V0.0 21.04.2010
 * @author Clemens Koza
 */
public class CardObjectImpl extends MagicObjectImpl implements CardObject {
    private static final Predicate<MagicObject> isPermanent = isIn(ofInstance(BATTLEFIELD));
    
    private List<CardCharacteristics>           characteristics, activeCharacteristics, activeCharacteristicsView;
    private List<CardParts>                     activePartsView;
    private State                               state;
    
    private Printing                            printing;
    
    private Property<PlayInformation>           info;
    private Property<Integer>                   damage;
    
    public CardObjectImpl(Game game, CardTemplate template) {
        this(game, template, null);
    }
    
    public CardObjectImpl(Game game, Printing printing) {
        this(game, printing.getTemplate(), printing);
    }
    
    private CardObjectImpl(Game game, CardTemplate template, Printing printing) {
        super(game);
        init(template);
        addMoveCardListener(new MoveCard());
        
        List<Printing> printings = template.getPrintings();
        if(printings.isEmpty()) log.warn(template + ": No printings available");
        else if(printings.contains(printing)) this.printing = printing;
        else this.printing = printings.get((int) (Math.random() * printings.size()));
        
        for(CardParts part:getTemplate().getCardParts())
            characteristics.add(new CardCharacteristicsImpl(this, part));
        
        switch(template.getType()) {
            case NORMAL:
                //the only part is active
                assert characteristics.size() == 1;
                activeCharacteristics.addAll(characteristics);
            break;
            case SPLIT:
                //all split parts are active
                activeCharacteristics.addAll(characteristics);
            break;
            case FLIP:
                //the unflipped side is active
                activeCharacteristics.add(characteristics.get(0));
            break;
        }
    }
    
    protected void init(CardTemplate template) {
        info = properties.property("info");
        damage = properties.property("damage", 0);
        
        characteristics = new ArrayList<CardCharacteristics>();
        activeCharacteristics = new ArrayList<CardCharacteristics>();
        activeCharacteristicsView = Collections.unmodifiableList(activeCharacteristics);
        activePartsView = Lists.transform(activeCharacteristicsView,
                new Function<CardCharacteristics, CardParts>() {
                    
                    public CardParts apply(CardCharacteristics from) {
                        return from.getParts();
                    }
                });
    }
    
    public CardTemplate getTemplate() {
        return getPrinting().getTemplate();
    }
    
    
    public List<? extends CardParts> getActiveCardParts() {
        return activePartsView;
    }
    
    
    public List<? extends CardCharacteristics> getCharacteristics() {
        return activeCharacteristicsView;
    }
    
    
    public State getState() {
        return state;
    }
    
    public Printing getPrinting() {
        return printing;
    }
    
    
    @Override
    public String toString() {
        return getTemplate() + "@" + toHexString(hashCode());
    }
    
    
    public boolean isLegal(PlayAction a) {
        if(!(a instanceof CastAction)) {
            throw new IllegalArgumentException("A spell can only be cast using a CastAction");
        }
        return getCharacteristics().get(0).getParts().isLegal((CastAction) a);
    }
    
    
    public void play(PlayAction a) {
        if(!(a instanceof CastAction)) {
            throw new IllegalArgumentException("A spell can only be cast using a CastAction");
        }
        //Create the info before changing the zones so that listeners see the PlayInformation
        info.setValue(getCharacteristics().get(0).getParts().getPlayInformation((CastAction) a));
        
        setZone(getGame().getStack());
    }
    
    
    public PlayInformation getPlayInformation() {
        PlayInformation info = this.info.getValue();
        if(info == null) throw new IllegalStateException("Card is not on the stack");
        return info;
    }
    
    
    public void resetPlayInformation() {
        info.setValue(null);
    }
    
    
    public void setMarkedDamage(int damage) {
        if(!isPermanent.apply(this)) throw new IllegalStateException();
        this.damage.setValue(damage);
    }
    
    
    public void resetMarkedDamage() {
        if(!isPermanent.apply(this)) return;
        this.damage.setValue(0);
    }
    
    
    public int getMarkedDamage() {
        if(!isPermanent.apply(this)) throw new IllegalStateException();
        return damage.getValue();
    }
    
    private class MoveCard implements MoveCardListener.Internal {
        public void cardMoved(MoveCardEvent ev) {
            state = ev.getTo().getType() == Zones.BATTLEFIELD? new StateImpl(CardObjectImpl.this):null;
        }
    }
}
