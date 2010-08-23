/**
 * EditableProperty.java
 * 
 * Created on 16.07.2010
 */

package net.slightlymagic.laterna.magica.edit.property;


import static java.lang.String.*;
import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.edit.Edit;
import net.slightlymagic.laterna.magica.impl.AbstractGameContent;


/**
 * The class EditableProperty.
 * 
 * @version V0.0 16.07.2010
 * @author Clemens Koza
 */
public class EditableProperty<T> extends AbstractGameContent {
    private EditablePropertyChangeSupport s;
    private String                        name;
    private T                             value;
    
    public EditableProperty(Game game, EditablePropertyChangeSupport s, String name) {
        this(game, s, name, null);
    }
    
    public EditableProperty(Game game, EditablePropertyChangeSupport s, String name, T initialValue) {
        super(game);
        this.s = s;
        this.name = name;
        value = initialValue;
    }
    
    public void setValue(T value) {
        if(this.value != value) new SetValueEdit(value).execute();
    }
    
    public T getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return valueOf(getValue());
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
            oldValue = value;
            value = newValue;
            if(s != null) s.firePropertyChange(name, oldValue, newValue);
        }
        
        @Override
        protected void rollback() {
            value = oldValue;
            if(s != null) s.firePropertyChange(name, newValue, oldValue);
        }
        
        @Override
        public String toString() {
            return "Set " + (s == null? "":s.getSourceBean() + "'s ") + name + " to " + newValue;
        }
    }
}
