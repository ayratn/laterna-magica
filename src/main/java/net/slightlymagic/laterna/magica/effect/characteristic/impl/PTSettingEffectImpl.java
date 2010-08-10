/**
 * PTChangingEffectImpl.java
 * 
 * Created on 09.09.2009
 */

package net.slightlymagic.laterna.magica.effect.characteristic.impl;


import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.effect.characteristic.abs.AbstractPTSettingEffect;


/**
 * The class PTChangingEffectImpl.
 * 
 * @version V0.0 09.09.2009
 * @author Clemens Koza
 */
public class PTSettingEffectImpl extends AbstractPTSettingEffect {
    private boolean isP, isT;
    private int     p, t;
    
    public PTSettingEffectImpl(Game game, int p, int t) {
        this(game, true, p, true, t);
    }
    
    /**
     * Creates a P/T switching effect for one of power or toughness. if p is true, value specifies the power,
     * otherwise it specifies the toughness.
     */
    public PTSettingEffectImpl(Game game, boolean p, int value) {
        this(game, p, value, !p, value);
    }
    
    public PTSettingEffectImpl(Game game, boolean isP, int p, boolean isT, int t) {
        super(game);
        this.isP = isP;
        this.p = p;
        this.isT = isT;
        this.t = t;
    }
    
    @Override
    public boolean affectsPower() {
        return isP;
    }
    
    @Override
    public boolean affectsToughness() {
        return isT;
    }
    
    @Override
    public int getPower() {
        if(!affectsPower()) throw new IllegalStateException();
        return p;
    }
    
    @Override
    public int getToughness() {
        if(!affectsToughness()) throw new IllegalStateException();
        return t;
    }
}
