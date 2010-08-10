/**
 * DamagePlayerEvent.java
 * 
 * Created on 12.07.2010
 */

package net.slightlymagic.laterna.magica.effects.damage;


import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.edit.CompoundEdit;
import net.slightlymagic.laterna.magica.player.Player;


/**
 * The class DamagePlayerEvent.
 * 
 * @version V0.0 12.07.2010
 * @author Clemens Koza
 */
public class DamagePlayerEvent extends DamageEvent {
    private Player p;
    
    public DamagePlayerEvent(Player p, MagicObject source, int ammount, boolean combat, boolean preventable) {
        super(p, source, ammount, combat, preventable);
        this.p = p;
    }
    
    public Player getPlayer() {
        return p;
    }
    
    @Override
    protected boolean execute0() {
        CompoundEdit ed = new CompoundEdit(getGame(), true, "Deal damage");
        
        //118.3a. Damage dealt to a player causes that player to lose that much life.
        getPlayer().getLifeTotal().loseLife(getAmmount());
        
        ed.end();
        
        return true;
    }
}
