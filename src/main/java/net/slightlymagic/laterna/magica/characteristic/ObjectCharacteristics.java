/**
 * ObjectCharacteristics.java
 * 
 * Created on 11.07.2009
 */

package net.slightlymagic.laterna.magica.characteristic;


import net.slightlymagic.laterna.magica.GameContent;
import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.ability.Ability;
import net.slightlymagic.laterna.magica.characteristics.CardType;
import net.slightlymagic.laterna.magica.characteristics.MagicColor;
import net.slightlymagic.laterna.magica.characteristics.SubType;
import net.slightlymagic.laterna.magica.characteristics.SuperType;
import net.slightlymagic.laterna.magica.mana.ManaSequence;


/**
 * TODO rewrite ObjectCharacteristics' class JavaDoc
 * <p>
 * The class ObjectCharacteristics. ObjectCharacteristics saves all characteristics of an object.
 * </p>
 * <p>
 * {@magic.ruleRef 20100716/R1093} An object's characteristics are name, mana cost, color, card
 * type, subtype, supertype, expansion symbol, rules text, abilities, power, toughness, and loyalty. Objects can
 * have some or all of these characteristics. Any other information about an object isn't a characteristic. For
 * example, characteristics don't include whether a permanent is tapped, a spell's target, an object's owner or
 * controller, what an Aura enchants, and so on.
 * </p>
 * <p>
 * All these may be modified by effects, see also {@magic.ruleRef 20100716/R611} and
 * {@magic.ruleRef 20100716/R613}, {@magic.ruleRef 20100716/R6043}. The layers of
 * applying effects are:
 * </p>
 * <ol>
 * <li>Copy effects. See {@magic.ruleRef 20100716/R706}.</li>
 * <li>Control-changing effects. <i>(not applied here)</i></li>
 * <li>Text-changing effects. See {@magic.ruleRef 20100716/R612}.</li>
 * <li>Type-changing effects.</li>
 * <li>Color-changing effects.</li>
 * <li>Ability-adding and -removing effects.</li>
 * <li>P/T-Changing effects:
 * <ol type="a">
 * <li>Effects from characteristic-defining abilities.</li>
 * <li>P/T-setting effects.</li>
 * <li>P/T-changing effects.</li>
 * <li>P/T-changes from counters. See {@magic.ruleRef 20100716/R121}. <i>(applied, but not stored
 * here)</i></li>
 * <li>P/T-switching effects.</li>
 * </ol>
 * </li>
 * </ol>
 * 
 * @version V0.0 11.07.2009
 * @author Clemens Koza
 */
public interface ObjectCharacteristics extends GameContent {
    /**
     * <p>
     * Returns the card object to which this characteristics belongs.
     * </p>
     */
    public MagicObject getCard();
    
    /**
     * Fills a characteristic snapshot with the current values from this characteristics. This is often better in
     * performance than getting the values directly from this characteristics, because the characteristics
     * implementation can optimize on the fact that the state won't change between reading individual
     * characteristic values.
     * 
     * @param c The snapshot to fill in. If {@code null}, nothing is done and {@code null} is returned.
     * @return The input snapshot
     */
    public CharacteristicSnapshot getCharacteristics(CharacteristicSnapshot c);
    
    /**
     * <p>
     * Returns the name of this Characteristics.
     * </p>
     */
    public String getName();
    
    /**
     * <p>
     * Returns the Characteristic used to store the name.
     * </p>
     */
    public OverridingCharacteristic<String> getNameCharacteristic();
    
    
    /**
     * <p>
     * Returns the mana cost of this Characteristics.
     * </p>
     */
    public ManaSequence getManaCost();
    
    /**
     * <p>
     * Returns the Characteristic used to store the mana cost.
     * </p>
     */
    public OverridingCharacteristic<ManaSequence> getManaCostCharacteristic();
    
    
    /**
     * <p>
     * Returns if the characteristics contains a color.
     * </p>
     */
    public boolean hasColor(MagicColor color);
    
    /**
     * <p>
     * Returns the Characteristic used to store the colors.
     * </p>
     */
    public SetCharacteristic<MagicColor> getColorCharacteristic();
    
    
    /**
     * <p>
     * Returns if the characteristics contains a type.
     * </p>
     */
    public boolean hasSuperType(SuperType type);
    
    /**
     * <p>
     * Returns the Characteristic used to store the card types.
     * </p>
     */
    public SetCharacteristic<SuperType> getSuperTypeCharacteristic();
    
    
    /**
     * <p>
     * Returns if the characteristics contains a type.
     * </p>
     */
    public boolean hasType(CardType type);
    
    /**
     * <p>
     * Returns the Characteristic used to store the card types.
     * </p>
     */
    public SetCharacteristic<CardType> getTypeCharacteristic();
    
    
    /**
     * <p>
     * Returns if the characteristics contains a type.
     * </p>
     */
    public boolean hasSubType(SubType type);
    
    /**
     * <p>
     * Returns the Characteristic used to store the card types.
     * </p>
     */
    public SetCharacteristic<SubType> getSubTypeCharacteristic();
    
    
    /**
     * <p>
     * Returns if the characteristics contains an ability.
     * </p>
     */
    public boolean hasAbility(Ability ability);
    
    /**
     * <p>
     * Returns the Characteristic used to store the card types.
     * </p>
     */
    public SetCharacteristic<Ability> getAbilityCharacteristic();
    
    
    /**
     * <p>
     * Returns the characteristic's value for power.
     * </p>
     */
    public int getPower();
    
    /**
     * <p>
     * Returns the characteristic's value for toughness.
     * </p>
     */
    public int getToughness();
    
    /**
     * <p>
     * Returns the characteristic used to store power & toughness.
     * </p>
     */
    public PTCharacteristic getPTCharacteristic();
    
    //TODO loyalty
}
