/**
 * GenericManaSymbol.java
 * 
 * Created on 03.09.2009
 */

package net.slightlymagic.laterna.magica.mana.impl;


import static net.slightlymagic.laterna.magica.mana.ManaSymbol.ManaType.*;


/**
 * The class NumeralManaSymbol.
 * 
 * @version V0.0 03.09.2009
 * @author Clemens Koza
 */
public class NumeralManaSymbol extends AbstractManaSymbol {
    private static final long serialVersionUID = 4981087803274983632L;
    
    private int               ammount;
    
    public NumeralManaSymbol(int ammount) {
        if(ammount < 0) throw new IllegalArgumentException("ammount == " + ammount);
        this.ammount = ammount;
    }
    
    public ManaType getType() {
        return NUMERAL;
    }
    
    @Override
    public int getAmount() {
        return ammount;
    }
    
    public int getConvertedCost() {
        return ammount;
    }
    
    @Override
    public String toString() {
        return ManaFactoryImpl.INSTANCE.toString(this);
    }
}
