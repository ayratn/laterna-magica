/**
 * HybridManaSymbol.java
 * 
 * Created on 03.09.2009
 */

package net.slightlymagic.laterna.magica.mana.impl;


import static java.lang.Math.*;
import static net.slightlymagic.laterna.magica.mana.ManaSymbol.ManaType.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.slightlymagic.laterna.magica.characteristics.MagicColor;
import net.slightlymagic.laterna.magica.mana.ManaSymbol;


/**
 * The class HybridManaSymbol.
 * 
 * @version V0.0 03.09.2009
 * @author Clemens Koza
 */
public class HybridManaSymbol extends AbstractManaSymbol {
    private static final long serialVersionUID = 1947120565393315480L;
    
    private List<ManaSymbol>  parts, partsView;
    
    public HybridManaSymbol(ManaSymbol... symbols) {
        this(Arrays.asList(symbols));
    }
    
    public HybridManaSymbol(List<ManaSymbol> symbols) {
        if(symbols == null || symbols.size() == 0) throw new IllegalArgumentException("symbols == " + symbols);
        init(symbols);
    }
    
    private void init(List<ManaSymbol> l) {
        parts = new ArrayList<ManaSymbol>(l);
        partsView = Collections.unmodifiableList(parts);
    }
    
    public ManaType getType() {
        return HYBRID;
    }
    
    @Override
    public List<ManaSymbol> getParts() {
        return partsView;
    }
    
    @Override
    public Set<MagicColor> getColors() {
        Set<MagicColor> result = new HashSet<MagicColor>();
        for(ManaSymbol part:getParts()) {
            result.addAll(part.getColors());
        }
        return result;
    }
    
    public int getConvertedCost() {
        int result = 0;
        for(ManaSymbol part:getParts()) {
            result = max(result, part.getConvertedCost());
        }
        return result;
    }
    
    @Override
    public String toString() {
        return ManaFactoryImpl.INSTANCE.toString(this);
    }
}
