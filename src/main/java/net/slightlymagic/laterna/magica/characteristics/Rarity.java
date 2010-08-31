/**
 * Rarity.java
 * 
 * Created on 27.07.2010
 */

package net.slightlymagic.laterna.magica.characteristics;


import java.util.HashMap;
import java.util.Map;


/**
 * The class Rarity.
 * 
 * @version V0.0 27.07.2010
 * @author Clemens Koza
 */
public enum Rarity {
    LAND('L'), COMMON('C'), UNCOMMON('U'), RARE('R'), MYTHIC('M');
    //TODO how to represent the purple rarity from timespiral?
    
    private static final Map<Character, Rarity> rarities;
    
    static {
        rarities = new HashMap<Character, Rarity>();
        for(Rarity r:values())
            rarities.put(r.r, r);
    }
    
    public static Rarity getRarity(char r) {
        return rarities.get(Character.toUpperCase(r));
    }
    
    private final char r;
    
    private Rarity(char r) {
        this.r = r;
    }
    
    public char getRarity() {
        return r;
    }
    
    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
