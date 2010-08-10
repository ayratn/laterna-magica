/**
 * ManaFactory.java
 * 
 * Created on 10.10.2009
 */

package net.slightlymagic.laterna.magica.mana;


import net.slightlymagic.laterna.magica.mana.impl.ManaSequenceImpl;


/**
 * The class ManaFactory. An object of this interface is used to parse text to mana related objects. Different
 * formats may use different ManaFactory implementations.
 * 
 * @version V0.0 10.10.2009
 * @author Clemens Koza
 */
public interface ManaFactory {
    public ManaSymbol parseSymbol(String symbol);
    
    public ManaSequenceImpl parseSequence(String sequence);
    
    public String toString(ManaSymbol symbol);
    
    public String toString(ManaSequence sequence);
}
