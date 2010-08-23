/**
 * EditableEntity.java
 * 
 * Created on 15.08.2010
 */

package net.slightlymagic.laterna.magica.util.relational.editableImpl;


import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.edit.property.EditableProperty;
import net.slightlymagic.laterna.magica.edit.property.EditablePropertyChangeSupport;
import net.slightlymagic.laterna.magica.impl.AbstractGameContent;


/**
 * The class EditableEntity.
 * 
 * @version V0.0 15.08.2010
 * @author Clemens Koza
 */
class EditableEntity<T> extends AbstractGameContent {
    protected final EditablePropertyChangeSupport s;
    private final EditableProperty<T>             value;
    
    public EditableEntity(Game game, EditablePropertyChangeSupport s, T value) {
        super(game);
        this.s = s;
        this.value = new EditableProperty<T>(getGame(), s, "value");
        setValue(value);
    }
    
    public void setValue(T value) {
        this.value.setValue(value);
    }
    
    public T getValue() {
        return value.getValue();
    }
}
