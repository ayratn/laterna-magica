/**
 * VariableManaSymbol.java
 * 
 * Created on 03.09.2009
 */

package net.slightlymagic.laterna.magica.mana.impl;


import static java.lang.String.*;
import static net.slightlymagic.laterna.magica.mana.ManaSymbol.ManaType.*;


/**
 * The class VariableManaSymbol.
 * 
 * @version V0.0 03.09.2009
 * @author Clemens Koza
 */
public class VariableManaSymbol extends AbstractManaSymbol {
    private static final long serialVersionUID = 466696725749232561L;
    
    private char              variable;
    
    public VariableManaSymbol(char variable) {
        //Only uppercase letters, and not from
        // -Colors
        // -Snow
        // -tap/untap
        if(variable < 'A' || variable > 'Z' || "WUBRG S TQ".indexOf(variable) != -1) throw new IllegalArgumentException(
                format("Illegal variable mana character: '%s'", variable));
        if("XYZ".indexOf(variable) == -1) new Exception(String.format("Warning: variable mana character: '%s'",
                variable)).printStackTrace();
        this.variable = variable;
    }
    
    public ManaType getType() {
        return VARIABLE;
    }
    
    @Override
    public char getVariableName() {
        return variable;
    }
    
    public int getConvertedCost() {
        return 0;
    }
    
    @Override
    public String toString() {
        return ManaFactoryImpl.INSTANCE.toString(this);
    }
}
