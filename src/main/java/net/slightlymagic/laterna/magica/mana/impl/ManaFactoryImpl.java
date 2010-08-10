/**
 * ManaFactoryImpl.java
 * 
 * Created on 10.10.2009
 */

package net.slightlymagic.laterna.magica.mana.impl;


import static java.lang.Integer.*;
import static java.lang.String.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.slightlymagic.laterna.magica.characteristics.MagicColor;
import net.slightlymagic.laterna.magica.mana.ManaFactory;
import net.slightlymagic.laterna.magica.mana.ManaSequence;
import net.slightlymagic.laterna.magica.mana.ManaSymbol;



/**
 * The class ManaFactoryImpl. This class uses a notation using braces. For example, the "one mana" symbol is "{1}",
 * the "white-blue hybrid symbol" is "{W/U}". A mana sequence containing both is "{1}{W/U}".
 * 
 * The notations in this class is forgiving for whitespaces, except between digits of a number. Say, "{1 1}" will
 * generate an error and not evaluate to "eleven mana".
 * 
 * @version V0.0 10.10.2009
 * @author Clemens Koza
 */
public final class ManaFactoryImpl implements ManaFactory {
    public static final ManaFactory INSTANCE = new ManaFactoryImpl();
    
    private ManaFactoryImpl() {}
    
    public ManaSymbol parseSymbol(String symbol) {
        symbol = symbol.trim();
        if(!symbol.startsWith("{")) throw new IllegalArgumentException(format("illegal first character: %s",
                symbol.charAt(0)));
        else if(!symbol.endsWith("}")) throw new IllegalArgumentException(format("illegal last character: %s",
                symbol.charAt(symbol.length() - 1)));
        return parse0(symbol.substring(1, symbol.length() - 1).trim());
    }
    
    private ManaSymbol parse0(String symbol) {
        try {
            int i = parseInt(symbol);
            return new NumeralManaSymbol(i);
        } catch(NumberFormatException ex) {
            if(symbol.length() == 1) {
                if("S".equalsIgnoreCase(symbol)) return new SnowManaSymbol();
                else if("WwUuBbRrGg".contains(symbol)) return new ColoredManaSymbol(
                        MagicColor.getColorByChar(symbol.charAt(0)));
                else return new VariableManaSymbol(symbol.charAt(0));
            } else if(symbol.indexOf('/') != -1) {
                List<ManaSymbol> symbols = new ArrayList<ManaSymbol>();
                String[] parts = symbol.split("\\s*/\\s*");
                for(String part:parts)
                    symbols.add(parse0(part));
                return new HybridManaSymbol(symbols);
            }
        }
        throw new IllegalArgumentException(format("Illegal symbol: '%s'", symbol));
    }
    
    public ManaSequenceImpl parseSequence(String sequence) {
        sequence = sequence.trim();
        //empty mana sequence
        if(sequence.length() == 0) return new ManaSequenceImpl();
        
        if(!sequence.startsWith("{")) throw new IllegalArgumentException(format("illegal first character: '%s'",
                sequence.charAt(0)));
        else if(!sequence.endsWith("}")) throw new IllegalArgumentException(format("illegal last character: '%s'",
                sequence.charAt(sequence.length() - 1)));
        
        List<ManaSymbol> symbols = new ArrayList<ManaSymbol>();
        String[] parts = sequence.substring(1, sequence.length() - 1).trim().split("\\s*\\}\\s*\\{\\s*", -1);
        for(String s:parts)
            symbols.add(parse0(s));
        return new ManaSequenceImpl(symbols);
    }
    
    public String toString(ManaSymbol symbol) {
        return "{" + toString0(symbol) + "}";
    }
    
    public String toString0(ManaSymbol symbol) {
        switch(symbol.getType()) {
            case COLORED:
                return valueOf(symbol.getColor().getShortChar());
            case HYBRID:
                Iterator<ManaSymbol> parts = symbol.getParts().iterator();
                if(!parts.hasNext()) return "";
                StringBuilder erg = new StringBuilder(toString0(parts.next()));
                while(parts.hasNext()) {
                    erg.append("/");
                    erg.append(toString0(parts.next()));
                }
                return erg.toString();
            case NUMERAL:
                return String.valueOf(symbol.getAmount());
            case SNOW:
                return "S";
            case VARIABLE:
                return valueOf(symbol.getVariableName());
            default:
                throw new AssertionError();
        }
    }
    
    public String toString(ManaSequence sequence) {
        Iterator<ManaSymbol> symbols = sequence.getSymbols().iterator();
        StringBuilder erg = new StringBuilder();
        while(symbols.hasNext())
            erg.append(toString(symbols.next()));
        return erg.toString();
    }
}
