/**
 * DamageEvent.java
 * 
 * Created on 12.07.2010
 */

package net.slightlymagic.laterna.magica.effects.damage;


import static com.google.common.base.Suppliers.*;
import static net.slightlymagic.laterna.magica.util.MagicaSuppliers.*;
import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.effect.replacement.ReplaceableEvent;
import net.slightlymagic.laterna.magica.player.Player;

import com.google.common.base.Supplier;


/**
 * The class DamageEvent.
 * 
 * @version V0.0 12.07.2010
 * @author Clemens Koza
 */
public abstract class DamageEvent extends ReplaceableEvent {
    private MagicObject source;
    private int         ammount;
    private boolean     combat, preventable;
    
    public DamageEvent(MagicObject affected, MagicObject source, int ammount, boolean combat, boolean preventable) {
        this(you(ofInstance(affected)), source, ammount, combat, preventable);
    }
    
    public DamageEvent(Player affected, MagicObject source, int ammount, boolean combat, boolean preventable) {
        this(ofInstance(affected), source, ammount, combat, preventable);
    }
    
    public DamageEvent(Supplier<Player> affected, MagicObject source, int ammount, boolean combat, boolean preventable) {
        super(affected);
        this.source = source;
        this.ammount = ammount;
        this.combat = combat;
        this.preventable = preventable;
    }
    
    public MagicObject getSource() {
        return source;
    }
    
    /**
     * This method can be used by replacement effects that prevent or increase damage.
     */
    public void setAmmount(int ammount) {
        this.ammount = ammount;
    }
    
    public int getAmmount() {
        return ammount;
    }
    
    public boolean isCombatDamage() {
        return combat;
    }
    
    public boolean isPreventable() {
        return preventable;
    }
}
