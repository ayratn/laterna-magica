/**
 * ManaSequenceImpl.java
 * 
 * Created on 03.09.2009
 */

package net.slightlymagic.laterna.magica.mana.impl;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.slightlymagic.laterna.magica.characteristics.MagicColor;
import net.slightlymagic.laterna.magica.mana.ManaSequence;
import net.slightlymagic.laterna.magica.mana.ManaSymbol;



/**
 * The class ManaSequenceImpl.
 * 
 * @version V0.0 03.09.2009
 * @author Clemens Koza
 */
public class ManaSequenceImpl implements ManaSequence {
    private static final long serialVersionUID = -6385518975263881411L;
    
    private List<ManaSymbol>  symbols, symbolsView;
    
    public ManaSequenceImpl(ManaSymbol... symbols) {
        this(Arrays.asList(symbols));
    }
    
    public ManaSequenceImpl(List<ManaSymbol> symbols) {
        if(symbols == null) throw new IllegalArgumentException("symbols == null");
        init(symbols);
    }
    
    private void init(List<ManaSymbol> l) {
        symbols = new ArrayList<ManaSymbol>(l);
        //TODO sort symbols
        symbolsView = Collections.unmodifiableList(symbols);
    }
    
    public int getConvertedCost() {
        int cc = 0;
        for(ManaSymbol s:getSymbols())
            cc += s.getConvertedCost();
        return cc;
    }
    
    public List<ManaSymbol> getSymbols() {
        return symbolsView;
    }
    
    public Set<MagicColor> getColors() {
        Set<MagicColor> result = new HashSet<MagicColor>();
        for(ManaSymbol part:getSymbols()) {
            result.addAll(part.getColors());
        }
        return result;
    }
    
    @Override
    public String toString() {
        return ManaFactoryImpl.INSTANCE.toString(this);
    }
    
    public boolean isResolved() {
        for(ManaSymbol symbol:getSymbols()) {
            switch(symbol.getType()) {
                case HYBRID:
                case VARIABLE:
                    return false;
            }
        }
        return true;
    }
}
