/**
 * EditablePropertyChangeSupport.java
 * 
 * Created on 16.07.2010
 */

package net.slightlymagic.laterna.magica.edit.property;


import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.GameContent;
import net.slightlymagic.laterna.magica.edit.Edit;


/**
 * The class EditablePropertyChangeSupport.
 * 
 * @version V0.0 16.07.2010
 * @author Clemens Koza
 */
public class EditablePropertyChangeSupport extends PropertyChangeSupport implements GameContent {
    private static final long serialVersionUID = -4241465377828790076L;
    
    private Game              game;
    private Object            sourceBean;
    
    public EditablePropertyChangeSupport(Game game, Object sourceBean) {
        super(sourceBean);
        this.game = game;
        this.sourceBean = sourceBean;
    }
    
    public Game getGame() {
        return game;
    }
    
    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        new PropertyChangeListenerEdit(null, listener, true).execute();
    }
    
    @Override
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if(propertyName == null) return;
        new PropertyChangeListenerEdit(propertyName, listener, true).execute();
    }
    
    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        new PropertyChangeListenerEdit(null, listener, false).execute();
    }
    
    @Override
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if(propertyName == null) return;
        new PropertyChangeListenerEdit(propertyName, listener, false).execute();
    }
    
    private void addPropertyChangeListener0(PropertyChangeListener listener) {
        super.addPropertyChangeListener(listener);
    }
    
    private void addPropertyChangeListener0(String propertyName, PropertyChangeListener listener) {
        super.addPropertyChangeListener(propertyName, listener);
    }
    
    private void removePropertyChangeListener0(PropertyChangeListener listener) {
        super.removePropertyChangeListener(listener);
    }
    
    private void removePropertyChangeListener0(String propertyName, PropertyChangeListener listener) {
        super.removePropertyChangeListener(propertyName, listener);
    }
    
    Object getSourceBean() {
        return sourceBean;
    }
    
    private class PropertyChangeListenerEdit extends Edit {
        private static final long      serialVersionUID = -4652984534134096875L;
        
        private String                 propertyName;
        private PropertyChangeListener listener;
        private boolean                add;
        
        public PropertyChangeListenerEdit(String propertyName, PropertyChangeListener listener, boolean add) {
            super(EditablePropertyChangeSupport.this.getGame());
            this.propertyName = propertyName;
            this.listener = listener;
            this.add = add;
        }
        
        @Override
        protected void execute() {
            if(add) {
                if(propertyName == null) addPropertyChangeListener0(listener);
                else addPropertyChangeListener0(propertyName, listener);
            } else {
                if(propertyName == null) removePropertyChangeListener0(listener);
                else removePropertyChangeListener0(propertyName, listener);
            }
        }
        
        @Override
        protected void rollback() {
            if(add) {
                if(propertyName == null) removePropertyChangeListener0(listener);
                else removePropertyChangeListener0(propertyName, listener);
            } else {
                if(propertyName == null) addPropertyChangeListener0(listener);
                else addPropertyChangeListener0(propertyName, listener);
            }
        }
        
        @Override
        public String toString() {
            return (add? "Add ":"Remove ") + listener + " for "
                    + (propertyName == null? "all properties":propertyName);
        }
    }
}
