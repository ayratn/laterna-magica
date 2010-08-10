/**
 * PTChangingEffect.java
 * 
 * Created on 13.07.2009
 */

package net.slightlymagic.laterna.magica.effect.characteristic;


/**
 * The class PTChangingEffect. A P/T changing effect is applied in sublayer 7c. Such an effect adds any values to
 * P/T.
 * 
 * @version V0.0 13.07.2009
 * @author Clemens Koza
 */
public interface PTChangingEffect extends PTEffect {
    /**
     * Returns the power offset added by this effect.
     */
    public int getPower();
    
    /**
     * Returns the toughness offset added by this effect.
     */
    public int getToughness();
}
