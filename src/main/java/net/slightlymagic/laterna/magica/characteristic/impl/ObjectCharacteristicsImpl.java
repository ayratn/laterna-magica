/**
 * ObjectCharacteristicImpl.java
 * 
 * Created on 03.09.2009
 */

package net.slightlymagic.laterna.magica.characteristic.impl;


import static java.lang.String.*;
import static net.slightlymagic.laterna.magica.characteristics.CardType.*;
import static net.slightlymagic.laterna.magica.characteristics.Characteristics.*;
import static net.slightlymagic.laterna.magica.effect.characteristic.SetCharacteristicEffect.Mode.*;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;


import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.ability.Ability;
import net.slightlymagic.laterna.magica.characteristic.CharacteristicSnapshot;
import net.slightlymagic.laterna.magica.characteristic.ObjectCharacteristics;
import net.slightlymagic.laterna.magica.characteristics.CardType;
import net.slightlymagic.laterna.magica.characteristics.MagicColor;
import net.slightlymagic.laterna.magica.characteristics.SubType;
import net.slightlymagic.laterna.magica.characteristics.SuperType;
import net.slightlymagic.laterna.magica.edit.CompoundEdit;
import net.slightlymagic.laterna.magica.edit.property.EditablePropertyChangeSupport;
import net.slightlymagic.laterna.magica.effect.characteristic.CharacteristicEffect;
import net.slightlymagic.laterna.magica.effect.characteristic.OverridingCharacteristicEffect;
import net.slightlymagic.laterna.magica.effect.characteristic.PTEffect;
import net.slightlymagic.laterna.magica.effect.characteristic.SetCharacteristicEffect;
import net.slightlymagic.laterna.magica.effect.characteristic.impl.ColorChangingEffectImpl;
import net.slightlymagic.laterna.magica.effect.characteristic.impl.EffectComparator;
import net.slightlymagic.laterna.magica.impl.AbstractGameContent;
import net.slightlymagic.laterna.magica.mana.ManaSequence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;


/**
 * The class ObjectCharacteristicImpl.
 * 
 * @version V0.0 03.09.2009
 * @author Clemens Koza
 */
public class ObjectCharacteristicsImpl extends AbstractGameContent implements ObjectCharacteristics {
    private static final Logger                        log = LoggerFactory.getLogger(ObjectCharacteristicsImpl.class);
    
    private EditablePropertyChangeSupport              s;
    
    private boolean                                    refreshing;
    private MagicObject                                card;
    private OverridingCharacteristicImpl<String>       name;
    private OverridingCharacteristicImpl<ManaSequence> manaCost;
    private SetCharacteristicImpl<MagicColor>          color;
    private SetCharacteristicImpl<SuperType>           superTypes;
    private SetCharacteristicImpl<CardType>            cardTypes;
    private SetCharacteristicImpl<SubType>             subTypes;
    private SetCharacteristicImpl<Ability>             abilities;
    private PTCharacteristicImpl                       pt;
    
    public ObjectCharacteristicsImpl(MagicObject card) {
        super(card.getGame());
        s = new EditablePropertyChangeSupport(getGame(), this);
        this.card = card;
        
        CompoundEdit e = new CompoundEdit(getGame(), true, "Create " + getCard() + "'s ObjectCharacteristics");
        
        name = new OverridingCharacteristicImpl<String>(getGame(), this, NAME, this + "'s name");
        manaCost = new OverridingCharacteristicImpl<ManaSequence>(getGame(), this, MANA_COST, this
                + "'s mana cost");
        color = new SetCharacteristicImpl<MagicColor>(getGame(), this, COLOR, this + "'s color");
        superTypes = new SetCharacteristicImpl<SuperType>(getGame(), this, SUPERTYPE, this + "'s supertypes");
        cardTypes = new SetCharacteristicImpl<CardType>(getGame(), this, CARD_TYPE, this + "'s card types");
        subTypes = new SetCharacteristicImpl<SubType>(getGame(), this, SUBTYPE, this + "'s subtypes") {
            @Override
            public boolean hasValue(SubType value) {
                if(!value.contained(getTypeCharacteristic())) return false;
                return super.hasValue(value);
            }
            
            @Override
            public boolean isAdding(Set<SubType> result) {
                boolean b = super.isAdding(result);
                if(result != null) {
                    Iterator<SubType> it = result.iterator();
                    while(it.hasNext())
                        if(!it.next().contained(getTypeCharacteristic())) it.remove();
                }
                return b;
            }
        };
        abilities = new SetCharacteristicImpl<Ability>(getGame(), this, ABILITIES, this + "'s abilities");
        pt = new PTCharacteristicImpl(getGame(), this, this + "'s P/T");
        
        e.end();
    }
    
    public EditablePropertyChangeSupport getPropertyChangeSupport() {
        return s;
    }
    
    public MagicObject getCard() {
        return card;
    }
    
    public OverridingCharacteristicImpl<String> getNameCharacteristic() {
        return name;
    };
    
    public OverridingCharacteristicImpl<ManaSequence> getManaCostCharacteristic() {
        return manaCost;
    }
    
    public SetCharacteristicImpl<MagicColor> getColorCharacteristic() {
        return color;
    }
    
    public SetCharacteristicImpl<SuperType> getSuperTypeCharacteristic() {
        return superTypes;
    }
    
    public SetCharacteristicImpl<CardType> getTypeCharacteristic() {
        return cardTypes;
    }
    
    public SetCharacteristicImpl<SubType> getSubTypeCharacteristic() {
        return subTypes;
    }
    
    public SetCharacteristicImpl<Ability> getAbilityCharacteristic() {
        return abilities;
    }
    
    public PTCharacteristicImpl getPTCharacteristic() {
        return pt;
    }
    
    /**
     * Refreshes all the characteristic's values in the case there may have been changes. This is necessarily
     * located here, because the effects have to be applied in their order, and computing characteristics
     * separately would be wrong.
     */
    @SuppressWarnings("unchecked")
    protected synchronized void refresh() {
        //avoid recursive calls from single characteristics
        if(refreshing) {
            log.trace("Refresh called while refreshing");
            return;
        }
        refreshing = true;
        log.trace("Refreshing now...");
        
        CompoundEdit ed = new CompoundEdit(getGame(), true, "Refresh " + getCard() + "'s characteristics");
        //reset all characteristics
        //TODO reset not-yet-implemented characteristics
        getNameCharacteristic().reset();
        getManaCostCharacteristic().reset();
        getColorCharacteristic().reset();
        getSuperTypeCharacteristic().reset();
        getTypeCharacteristic().reset();
        getSubTypeCharacteristic().reset();
//        getExpansionSymbolCharacteristic().reset();
        getPTCharacteristic().reset();
//        getRulesTextCharacteristic().reset();
        getAbilityCharacteristic().reset();
//        getLoyaltyCharacteristic().reset();
        
        //apply effects
        SortedMap<CharacteristicEffect, Predicate<? super MagicObject>> effects = new TreeMap<CharacteristicEffect, Predicate<? super MagicObject>>(
                EffectComparator.INSTANCE);
        for(CharacteristicEffect ef:getCard().getEffects().getEffects())
            effects.put(ef, null);
        effects.putAll(getGame().getGlobalEffects().getEffects());
        

        log.trace("Active effects:");
        for(Entry<CharacteristicEffect, Predicate<? super MagicObject>> e:effects.entrySet()) {
            if(e.getValue() != null && !e.getValue().apply(getCard())) continue;
            CharacteristicEffect ef = e.getKey();
            log.trace(valueOf(ef));
            switch(ef.getCharacteristic()) {
                case NAME:
                    getNameCharacteristic().applyEffect((OverridingCharacteristicEffect<String>) ef);
                break;
                case MANA_COST:
                    getManaCostCharacteristic().applyEffect((OverridingCharacteristicEffect<ManaSequence>) ef);
                    getColorCharacteristic().applyEffect(
                            new ColorChangingEffectImpl(getGame(), SETTING, getManaCost().getColors()));
                break;
                case COLOR:
                    getColorCharacteristic().applyEffect((SetCharacteristicEffect<MagicColor>) ef);
                break;
                case SUPERTYPE:
                    getSuperTypeCharacteristic().applyEffect((SetCharacteristicEffect<SuperType>) ef);
                break;
                case CARD_TYPE:
                    getTypeCharacteristic().applyEffect((SetCharacteristicEffect<CardType>) ef);
                break;
                case SUBTYPE:
                    getSubTypeCharacteristic().applyEffect((SetCharacteristicEffect<SubType>) ef);
                break;
                case EXPANSION_SYMBOL:
                    //TODO implement expansion symbol
                    throw new UnsupportedOperationException("Not yet implemented");
                    //break;
                case RULES_TEXT:
                    //TODO implement rules text
                    throw new UnsupportedOperationException("Not yet implemented");
                    //break;
                case ABILITIES:
                    getAbilityCharacteristic().applyEffect((SetCharacteristicEffect<Ability>) ef);
                break;
                case POWER_TOUGHNESS:
                    //TODO consider counters
                    getPTCharacteristic().applyEffect((PTEffect) ef);
                break;
                case LOYALTY:
                    //TODO implement loyalty
                    throw new UnsupportedOperationException("Not yet implemented");
                    //break;
                default:
                    throw new AssertionError(ef.getCharacteristic());
            }
        }
        //TODO end not-yet-implemented characteristics
        getNameCharacteristic().end();
        getManaCostCharacteristic().end();
        getColorCharacteristic().end();
        getSuperTypeCharacteristic().end();
        getTypeCharacteristic().end();
        getSubTypeCharacteristic().end();
//        getExpansionSymbolCharacteristic().end();
        getPTCharacteristic().end();
//        getRulesTextCharacteristic().end();
        getAbilityCharacteristic().end();
//        getLoyaltyCharacteristic().end();
        
        ed.end();
        log.trace("Refreshing ended");
        refreshing = false;
    }
    
    public synchronized CharacteristicSnapshot getCharacteristics(CharacteristicSnapshot c) {
        if(c == null) return null;
        //refresh once...
        refresh();
        //then set the flag that other calls don't trigger refreshing again
        refreshing = true;
        
        //fill in the values
        c.setCharacteristics(this);
        c.setName(getName());
        c.setManaCost(getManaCost());
        c.setPower(getPower());
        c.setToughness(getToughness());
        //TODO s loyalty
        c.setLoyalty(0);
        
        getColorCharacteristic().getValues(c.getColors());
        getSuperTypeCharacteristic().getValues(c.getSuperTypes());
        getTypeCharacteristic().getValues(c.getTypes());
        getSubTypeCharacteristic().getValues(c.getSubTypes());
        getAbilityCharacteristic().getValues(c.getAbilities());
        
        //reset the flag in the end
        refreshing = false;
        
        c.notifyObservers();
        
        return c;
    }
    
    public String getName() {
        return getNameCharacteristic().getValue();
    }
    
    public ManaSequence getManaCost() {
        return getManaCostCharacteristic().getValue();
    }
    
    public boolean hasColor(MagicColor color) {
        return getColorCharacteristic().hasValue(color);
    }
    
    public boolean hasSuperType(SuperType type) {
        return getSuperTypeCharacteristic().hasValue(type);
    }
    
    public boolean hasType(CardType type) {
        return getTypeCharacteristic().hasValue(type);
    }
    
    public boolean hasSubType(SubType type) {
        return type.contained(getTypeCharacteristic()) && getSubTypeCharacteristic().hasValue(type);
    }
    
    public boolean hasAbility(Ability ability) {
        return getAbilityCharacteristic().hasValue(ability);
    }
    
    public int getPower() {
        return hasType(CREATURE)? getPTCharacteristic().getPower():0;
    }
    
    public int getToughness() {
        return hasType(CREATURE)? getPTCharacteristic().getToughness():0;
    }
}
