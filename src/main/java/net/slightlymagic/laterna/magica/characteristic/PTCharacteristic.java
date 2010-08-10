/**
 * PTCharacteristic.java
 * 
 * Created on 13.07.2009
 */

package net.slightlymagic.laterna.magica.characteristic;


/**
 * <p>
 * The class PTCharacteristic. PTCharacteristic stores a pair of power and toughness and applies effects in the
 * different sublayers:
 * </p>
 * <ol>
 * <li>Effects from characteristic-defining abilities. See CR 604.3.</li>
 * <li>P/T-setting effects.</li>
 * <li>P/T-changing effects.</li>
 * <li>P/T-changes from counters. See CR 120.</li>
 * <li>P/T-switching effects.</li>
 * </ol>
 * 
 * @version V0.0 13.07.2009
 * @author Clemens Koza
 */
public interface PTCharacteristic extends Characteristic {
    /**
     * <p>
     * Returns the resulting value of power.
     * </p>
     */
    public int getPower();
    
    /**
     * <p>
     * Returns the resulting value of toughness.
     * </p>
     */
    public int getToughness();
}
