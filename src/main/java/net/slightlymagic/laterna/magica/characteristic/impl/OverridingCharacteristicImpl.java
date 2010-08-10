/**
 * OverridingCharacteristicImpl.java
 * 
 * Created on 03.09.2009
 */

package net.slightlymagic.laterna.magica.characteristic.impl;


import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.characteristic.OverridingCharacteristic;
import net.slightlymagic.laterna.magica.characteristics.Characteristics;
import net.slightlymagic.laterna.magica.edit.Edit;
import net.slightlymagic.laterna.magica.effect.characteristic.OverridingCharacteristicEffect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The class OverridingCharacteristicImpl.
 * 
 * @version V0.0 03.09.2009
 * @author Clemens Koza
 */
public class OverridingCharacteristicImpl<T> extends AbstractCharacteristic<OverridingCharacteristicEffect<T>> implements OverridingCharacteristic<T> {
    private static final Logger log = LoggerFactory.getLogger(OverridingCharacteristicImpl.class);
    
    private String              name;
    protected RefreshEdit       edit;
    protected T                 value;
    
    public OverridingCharacteristicImpl(Game game, ObjectCharacteristicsImpl characteristics, Characteristics characteristic, String name) {
        super(game, characteristics, characteristic);
        this.name = name;
    }
    
    @Override
    public ObjectCharacteristicsImpl getCharacteristics() {
        return (ObjectCharacteristicsImpl) super.getCharacteristics();
    }
    
    public T getValue() {
        if(edit != null) return edit.newValue;
        else {
            getCharacteristics().refresh();
            return value;
        }
    }
    
    @Override
    protected void reset() {
        edit = new RefreshEdit();
        if(getFirst() != null) applyEffect(getFirst());
    }
    
    @Override
    protected void applyEffect(OverridingCharacteristicEffect<T> ef) {
        edit.applyEffect(ef);
    }
    
    @Override
    protected void end() {
        edit.execute();
        edit = null;
    }
    
    private class RefreshEdit extends Edit {
        private static final long serialVersionUID = 5056088603352168050L;
        
        private T                 oldValue, newValue;
        
        public RefreshEdit() {
            super(OverridingCharacteristicImpl.this.getGame());
            newValue = null;
        }
        
        public void applyEffect(OverridingCharacteristicEffect<T> ef) {
            assert ef.getCharacteristic() == getCharacteristic();
            log.trace("Applying " + ef);
            newValue = ef.getOverridingValue();
        }
        
        @Override
        protected void execute() {
            oldValue = value;
            value = newValue;
            getCharacteristics().getPropertyChangeSupport().firePropertyChange(getCharacteristic().name(),
                    oldValue, newValue);
        }
        
        @Override
        protected void rollback() {
            value = oldValue;
            getCharacteristics().getPropertyChangeSupport().firePropertyChange(getCharacteristic().name(),
                    newValue, oldValue);
        }
        
        @Override
        public String toString() {
            return "Refresh " + name;
        }
    }
}
