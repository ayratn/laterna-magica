/**
 * PTSettingEffect.java
 * 
 * Created on 13.07.2009
 */

package net.slightlymagic.laterna.magica.effect.characteristic;


/**
 * The class PTSettingEffect. A P/T setting effect is applied in sublayer 7b. Such an effect sets P/T, or only one,
 * to any value.
 * 
 * @version V0.0 13.07.2009
 * @author Clemens Koza
 */
public interface PTSettingEffect extends PTEffect {
    /**
     * Returns if this effect applies to power.
     */
    public boolean affectsPower();
    
    /**
     * Returns the power set by this effect.
     * 
     * @throws IllegalStateException If this effect not {@link #affectsPower()}.
     */
    public int getPower();
    
    /**
     * Returns if this effect applies to toughness.
     */
    public boolean affectsToughness();
    
    /**
     * Returns the toughness set by this effect.
     * 
     * @throws IllegalStateException If this effect not {@link #affectsToughness()}.
     */
    public int getToughness();
}
