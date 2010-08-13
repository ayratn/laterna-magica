/**
 * CardParts.java
 * 
 * Created on 23.07.2009
 */

package net.slightlymagic.laterna.magica.card;


import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

import net.slightlymagic.laterna.magica.ability.Ability;
import net.slightlymagic.laterna.magica.action.play.CastAction;
import net.slightlymagic.laterna.magica.action.play.PlayInformation;
import net.slightlymagic.laterna.magica.characteristic.CharacteristicSnapshot;
import net.slightlymagic.laterna.magica.characteristics.CardType;
import net.slightlymagic.laterna.magica.characteristics.MagicColor;
import net.slightlymagic.laterna.magica.characteristics.SubType;
import net.slightlymagic.laterna.magica.characteristics.SuperType;
import net.slightlymagic.laterna.magica.mana.ManaSequence;


/**
 * <p>
 * The class CardParts. CardParts represents the unmodifiable data that
 * <ul>
 * <li>is printed on a card</li>
 * <li>a token acquired through the effect creating it or</li>
 * <li>a copy inherited from the object it copies</li>
 * </ul>
 * </p>
 * <p>
 * See rule {@magic.ruleRef 20100716/R2001}.
 * </p>
 * 
 * <p>
 * All setting operations are optional, and may throw {@link UnsupportedOperationException}s.
 * </p>
 * 
 * @version V0.0 23.07.2009
 * @author Clemens Koza
 */
public interface CardParts extends Serializable {
    /**
     * Fills a characteristic snapshot with the current values from this card parts. This method enables a uniform
     * way for showing cards independent of if it's a game (showing a {@link CardObject}) or not (showing a
     * {@link CardTemplate}).
     * 
     * @param c The snapshot to fill in. If {@code null}, nothing is done and {@code null} is returned.
     * @return The input snapshot
     */
    public CharacteristicSnapshot getCharacteristics(CharacteristicSnapshot c);
    
    public void setName(String name);
    
    public String getName();
    
    
    public void setManaCost(ManaSequence manaCost);
    
    public ManaSequence getManaCost();
    
    
    /**
     * <p>
     * Sets the colors from the mana cost
     * </p>
     * 
     * @throws UnsupportedOperationException
     */
    public void takeColors();
    
    public void setColors(MagicColor... types);
    
    public void setColors(Collection<MagicColor> types);
    
    public Set<MagicColor> getColors();
    
    
    public void setSuperTypes(SuperType... types);
    
    public void setSuperTypes(Collection<SuperType> types);
    
    public Set<SuperType> getSuperTypes();
    
    
    public void setTypes(CardType... types);
    
    public void setTypes(Collection<CardType> types);
    
    public Set<CardType> getTypes();
    
    
    public void setSubTypes(SubType... types);
    
    public void setSubTypes(Collection<SubType> types);
    
    public Set<SubType> getSubTypes();
    
    
    public void setAbilities(Ability... abilities);
    
    public void setAbilities(Collection<Ability> abilities);
    
    public Set<Ability> getAbilities();
    
    
    public void setPower(int power);
    
    public int getPower();
    
    
    public void setToughness(int toughness);
    
    public int getToughness();
    
    
    public void setLoyalty(int loyalty);
    
    public int getLoyalty();
    
    
    /**
     * Returns if casting the spell using the specified CastAction is legal.
     */
    public boolean isLegal(CastAction a);
    
    /**
     * Returns the PlayInformation for executing the specified CastAction and resolving the resulting spell.
     */
    public PlayInformation getPlayInformation(CastAction a);
}
