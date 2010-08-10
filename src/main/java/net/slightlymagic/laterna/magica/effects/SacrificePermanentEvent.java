/**
 * DestroyPermanentEvent.java
 * 
 * Created on 30.06.2010
 */

package net.slightlymagic.laterna.magica.effects;


import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.effect.replacement.ReplaceableEvent;
import net.slightlymagic.laterna.magica.zone.Zone.Zones;


/**
 * The class DestroyPermanentEvent.
 * 
 * @version V0.0 30.06.2010
 * @author Clemens Koza
 */
public class SacrificePermanentEvent extends ReplaceableEvent {
    private MagicObject o;
    
    public SacrificePermanentEvent(MagicObject o) {
        super(o);
        this.o = o;
    }
    
    @Override
    protected boolean execute0() {
        if(o.getZone().getType() != Zones.BATTLEFIELD) return false;
        o.setZone(o.getOwner().getGraveyard());
        return true;
    }
}
