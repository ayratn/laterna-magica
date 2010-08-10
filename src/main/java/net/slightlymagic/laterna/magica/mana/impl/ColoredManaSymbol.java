/**
 * ColoredManaSymbol.java
 * 
 * Created on 03.09.2009
 */

package net.slightlymagic.laterna.magica.mana.impl;


import static net.slightlymagic.laterna.magica.mana.ManaSymbol.ManaType.*;

import java.util.Collections;
import java.util.Set;

import net.slightlymagic.laterna.magica.characteristics.MagicColor;


/**
 * The class ColoredManaSymbol.
 * 
 * @version V0.0 03.09.2009
 * @author Clemens Koza
 */
public class ColoredManaSymbol extends AbstractManaSymbol {
    private static final long serialVersionUID = -6856761926275172820L;
    
    private MagicColor        color;
    
    public ColoredManaSymbol(MagicColor color) {
        if(color == null) throw new IllegalArgumentException("color == null");
        this.color = color;
    }
    
    public ManaType getType() {
        return COLORED;
    }
    
    @Override
    public MagicColor getColor() {
        return color;
    }
    
    public int getConvertedCost() {
        return 1;
    }
    
    @Override
    public Set<MagicColor> getColors() {
        return Collections.singleton(color);
    }
    
    @Override
    public String toString() {
        return ManaFactoryImpl.INSTANCE.toString(this);
    }
}
