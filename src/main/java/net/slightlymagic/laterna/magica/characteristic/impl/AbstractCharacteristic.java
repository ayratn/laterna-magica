/**
 * AbstractCharacteristic.java
 * 
 * Created on 03.09.2009
 */

package net.slightlymagic.laterna.magica.characteristic.impl;


import static java.lang.String.*;
import net.slightlymagic.beans.properties.Property;
import net.slightlymagic.laterna.magica.characteristic.Characteristic;
import net.slightlymagic.laterna.magica.characteristic.ObjectCharacteristics;
import net.slightlymagic.laterna.magica.characteristics.Characteristics;
import net.slightlymagic.laterna.magica.edit.Edit;
import net.slightlymagic.laterna.magica.effect.ContinuousEffect;
import net.slightlymagic.laterna.magica.impl.AbstractGameContent;


/**
 * The class AbstractCharacteristic. AbstractCharacteristic makes some assumptions to reduce the amount of
 * {@link Edit} objects being created while refreshing it:
 * <ul>
 * <li>First, {@link #reset()} is called. This creates an {@link Edit}, but doesn't execute it</li>
 * <li>Then {@link #applyEffect(ContinuousEffect)} is called. This should not manipulate the configured value, but
 * the edit.</li>
 * <li>At the end, {@link #end()} is called and executes the created move</li>
 * </ul>
 * Note that while this process runs, the characteristic must return the value currently stored in the move.
 * 
 * This process is managed by {@link ObjectCharacteristicsImpl#refresh()}, which should be called every time before
 * returning the characteristic's value. If either of these methods is invoked otherwise, the behavior is
 * unspecified.
 * 
 * @version V0.0 03.09.2009
 * @author Clemens Koza
 */
abstract class AbstractCharacteristic<T extends ContinuousEffect> extends AbstractGameContent implements Characteristic {
    private ObjectCharacteristics characteristics;
    private Characteristics       characteristic;
    private Property<T>           first;
    
    public AbstractCharacteristic(ObjectCharacteristicsImpl characteristics, Characteristics characteristic) {
        super(characteristics);
        this.characteristics = characteristics;
        this.characteristic = characteristic;
        first = properties.property(characteristic.name() + ".first");
    }
    
    public ObjectCharacteristics getCharacteristics() {
        return characteristics;
    }
    
    public Characteristics getCharacteristic() {
        return characteristic;
    }
    
    protected void setFirst(T first) {
        this.first.setValue(first);
    }
    
    protected T getFirst() {
        return first.getValue();
    }
    
    /**
     * Resets the characteristic to the value from the card part (saved as the "first" property).
     */
    protected abstract void reset();
    
    /**
     * Applies one effect to the characteristic.
     */
    protected abstract void applyEffect(T ef);
    
    /**
     * Executes the edit that was configured by {@link #reset()} and {@link #applyEffect(ContinuousEffect)}.
     */
    protected abstract void end();
    
    @Override
    public String toString() {
        return valueOf(getCharacteristic());
    }
}
