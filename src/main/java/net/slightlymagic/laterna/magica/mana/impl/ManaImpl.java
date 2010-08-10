/**
 * ManaImpl.java
 * 
 * Created on 12.04.2010
 */

package net.slightlymagic.laterna.magica.mana.impl;


import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.characteristics.MagicColor;
import net.slightlymagic.laterna.magica.mana.Mana;


/**
 * The class ManaImpl.
 * 
 * @version V0.0 12.04.2010
 * @author Clemens Koza
 */
public class ManaImpl implements Mana {
    private MagicColor color;
    
    public ManaImpl(MagicColor color) {
        this.color = color;
    }
    
    public MagicColor getColor() {
        return color;
    }
    
    public MagicObject getSource() {
        //TODO implement
        return null;
    }
}
