/**
 * DeckImpl.java
 * 
 * Created on 23.10.2009
 */

package net.slightlymagic.laterna.magica.deck.impl;


import java.util.HashMap;
import java.util.Map;

import net.slightlymagic.laterna.magica.card.Printing;
import net.slightlymagic.laterna.magica.deck.Deck;



/**
 * The class DeckImpl.
 * 
 * @version V0.0 23.10.2009
 * @author Clemens Koza
 */
public class DeckImpl implements Deck {
    private Map<DeckType, Map<Printing, Integer>> pools;
    
    public DeckImpl() {
        pools = new HashMap<DeckType, Map<Printing, Integer>>();
    }
    
    public void addPool(DeckType pool) {
        if(!pools.containsKey(pool)) pools.put(pool, new HashMap<Printing, Integer>());
    }
    
    public void removePool(DeckType pool) {
        pools.remove(pool);
    }
    
    public Map<Printing, Integer> getPool(DeckType pool) {
        return pools.get(pool);
    }
}
