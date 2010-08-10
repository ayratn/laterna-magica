/**
 * ManaSequence.java
 * 
 * Created on 15.07.2009
 */

package net.slightlymagic.laterna.magica.mana;


import java.io.Serializable;
import java.util.List;
import java.util.Set;

import net.slightlymagic.laterna.magica.characteristics.MagicColor;


/**
 * The class ManaSequence. A mana sequence consists of one or more mana symbols, and may be used in costs or mana
 * producing effects. The ammount of the mana symbols add up. Variable symbols of the same name have the same
 * values. Hybrid symbols are treated individually.
 * 
 * @version V0.0 15.07.2009
 * @author Clemens Koza
 */
public interface ManaSequence extends Serializable {
    /**
     * Returns the converted mana cost of this mana sequence.
     */
    public int getConvertedCost();
    
    /**
     * Returns the sequence's mana symbols.
     */
    public List<ManaSymbol> getSymbols();
    
    /**
     * Returns all colors of this mana sequence.
     */
    public Set<MagicColor> getColors();
    
    /**
     * Returns true if the cost does not contain hybrid or variable symbols.
     */
    public boolean isResolved();
}
