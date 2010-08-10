/**
 * MapPutEdit.java
 * 
 * Created on 21.03.2010
 */

package net.slightlymagic.laterna.magica.edit.impl;


import java.util.Map;

import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.edit.Edit;



/**
 * The class MapPutEdit.
 * 
 * @version V0.0 21.03.2010
 * @author Clemens Koza
 */
public class MapPutEdit<K, V> extends Edit {
    private static final long serialVersionUID = 2694000154192080900L;
    
    private Map<K, V>         map;
    private K                 key;
    private V                 oldValue, newValue;
    private boolean           contained;
    
    public MapPutEdit(Game game, Map<K, V> map, K key, V value) {
        super(game);
        this.map = map;
        this.key = key;
        this.newValue = value;
    }
    
    @Override
    public void execute() {
        contained = map.containsKey(key);
        oldValue = map.put(key, newValue);
    }
    
    @Override
    protected void rollback() {
        if(contained) map.put(key, oldValue);
        else map.remove(key);
    }
    
    public V getOldValue() {
        return oldValue;
    }
    
    @Override
    public String toString() {
        return "Put [" + key + ", " + newValue + "] into map";
    }
}
