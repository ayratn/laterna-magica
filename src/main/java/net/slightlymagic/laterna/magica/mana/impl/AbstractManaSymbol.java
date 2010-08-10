/**
 * AbstractManaSymbol.java
 * 
 * Created on 03.09.2009
 */

package net.slightlymagic.laterna.magica.mana.impl;


import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.slightlymagic.laterna.magica.characteristics.MagicColor;
import net.slightlymagic.laterna.magica.mana.ManaSymbol;


/**
 * The class AbstractManaSymbol. All methods, except {@link #getType()}, which stays abstract, throw
 * {@link IllegalStateException}s. Only those of interest have to be overwritten.
 * 
 * @version V0.0 03.09.2009
 * @author Clemens Koza
 */
public abstract class AbstractManaSymbol implements ManaSymbol {
    private static final long serialVersionUID = 2871765858009735445L;
    
    public int getAmount() {
        throw new IllegalStateException();
    }
    
    public MagicColor getColor() {
        throw new IllegalStateException();
    }
    
    public List<ManaSymbol> getParts() {
        throw new IllegalStateException();
    }
    
    public char getVariableName() {
        throw new IllegalStateException();
    }
    
    @SuppressWarnings("unchecked")
    public Set<MagicColor> getColors() {
        return Collections.EMPTY_SET;
    }
}
