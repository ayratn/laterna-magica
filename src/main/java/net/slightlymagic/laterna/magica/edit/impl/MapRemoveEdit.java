/**
 * MapRemoveEdit.java
 * 
 * Created on 21.03.2010
 */

package net.slightlymagic.laterna.magica.edit.impl;


import java.util.Map;

import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.edit.Edit;



/**
 * The class MapRemoveEdit.
 * 
 * @version V0.0 21.03.2010
 * @author Clemens Koza
 */
public class MapRemoveEdit<K, V> extends Edit {
    private static final long serialVersionUID = 8132588887500376977L;
    
    private Map<K, V>         map;
    private K                 key;
    private V                 oldValue;
    private boolean           removed;
    
    public MapRemoveEdit(Game game, Map<K, V> map, K key) {
        super(game);
        this.map = map;
        this.key = key;
    }
    
    @Override
    public void execute() {
        removed = map.containsKey(key);
        if(removed) oldValue = map.remove(key);
    }
    
    @Override
    protected void rollback() {
        if(removed) map.put(key, oldValue);
    }
    
    @Override
    public String toString() {
        return "Remove " + key + " from map";
    }
}
