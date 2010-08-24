/**
 * SetCharacteristicImpl.java
 * 
 * Created on 03.09.2009
 */

package net.slightlymagic.laterna.magica.characteristic.impl;


import static java.lang.String.*;

import java.util.HashSet;
import java.util.Set;

import net.slightlymagic.laterna.magica.characteristic.CharacteristicSnapshot.SetCharacteristicSnapshot;
import net.slightlymagic.laterna.magica.characteristic.SetCharacteristic;
import net.slightlymagic.laterna.magica.characteristics.Characteristics;
import net.slightlymagic.laterna.magica.edit.Edit;
import net.slightlymagic.laterna.magica.effect.characteristic.SetCharacteristicEffect;


/**
 * The class SetCharacteristicImpl.
 * 
 * @version V0.0 03.09.2009
 * @author Clemens Koza
 */
public class SetCharacteristicImpl<T> extends AbstractCharacteristic<SetCharacteristicEffect<T>> implements SetCharacteristic<T> {
    private String        name;
    protected boolean     adding;
    protected RefreshEdit edit;
    protected Set<T>      values;
    
    public SetCharacteristicImpl(ObjectCharacteristicsImpl characteristics, Characteristics characteristic, String name) {
        super(characteristics, characteristic);
        this.name = name;
        values = new HashSet<T>();
    }
    
    @Override
    public ObjectCharacteristicsImpl getCharacteristics() {
        return (ObjectCharacteristicsImpl) super.getCharacteristics();
    }
    
    public boolean hasValue(T value) {
        if(edit != null) return edit.hasValue(value);
        else {
            getCharacteristics().refresh();
            return values.contains(value) == adding;
        }
    }
    
    public boolean isAdding(Set<T> result) {
        getCharacteristics().refresh();
        if(result != null) {
            result.clear();
            result.addAll(values);
        }
        return adding;
    }
    
    @Override
    protected void reset() {
        edit = new RefreshEdit();
        if(getFirst() != null) applyEffect(getFirst());
    }
    
    @Override
    protected void applyEffect(SetCharacteristicEffect<T> ef) {
        edit.applyEffect(ef);
    }
    
    @Override
    protected void end() {
        edit.execute();
        edit = null;
    }
    
    public String valueString() {
        Set<T> result = new HashSet<T>();
        boolean adding = isAdding(result);
        if(result.isEmpty()) return "";
        return (adding? "+":"-") + result;
    }
    
    public SetCharacteristicSnapshot<T> getValues(SetCharacteristicSnapshot<T> c) {
        if(c == null) return null;
        
        getCharacteristics().refresh();
        
        c.setAdding(adding);
        if(!c.getValues().equals(values)) {
            c.getValues().clear();
            c.getValues().addAll(values);
            c.setChanged();
        }
        
        return c;
    }
    
    private class RefreshEdit extends Edit {
        private static final long serialVersionUID = 5056088603352168050L;
        
        private Set<T>            oldValues, newValues;
        private boolean           oldAdding, newAdding;
        
        public RefreshEdit() {
            super(SetCharacteristicImpl.this.getGame());
            newValues = new HashSet<T>();
            newAdding = true;
        }
        
        public boolean hasValue(T value) {
            return newValues.contains(value) == newAdding;
        }
        
        public void applyEffect(SetCharacteristicEffect<T> ef) {
            assert ef.getCharacteristic() == getCharacteristic():format("%s != %s", ef.getCharacteristic(),
                    getCharacteristic());
            log.trace("Applying " + ef);
            switch(ef.getMode()) {
                case ADDING:
                    if(newAdding) newValues.addAll(ef.getSetValues());
                    else newValues.removeAll(ef.getSetValues());
                break;
                case REMOVING:
                    if(newAdding) newValues.removeAll(ef.getSetValues());
                    else newValues.addAll(ef.getSetValues());
                break;
                case SETTING:
                    newAdding = true;
                    newValues.clear();
                    newValues.addAll(ef.getSetValues());
                break;
                case UNSETTING:
                    newAdding = false;
                    newValues.clear();
                    newValues.addAll(ef.getSetValues());
                break;
                default:
                    throw new AssertionError();
            }
        }
        
        @Override
        protected void execute() {
            oldValues = values;
            oldAdding = adding;
            values = newValues;
            adding = newAdding;
            s.firePropertyChange(getCharacteristic().name(), null, null);
        }
        
        @Override
        protected void rollback() {
            values = oldValues;
            adding = oldAdding;
        }
        
        @Override
        public String toString() {
            return "Refresh " + name;
        }
    }
}
