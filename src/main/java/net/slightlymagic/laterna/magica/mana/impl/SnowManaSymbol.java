/**
 * SnowManaSymbol.java
 * 
 * Created on 03.09.2009
 */

package net.slightlymagic.laterna.magica.mana.impl;


import static net.slightlymagic.laterna.magica.mana.ManaSymbol.ManaType.*;


/**
 * The class SnowManaSymbol.
 * 
 * @version V0.0 03.09.2009
 * @author Clemens Koza
 */
public class SnowManaSymbol extends AbstractManaSymbol {
    private static final long serialVersionUID = 6363779753815124265L;
    
    public ManaType getType() {
        return SNOW;
    }
    
    public int getConvertedCost() {
        return 1;
    }
    
    @Override
    public String toString() {
        return ManaFactoryImpl.INSTANCE.toString(this);
    }
}
