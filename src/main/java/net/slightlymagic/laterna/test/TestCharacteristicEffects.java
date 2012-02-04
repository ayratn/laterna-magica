/**
 * TestCharacteristicEffects.java
 * 
 * Created on 24.03.2010
 */

package net.slightlymagic.laterna.test;


import static java.util.Collections.*;
import static net.slightlymagic.laterna.magica.card.impl.CardObjectImpl.*;
import static net.slightlymagic.laterna.magica.characteristic.CardType.*;
import static net.slightlymagic.laterna.magica.characteristic.Characteristics.*;
import static net.slightlymagic.laterna.magica.characteristic.MagicColor.*;
import static net.slightlymagic.laterna.magica.effect.ContinuousEffect.Layer.*;
import static net.slightlymagic.laterna.magica.effect.characteristic.SetCharacteristicEffect.Mode.*;
import static net.slightlymagic.laterna.magica.effect.characteristic.impl.ColorChangingEffectImpl.*;
import static net.slightlymagic.laterna.magica.effect.characteristic.impl.OverridingCharacteristicEffectImpl.*;
import static net.slightlymagic.laterna.magica.effect.characteristic.impl.PTChangingEffectImpl.*;
import static net.slightlymagic.laterna.magica.effect.characteristic.impl.PTSwitchingEffectImpl.*;
import static net.slightlymagic.laterna.magica.effect.characteristic.impl.TypeChangingEffectImpl.*;
import static net.slightlymagic.laterna.magica.impl.GameImpl.*;
import static net.slightlymagic.laterna.magica.mana.ManaFactory.*;
import static net.slightlymagic.laterna.magica.util.MagicaPredicates.*;

import java.util.UUID;

import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.LaternaMagica;
import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.card.CardObject;
import net.slightlymagic.laterna.magica.characteristic.CardSnapshot;
import net.slightlymagic.laterna.magica.characteristic.ObjectCharacteristics;
import net.slightlymagic.laterna.magica.effect.characteristic.CharacteristicEffect;
import net.slightlymagic.objectTransactions.History;

import com.google.common.base.Predicate;


/**
 * The class TestCharacteristicEffects.
 * 
 * @version V0.0 24.03.2010
 * @author Clemens Koza
 */
public class TestCharacteristicEffects {
    public static void main(String[] args) throws Exception {
        LaternaMagica.init();
        
        History h = History.createHistory(UUID.randomUUID());
        h.pushHistoryForThread();
        try {
            final Game g = newGameImpl();
            Predicate<MagicObject> m1 = card(has(ENCHANTMENT));
            //all enchantments are green
            g.getGlobalEffects().put(newColorChangingEffectImpl(ADDING, singleton(GREEN)), m1);
            
            
            CardObject card = newCardObjectImpl(LaternaMagica.CARDS().getCard("Llanowar Elves"));
            
            
            //switch power and toughness
            CharacteristicEffect e1 = newPTSwitchingEffectImpl();
            //becomes 0/2
            CharacteristicEffect e2 = newPTChangingEffectImpl(0, 2);
            //is an artifact in addition to its other types
            CharacteristicEffect e3 = newTypeChangingEffectImpl(ADDING, singleton(ARTIFACT));
            //is an enchantment
            CharacteristicEffect e4 = newTypeChangingEffectImpl(SETTING, singleton(ENCHANTMENT));
            //mana cost is {R/W}
            CharacteristicEffect e5 = newOverridingCharacteristicEffectImpl(L3, MANA_COST,
                    ManaFactory.parseSequence("{R/W}"));
            
            ObjectCharacteristics ch = card.getCharacteristics().get(0);
            CardSnapshot cs = null;
            
            print(ch.getCharacteristics(cs));
            
            card.getEffects().add(e1);
            card.getEffects().add(e2);
            card.getEffects().add(e3);
            card.getEffects().add(e4);
            card.getEffects().add(e5);
            
            print(ch.getCharacteristics(cs));
//            System.out.println(g.getGameState());
        } finally {
            h.popHistoryForThread();
        }
    }
    
    public static void print(ObjectCharacteristics c) {
        System.out.printf("%s - %s%n", c.getName(), c.getManaCost());
        System.out.println(c.getColorCharacteristic().valueString());
        System.out.printf("%s %s - %s%n", c.getSuperTypeCharacteristic().valueString(),
                c.getTypeCharacteristic().valueString(), c.getSubTypeCharacteristic().valueString());
        System.out.println(c.getAbilityCharacteristic().valueString());
        System.out.printf("%d/%d%n", c.getPower(), c.getToughness());
    }
    
    public static void print(CardSnapshot c) {
        System.out.printf("%s - %s%n", c.getName(), c.getManaCost());
        System.out.println(c.getColors().valueString());
        System.out.printf("%s %s - %s%n", c.getSuperTypes().valueString(), c.getTypes().valueString(),
                c.getSubTypes().valueString());
        System.out.println(c.getAbilities().valueString());
        System.out.printf("%d/%d%n", c.getPower(), c.getToughness());
    }
}
