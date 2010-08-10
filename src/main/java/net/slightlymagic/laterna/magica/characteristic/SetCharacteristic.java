/**
 * SetCharacteristic.java
 * 
 * Created on 12.07.2009
 */

package net.slightlymagic.laterna.magica.characteristic;


import java.util.Set;


/**
 * <p>
 * The class SetCharacteristic. This interface defines the capabilities for a characteristic that can contain a set
 * of values, such as subtypes. Modifying that set is possible by:
 * </p>
 * <ul>
 * <li>Setting a set of values that is contained.</li>
 * <li>Setting a set of values that is not contained. All other values are contained.</li>
 * <li>Adding a set of values.</li>
 * <li>Removing a set of values.</li>
 * </ul>
 * 
 * @version V0.0 12.07.2009
 * @author Clemens Koza
 */
public interface SetCharacteristic<T> extends Characteristic {
    /**
     * <p>
     * Determines if the effects specify that the requested value is contained in this SetCharacteristic.
     * </p>
     */
    public boolean hasValue(T value);
    
    /**
     * Returns if the characteristic specifies the contained values (true) or the non-contained values (false).
     * 
     * The parameter is used to return these values, either the only ones included, or the only ones not included.
     * The set may be null if that is not necessary, but if one is given, it has to be modifiable.
     */
    public boolean isAdding(Set<T> result);
    
    /**
     * Returns a string representing this characteristic's values. This method uses {@link #isAdding(Set)}. If the
     * returned value is true, the string starts with "+", otherwise with "-". following that comes the set used as
     * the parameter. However, if the set is empty, the empty string may be returned.
     */
    public String valueString();
}
