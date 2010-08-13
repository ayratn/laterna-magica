/**
 * Subtype.java
 * 
 * Created on 17.07.2009
 */

package net.slightlymagic.laterna.magica.characteristics;


import static net.slightlymagic.laterna.magica.characteristics.CardType.*;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import net.slightlymagic.laterna.magica.characteristic.SetCharacteristic;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;


/**
 * <p>
 * The class Subtype. Though subtypes are, according to the comprehensive rules ({@magic.ruleRef
 * 20100716/R2043}), a closed set of values, that set is very huge and has no meaning to the game other than that
 * it exists: There are no specific rules bound to the subtypes (other than the basic land types and Aura, more on
 * that later). Therefore it's more practical to make that set <i>open</i> by lazily creating subtypes for any
 * requested combination of type and subtype name.
 * </p>
 * <p>
 * This has the benefits of openness for both subtypes introduced in the future and hypothetical subtypes on cards
 * created in the computer game, and it saves memory as only a minority of the many subtypes would be used in a
 * single game.
 * </p>
 * <p>
 * Subtypes with special rules:
 * </p>
 * <ul>
 * <li>
 * The basic land types
 * <p>
 * {@magic.ruleRef 20100716/R3056} The basic land types are Plains, Island, Swamp, Mountain, and
 * Forest. If an object uses the words “basic land type,” it’s referring to one of these subtypes. A basic land
 * type implies an intrinsic ability to produce colored mana. (See rule 605, “Mana Abilities.”) An object with a
 * basic land type is treated as if its text box included “{T}: Add [mana symbol] to your mana pool,” even if the
 * text box doesn’t actually contain text or the object has no text box. Plains produce white mana; Islands, blue;
 * Swamps, black; Mountains, red; and Forests, green.
 * </p>
 * <p>
 * {@magic.ruleRef 20100716/R3057} If an effect changes a land’s subtype to one or more of the
 * basic land types, the land no longer has its old land type. It loses all abilities generated from its rules text
 * and its old land types, and it gains the appropriate mana ability for each new basic land type. Note that this
 * doesn’t remove any abilities that were granted to the land by other effects. Changing a land’s subtype doesn’t
 * add or remove any card types (such as creature) or supertypes (such as basic, legendary, and snow) the land may
 * have. If a land gains one or more land types in addition to its own, it keeps its land types and rules text, and
 * it gains the new land types and mana abilities.
 * </p>
 * </li>
 * <li>
 * Aura
 * <p>
 * {@magic.ruleRef 20100716/R3034a} An Aura spell requires a target, which is restricted by its
 * enchant ability.
 * </p>
 * <p>
 * {@magic.ruleRef 20100716/R3034f} If an Aura is entering the battlefield under a player’s control
 * by any means other than by resolving as an Aura spell, and the effect putting it onto the battlefield doesn’t
 * specify the object or player the Aura will enchant, that player chooses what it will enchant as the Aura enters
 * the battlefield. The player must choose a legal object or player according to the Aura’s enchant ability and any
 * other applicable effects.
 * </p>
 * <p>
 * {@magic.ruleRef 20100716/R3034g} If an Aura is entering the battlefield and there is no legal
 * object or player for it to enchant, the Aura remains in its current zone, unless that zone is the stack. In that
 * case, the Aura is put into its owner’s graveyard instead of entering the battlefield.
 * </p>
 * <p>
 * {@magic.ruleRef 20100716/R3034h} If an effect attempts to attach an Aura on the battlefield to
 * an object or player, that object or player must be able to be enchanted by it. If the object or player can’t be,
 * the Aura doesn’t move.
 * </p>
 * <p>
 * {@magic.ruleRef 20100716/R7025a} Enchant is a static ability, written “Enchant [object or
 * player].” The enchant ability restricts what an Aura spell can target and what an Aura can enchant.
 * </p>
 * <p>
 * {@magic.ruleRef 20100716/R7025c} If an Aura has multiple instances of enchant, all of them
 * apply. The Aura’s target must follow the restrictions from all the instances of enchant. The Aura can enchant
 * only objects or players that match all of its enchant abilities.
 * </p>
 * </li>
 * 
 * @version V0.0 17.07.2009
 * @author Clemens Koza
 */
public class SubType implements Serializable {
    private static final long            serialVersionUID = 1419379860183044413L;
    
    private static Map<SubType, SubType> map;
    
    public static SubType getSubtype(CardType type, String subtype) {
        if(subtype == null || !subtype.matches("\\w+")) throw new IllegalArgumentException("subtype == " + subtype);
        
        subtype = subtype.substring(0, 1).toUpperCase() + subtype.substring(1).toLowerCase();
        
        //look up the shared subtype using an equal subtype
        return getSubType(new SubType(type, subtype));
    }
    
    private static SubType getSubType(SubType subtype) {
        if(map == null) {
            //make a map that maps the subtype to itself. so, the shared subtype instance can be looked up by
            //passing an equal subtype
            //both keys and values are soft, since they are the same objects; just having one soft is pointless
            map = new MapMaker().softKeys().softValues().makeComputingMap(new Function<SubType, SubType>() {
                public SubType apply(SubType from) {
                    return from;
                }
            });
        }
        
        return map.get(subtype);
    }
    
    
    private final CardType type;
    private final String   name;
    
    private SubType(CardType type, String name) {
        this.type = type.normalize();
        this.name = name;
    }
    
    public CardType getType() {
        return type;
    }
    
    public String getName() {
        return name;
    }
    
    /**
     * <p>
     * Returns if the subtype's type is contained in the iterable. This is not equal to
     * {@link Collection#contains(Object)}, because of {@link CardType#normalize() normalization}.
     * </p>
     */
    public boolean contained(Iterable<CardType> types) {
        for(CardType type:types)
            if(type.normalize() == getType()) return true;
        return false;
    }
    
    /**
     * <p>
     * Returns if the subtype's type is contained in the array. This method performs {@link CardType#normalize()
     * normalization}.
     * </p>
     */
    public boolean contained(CardType... types) {
        for(CardType type:types)
            if(type.normalize() == getType()) return true;
        return false;
    }
    
    /**
     * <p>
     * Returns if the subtype's type is contained in the characteristic. This is not equal to
     * {@link SetCharacteristic#hasValue(Object)}, because of {@link CardType#normalize() normalization}.
     * </p>
     */
    public boolean contained(SetCharacteristic<CardType> types) {
        if(types.hasValue(getType())) return true;
        if(getType() == CREATURE && types.hasValue(TRIBAL)) return true;
        if(getType() == SORCERY && types.hasValue(INSTANT)) return true;
        return false;
    }
    
    @Override
    public String toString() {
        return getName();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null)? 0:name.hashCode());
        result = prime * result + ((type == null)? 0:type.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null) return false;
        if(getClass() != obj.getClass()) return false;
        SubType other = (SubType) obj;
        if(name == null) {
            if(other.name != null) return false;
        } else if(!name.equals(other.name)) return false;
        if(type == null) {
            if(other.type != null) return false;
        } else if(!type.equals(other.type)) return false;
        return true;
    }
    
    /**
     * When deserializing, replace with a local cached instance
     */
    private Object readResolve() throws ObjectStreamException {
        return getSubType(this);
    }
}
