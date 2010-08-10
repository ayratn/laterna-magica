/**
 * LifeEvent.java
 * 
 * Created on 20.10.2009
 */

package net.slightlymagic.laterna.magica.player.impl;


import net.slightlymagic.laterna.magica.effect.replacement.ReplaceableEvent;
import net.slightlymagic.laterna.magica.player.Player;


/**
 * The class LifeEvent. A life event reports the change of a player's life total. The change described by the
 * positive {@code amount} and the {@code boolean} {@code gained}.
 * 
 * @version V0.0 20.10.2009
 * @author Clemens Koza
 */
public class LifeEvent extends ReplaceableEvent {
    private Player player;
    private int    amount;
    
    /**
     * Creates a life event from a signed amount: Positive amounts are life gains, negative losses. Zero causes an
     * {@link IllegalArgumentException}.
     */
    public LifeEvent(Player player, int amount) {
        super(player);
        if(amount == 0) throw new IllegalArgumentException();
        this.player = player;
        this.amount = amount;
    }
    
    /**
     * Creates a life event from an unsigned amount: Nonpositive amounts cause an {@link IllegalArgumentException}.
     */
    public LifeEvent(Player player, int amount, boolean gained) {
        super(player);
        if(amount <= 0) throw new IllegalArgumentException();
        this.player = player;
        this.amount = gained? amount:-amount;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    /**
     * Returns the absolute ammount of life lost/gained
     */
    public int getAmount() {
        return amount < 0? -amount:amount;
    }
    
    public boolean isGained() {
        return amount > 0;
    }
    
    /**
     * Returns the amount by which the player's life total has changed absolutely. This is just the amount with a
     * sign depending on gained/lost.
     */
    public int getChange() {
        return amount;
    }
    
    @Override
    public boolean execute0() {
        return ((LifeTotalImpl) getPlayer().getLifeTotal()).fireLifeEvent(this);
    }
}
