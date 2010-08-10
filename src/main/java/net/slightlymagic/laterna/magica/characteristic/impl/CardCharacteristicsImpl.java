/**
 * CardCharacteristicsImpl.java
 * 
 * Created on 21.04.2010
 */

package net.slightlymagic.laterna.magica.characteristic.impl;


import static java.lang.Integer.*;
import static net.slightlymagic.laterna.magica.characteristics.Characteristics.*;
import static net.slightlymagic.laterna.magica.effect.characteristic.SetCharacteristicEffect.Mode.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.ability.Ability;
import net.slightlymagic.laterna.magica.card.CardParts;
import net.slightlymagic.laterna.magica.characteristic.CardCharacteristics;
import net.slightlymagic.laterna.magica.characteristics.CardType;
import net.slightlymagic.laterna.magica.characteristics.MagicColor;
import net.slightlymagic.laterna.magica.characteristics.SubType;
import net.slightlymagic.laterna.magica.characteristics.SuperType;
import net.slightlymagic.laterna.magica.effect.characteristic.abs.AbstractAbilityChangingEffect;
import net.slightlymagic.laterna.magica.effect.characteristic.abs.AbstractColorChangingEffect;
import net.slightlymagic.laterna.magica.effect.characteristic.abs.AbstractOverridingCharacteristicEffect;
import net.slightlymagic.laterna.magica.effect.characteristic.abs.AbstractPTSettingEffect;
import net.slightlymagic.laterna.magica.effect.characteristic.abs.AbstractSubTypeChangingEffect;
import net.slightlymagic.laterna.magica.effect.characteristic.abs.AbstractSuperTypeChangingEffect;
import net.slightlymagic.laterna.magica.effect.characteristic.abs.AbstractTypeChangingEffect;
import net.slightlymagic.laterna.magica.mana.ManaSequence;



/**
 * The class CardCharacteristicsImpl.
 * 
 * @version V0.0 21.04.2010
 * @author Clemens Koza
 */
public class CardCharacteristicsImpl extends ObjectCharacteristicsImpl implements CardCharacteristics {
    private CardParts parts;
    
    public CardCharacteristicsImpl(MagicObject card, CardParts parts) {
        super(card);
        this.parts = parts;
        getNameCharacteristic().setFirst(
                new AbstractOverridingCharacteristicEffect<String>(getGame(), null, NAME) {
                    public String getOverridingValue() {
                        return getParts().getName();
                    }
                    
                    @Override
                    public String toString() {
                        return "inherited name of card parts";
                    }
                });
        getManaCostCharacteristic().setFirst(
                new AbstractOverridingCharacteristicEffect<ManaSequence>(getGame(), null, MANA_COST) {
                    @Override
                    public Layer getLayer() {
                        return null;
                    }
                    
                    public ManaSequence getOverridingValue() {
                        return getParts().getManaCost();
                    }
                    
                    @Override
                    public String toString() {
                        return "inherited mana cost of card parts";
                    }
                });
        getColorCharacteristic().setFirst(new AbstractColorChangingEffect(getGame(), SETTING) {
            @Override
            public Layer getLayer() {
                return null;
            }
            
            public Set<MagicColor> getSetValues() {
                return getManaCost().getColors();
            }
            
            @Override
            public String toString() {
                return "inherited color of card parts";
            }
        });
        getSuperTypeCharacteristic().setFirst(new AbstractSuperTypeChangingEffect(getGame(), SETTING) {
            @Override
            public Layer getLayer() {
                return null;
            }
            
            public Set<SuperType> getSetValues() {
                return getParts().getSuperTypes();
            }
            
            @Override
            public String toString() {
                return "inherited supertypes of card parts";
            }
        });
        getTypeCharacteristic().setFirst(new AbstractTypeChangingEffect(getGame(), SETTING) {
            @Override
            public Layer getLayer() {
                return null;
            }
            
            public Set<CardType> getSetValues() {
                return getParts().getTypes();
            }
            
            @Override
            public String toString() {
                return "inherited types of card parts";
            }
        });
        getSubTypeCharacteristic().setFirst(new AbstractSubTypeChangingEffect(getGame(), SETTING) {
            @Override
            public Layer getLayer() {
                return null;
            }
            
            public Set<SubType> getSetValues() {
                Set<SubType> result = new HashSet<SubType>(getParts().getSubTypes());
                for(Iterator<SubType> it = result.iterator(); it.hasNext();) {
                    if(!it.next().contained(getTypeCharacteristic())) it.remove();
                }
                return result;
            }
            
            @Override
            public String toString() {
                return "inherited subtypes of card parts";
            }
        });
        getAbilityCharacteristic().setFirst(new AbstractAbilityChangingEffect(getGame(), SETTING) {
            @Override
            public Layer getLayer() {
                return null;
            }
            
            public Set<Ability> getSetValues() {
                return getParts().getAbilities();
            }
            
            @Override
            public String toString() {
                return "inherited abilities of card parts";
            }
        });
        getPTCharacteristic().setFirst(new AbstractPTSettingEffect(getGame()) {
            @Override
            public Layer getLayer() {
                return null;
            }
            
            @Override
            public boolean affectsPower() {
                return true;
            }
            
            @Override
            public int getPower() {
                return getParts().getPower();
            }
            
            @Override
            public boolean affectsToughness() {
                return true;
            }
            
            @Override
            public int getToughness() {
                return getParts().getToughness();
            }
            
            @Override
            public String toString() {
                return "inherited P/T of card parts";
            }
        });
    }
    
    public CardParts getParts() {
        return parts;
    }
    
    @Override
    public String toString() {
        return getParts() + "@" + toHexString(getCard().hashCode());
    }
}
