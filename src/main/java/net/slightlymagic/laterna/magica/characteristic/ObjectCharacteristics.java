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
import net.slightlymagic.laterna.magica.edit.property.EditablePropertyChangeSupport;
import net.slightlymagic.laterna.magica.mana.ManaSequence;


/**
 * TODO rewrite ObjectCharacteristics' class JavaDoc
 * <p>
 * The class ObjectCharacteristics. ObjectCharacteristics saves all characteristics of an object.
 * </p>
 * <p>
 * {@magic.ruleRef 109.3 109.3} An object's characteristics are name, mana cost, color, card type,
 * subtype, supertype, expansion symbol, rules text, abilities, power, toughness, and loyalty. Objects can have
 * some or all of these characteristics. Any other information about an object isn't a characteristic. For example,
 * characteristics don't include whether a permanent is tapped, a spell's target, an object's owner or controller,
 * what an Aura enchants, and so on.
 * </p>
 * <p>
 * All these may be modified by effects, see also {@magic.ruleRef 612 CR 612}. The layers of
 * applying effects are:
 * </p>
 * <ol>
 * <li>Copy effects. See {@magic.ruleRef 706 CR 706}.</li>
 * <li>Control-changing effects. <i>(not applied here)</i></li>
 * <li>Text-changing effects. See {@magic.ruleRef 611 CR 611}.</li>
 * <li>Type-changing effects.</li>
 * <li>Color-changing effects.</li>
 * <li>Ability-adding and -removing effects.</li>
 * <li>P/T-Changing effects:
 * <ol type="a">
 * <li>Effects from characteristic-defining abilities. See {@magic.ruleRef 604.3 CR 604.3}.</li>
 * <li>P/T-setting effects.</li>
 * <li>P/T-changing effects.</li>
 * <li>P/T-changes from counters. See {@magic.ruleRef 120 CR 120}. <i>(applied, but not stored
 * here)</i></li>
 * <li>P/T-switching effects.</li>
 * </ol>
 * </li>
 * </ol>
 * <p>
 * Different properties are stored differently in Characteristics, to provide the ability to modify them
 * appropriately. However, generally true is that values (other than number of counters) is not permanently
 * changed, but instead the effects generating the change are stored, so that if an effect stops to apply, this is
 * easily possible.
 * </p>
 * <i>TODO This version does not s text-changing effects</i>
 * 
 * <ul>
 * <li>name<br>
 * A name can only be overridden, usually by a copying effect.</li>
 * <li>mana cost<br>
 * A mana cost can only be overridden in this version, usually by a copying effect. Usually, costs aren't changed
 * for other purposes anyway. Only the <i>ammount paid</i> is changed, either by cost-increasing or reducing
 * effects, or by additional or alternate costs paid.</li>
 * <li>color<br>
 * The colors of a card are normally determined by its mana cost: A card has all colors of mana symbols contained
 * in its mana cost. Colors are stored as a {@link SetCharacteristic}.</li>
 * <li>type<br>
 * is stored as a {@link SetCharacteristic} of types.</li>
 * <li>subtype<br>
 * is stored as a {@link SetCharacteristic} of subtypes.</li>
 * <li>supertype<br>
 * is stored as a {@link SetCharacteristic} of supertypes.</li>
 * <li>rules text & abilities<br>
 * are stored as a {@link SetCharacteristic} of abilities.</li>
 * <li>P/T<br>
 * Power and toughness can be changed in all the ways specified by the layers. Layer 7d is applied by taking all
 * counters with a name matching {@code "[+-]\d+/[+-]\d+"}.</li>
 * <li>Counters<br>
 * Though counters may directly influence characteristics, such as P/T and loyality, Counters are not stored here,
 * but at object level, since counters don't change when, like, flipping a permanent.</li>
 * <li>Control<br>
 * Control is not stored here, for the same reason as counters.</li>
 * </ul>
 * 
 * @version V0.0 11.07.2009
 * @author Clemens Koza
 */
public interface ObjectCharacteristics extends GameContent {
    /**
     * Returns the property change s used by all {@link Characteristic}s in this {@link ObjectCharacteristics}.
     */
    public EditablePropertyChangeSupport getPropertyChangeSupport();
    
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
    
    //TODO s loyalty
}
