/**
 * Mana.java
 * 
 * Created on 13.07.2009
 */

package net.slightlymagic.laterna.magica.mana;


import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.characteristics.MagicColor;
import net.slightlymagic.laterna.magica.characteristics.SuperType;


/**
 * The class Mana. An object of this class represents a single mana in a pool. A mana is characterized by its
 * color, or lack thereof, and the ability that created it, along with the object to which the ability belongs. For
 * example, mana generated by {@link SuperType#SNOW snow} sources can be used to pay for a snow symbol.
 * Additionally, the effect generating the mana may precisize what costs that mana can be used to pay for.
 * 
 * @version V0.0 13.07.2009
 * @author Clemens Koza
 */
public interface Mana {
    /**
     * Returns the color of mana, or {@code null} if the mana is colorless.
     */
    public MagicColor getColor();
    
    /**
     * Returns the source that produced this mana.
     */
    public MagicObject getSource();
}
