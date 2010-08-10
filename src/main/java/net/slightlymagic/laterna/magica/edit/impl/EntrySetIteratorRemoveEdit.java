/**
 * EntrySetIteratorRemoveEdit.java
 * 
 * Created on 25.03.2010
 */

package net.slightlymagic.laterna.magica.edit.impl;


import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.slightlymagic.laterna.magica.Game;



/**
 * The class EntrySetIteratorRemoveEdit.
 * 
 * @version V0.0 25.03.2010
 * @author Clemens Koza
 */
public class EntrySetIteratorRemoveEdit<K, V> extends IteratorRemoveEdit<Entry<K, V>> {
    private static final long serialVersionUID = 5260678785304781137L;
    
    private Map<K, V>         map;
    private K                 key;
    private V                 value;
    
    public EntrySetIteratorRemoveEdit(Game game, Map<K, V> map, Iterator<Entry<K, V>> it, K key, V value) {
        super(game, it, null);
        this.map = map;
        this.key = key;
        this.value = value;
    }
    
    @Override
    protected void add() {
        map.put(key, value);
    }
    
    @Override
    protected void remove() {
        map.remove(key);
    }
}
