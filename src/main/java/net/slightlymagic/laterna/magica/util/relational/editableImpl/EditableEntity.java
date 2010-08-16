/**
 * EditableEntity.java
 * 
 * Created on 15.08.2010
 */

package net.slightlymagic.laterna.magica.util.relational.editableImpl;


import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.edit.property.EditableProperty;
import net.slightlymagic.laterna.magica.impl.AbstractGameContent;


/**
 * The class EditableEntity.
 * 
 * @version V0.0 15.08.2010
 * @author Clemens Koza
 */
class EditableEntity<T> extends AbstractGameContent {
    private final EditableProperty<T> value;
    
    public EditableEntity(Game game) {
        this(game, null);
    }
    
    public EditableEntity(Game game, T value) {
        super(game);
        this.value = new EditableProperty<T>(getGame(), null, "value");
        setValue(value);
    }
    
    public void setValue(T value) {
        this.value.setValue(value);
    }
    
    public T getValue() {
        return value.getValue();
    }
}
