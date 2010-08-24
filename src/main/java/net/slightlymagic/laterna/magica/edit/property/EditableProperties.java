/**
 * EditableProperties.java
 * 
 * Created on 24.08.2010
 */

package net.slightlymagic.laterna.magica.edit.property;


import static net.slightlymagic.laterna.magica.util.MagicaCollections.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

import net.slightlymagic.beans.properties.AbstractProperties;
import net.slightlymagic.beans.properties.Property;
import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.GameContent;


/**
 * The class EditableProperties.
 * 
 * @version V0.0 24.08.2010
 * @author Clemens Koza
 */
class EditableProperties extends AbstractProperties implements GameContent {
    private Game game;
    
    public EditableProperties(Game game) {
        this.game = game;
    }
    
    public Game getGame() {
        return game;
    }
    
    @Override
    public <T> Property<T> property(String name, Property<T> property) {
        return new EditableProperty<T>(game, property);
    }
    
    @Override
    public <E> List<E> list(String name, List<E> list) {
        return editableList(game, list);
    }
    
    @Override
    public <E> Set<E> set(String name, Set<E> set) {
        return editableSet(game, set);
    }
    
    @Override
    public <K, V> Map<K, V> map(String name, Map<K, V> map) {
        return editableMap(game, map);
    }
}
