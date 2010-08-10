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
public class DestroyPermanentEvent extends ReplaceableEvent {
    private MagicObject o;
    private boolean     regen;
    
    public DestroyPermanentEvent(MagicObject o) {
        this(o, true);
    }
    
    public DestroyPermanentEvent(MagicObject o, boolean regen) {
        super(o);
        this.o = o;
        this.regen = regen;
    }
    
    public boolean canRegenerate() {
        return regen;
    }
    
    @Override
    protected boolean execute0() {
        if(o.getZone().getType() != Zones.BATTLEFIELD) return false;
        //TODO add a check for indestructible
        o.setZone(o.getOwner().getGraveyard());
        return true;
    }
}
