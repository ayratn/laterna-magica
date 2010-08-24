/**
 * EditableProperty.java
 * 
 * Created on 16.07.2010
 */

package net.slightlymagic.laterna.magica.edit.property;


import net.slightlymagic.beans.properties.AbstractProperty;
import net.slightlymagic.beans.properties.Property;
import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.GameContent;
import net.slightlymagic.laterna.magica.edit.Edit;


/**
 * The class EditableProperty.
 * 
 * @version V0.0 16.07.2010
 * @author Clemens Koza
 */
class EditableProperty<T> extends AbstractProperty<T> implements GameContent {
    private Game        game;
    private Property<T> property;
    
    public EditableProperty(Game game, Property<T> property) {
        if(property == null) throw new IllegalArgumentException();
        this.game = game;
        this.property = property;
    }
    
    @Override
    public Game getGame() {
        return game;
    }
    
    public void setValue(T value) {
        if(getValue() != value) new SetValueEdit(value).execute();
    }
    
    public T getValue() {
        return property.getValue();
    }
    
    private class SetValueEdit extends Edit {
        private static final long serialVersionUID = 93955529563844615L;
        
        private T                 oldValue, newValue;
        
        public SetValueEdit(T newValue) {
            super(EditableProperty.this.getGame());
            this.newValue = newValue;
        }
        
        @Override
        protected void execute() {
            oldValue = property.getValue();
            property.setValue(newValue);
        }
        
        @Override
        protected void rollback() {
            property.setValue(oldValue);
        }
        
        @Override
        public String toString() {
            return "Set value to " + newValue;
        }
    }
}
