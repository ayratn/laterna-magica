/**
 * PTChangingEffectImpl.java
 * 
 * Created on 09.09.2009
 */

package net.slightlymagic.laterna.magica.effect.characteristic.impl;


import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.effect.characteristic.abs.AbstractPTChangingEffect;


/**
 * The class PTChangingEffectImpl.
 * 
 * @version V0.0 09.09.2009
 * @author Clemens Koza
 */
public class PTChangingEffectImpl extends AbstractPTChangingEffect {
    private int p, t;
    
    public PTChangingEffectImpl(Game game, int p, int t) {
        super(game);
        this.p = p;
        this.t = t;
    }
    
    @Override
    public int getPower() {
        return p;
    }
    
    @Override
    public int getToughness() {
        return t;
    }
}
