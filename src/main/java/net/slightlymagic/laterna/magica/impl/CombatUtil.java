/**
 * CombatUtil.java
 * 
 * Created on 23.08.2010
 */

package net.slightlymagic.laterna.magica.impl;


import static com.google.common.base.Predicates.*;
import static com.google.common.base.Suppliers.*;
import static net.slightlymagic.laterna.magica.card.State.StateType.*;
import static net.slightlymagic.laterna.magica.characteristics.CardType.*;
import static net.slightlymagic.laterna.magica.util.MagicaFunctions.*;
import static net.slightlymagic.laterna.magica.util.MagicaPredicates.*;
import static net.slightlymagic.laterna.magica.zone.Zone.Zones.*;
import net.slightlymagic.laterna.magica.Combat.Attacker;
import net.slightlymagic.laterna.magica.Combat.BlockAssignment;
import net.slightlymagic.laterna.magica.Combat.Blocker;
import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.card.CardObject;
import net.slightlymagic.laterna.magica.player.Player;

import com.google.common.base.Predicate;


/**
 * The class CombatUtil.
 * 
 * @version V0.0 23.08.2010
 * @author Clemens Koza
 */
public class CombatUtil {
    private static final Predicate<Player>      active           = new Predicate<Player>() {
                                                                     @Override
                                                                     public boolean apply(Player input) {
                                                                         return input == input.getGame().getTurnStructure().getActivePlayer();
                                                                     }
                                                                 };
    
    private static final Predicate<MagicObject> permanent        = isIn(ofInstance(BATTLEFIELD));
    private static final Predicate<MagicObject> untapped         = not(is(TAPPED));
    private static final Predicate<MagicObject> creature         = and(permanent, card(has(CREATURE)));
    private static final Predicate<MagicObject> planeswalker     = and(permanent, card(has(PLANESWALKER)));
    private static final Predicate<CardObject>  untappedCreature = and(creature, untapped);
    

    //TODO consider teams
    //no problem, since the player will provide a Game as context to determine teams
    private static final Predicate<Player>      legalDefPl       = not(active);
    private static final Predicate<CardObject>  legalDefPw       = and(planeswalker,
                                                                         compose(legalDefPl, controller));
    @SuppressWarnings("unchecked")
    private static final Predicate<CardObject>  legalAttacker    = and(untappedCreature, not(summoningSick),
                                                                         compose(active, controller));
    private static final Predicate<CardObject>  legalBlocker     = and(untappedCreature,
                                                                         compose(legalDefPl, controller));
    
    public static boolean isLegalDefendingPlayer(Player input) {
        return legalDefPl.apply(input);
    }
    
    public static boolean isLegalDefendingPlaneswalker(CardObject input) {
        return legalDefPw.apply(input);
    }
    
    public static boolean isLegalAttacker(CardObject input) {
        return legalAttacker.apply(input);
    }
    
    public static boolean isLegalBlocker(CardObject input) {
        return legalBlocker.apply(input);
    }
    
    /**
     * Returns whether the creature deals, depending on {@code first}, first strike or regular damage.
     */
    public static boolean dealsDamage(Attacker a, boolean first) {
        //TODO check first & double strike
        boolean firstStrike = false, doubleStrike = false;
        
        return (first == firstStrike) || doubleStrike;
    }
    
    /**
     * Returns whether the creature deals, depending on {@code first}, first strike or regular damage.
     */
    public static boolean dealsDamage(Blocker b, boolean first) {
        //TODO check first & double strike
        boolean firstStrike = false, doubleStrike = false;
        
        return (first == firstStrike) || doubleStrike;
    }
    
    /**
     * Returns the amount of damage dealt by the creature
     */
    public static int getAmmount(Attacker a) {
        //TODO implement properly
        return a.getAttacker().getCharacteristics().get(0).getPower();
    }
    
    /**
     * Returns the amount of damage dealt by the creature
     */
    public static int getAmmount(Blocker b) {
        //TODO implement properly
        return b.getBlocker().getCharacteristics().get(0).getPower();
    }
    
    /**
     * Returns whether the creature was assigned lethal damage
     */
    public static boolean isLethal(Attacker a) {
        //TODO implement properly
        int toughness = a.getAttacker().getCharacteristics().get(0).getToughness();
        for(BlockAssignment b:a.getBlockers().values())
            toughness -= b.getBlockerAssignedDamage();
        return toughness <= 0;
    }
    
    /**
     * Returns whether the creature was assigned lethal damage
     */
    public static boolean isLethal(Blocker b) {
        //TODO implement properly
        int toughness = b.getBlocker().getCharacteristics().get(0).getToughness();
        for(BlockAssignment a:b.getAttackers().values())
            toughness -= a.getAttackerAssignedDamage();
        return toughness <= 0;
    }
    
    /**
     * Returns whether the attacker can assign excess damage to the defender rather than its blockers
     */
    public static boolean hasTrample(Attacker a) {
        //TODO check trample
        return false;
    }
}
