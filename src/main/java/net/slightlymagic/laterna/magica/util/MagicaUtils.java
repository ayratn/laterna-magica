/**
 * MagicaUtils.java
 * 
 * Created on 30.03.2010
 */

package net.slightlymagic.laterna.magica.util;


import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.mana.ManaPool;
import net.slightlymagic.laterna.magica.mana.ManaSequence;
import net.slightlymagic.laterna.magica.mana.ManaSymbol;
import net.slightlymagic.laterna.magica.mana.impl.ManaImpl;
import net.slightlymagic.laterna.magica.player.Player;
import net.slightlymagic.laterna.magica.turnStructure.PhaseStructure.Step;


/**
 * The class MagicaUtils.
 * 
 * @version V0.0 30.03.2010
 * @author Clemens Koza
 */
public final class MagicaUtils {
    private MagicaUtils() {}
    
    /**
     * Returns if the player could currently play a sorcery.
     */
    public static boolean canPlaySorcery(Player p) {
        //it's the player's turn
        if(p.getGame().getTurnStructure().getActivePlayer() != p) return false;
        //it's a main phase
        if(p.getGame().getPhaseStructure().getStep() != Step.MAIN) return false;
        //he has priority
        if(p.getGame().getPhaseStructure().getPriorPlayer() != p) return false;
        //the stack is empty
        if(!p.getGame().getStack().isEmpty()) return false;
        return true;
    }
    
    /**
     * Returns if the player could currently play an instant, that is anytime he has priority.
     */
    public static boolean canPlayInstant(Player p) {
        //he has priority
        if(p.getGame().getPhaseStructure().getPriorPlayer() != p) return false;
        return true;
    }
    
    /**
     * Returns the current "you" for the supplied object. That is, if the object has a controller, its controller,
     * or its owner otherwise.
     */
    public static Player you(MagicObject o) {
        Player pl = o.getController();
        if(pl != null) return pl;
        else return o.getOwner();
    }
    
    /**
     * Adds the mana in the mana sequence to the player's mana symbol. This will only work ifthe sequence does not
     * contain snow, hybrid or variable mana symbols.
     */
    public static void addMana(Player to, ManaSequence mana) {
        ManaPool p = to.getManaPool();
        for(ManaSymbol s:mana.getSymbols()) {
            switch(s.getType()) {
                case VARIABLE:
                case HYBRID:
                case SNOW:
                    throw new IllegalArgumentException("Only colored and numeral symbols allowed");
                case COLORED:
                    p.addMana(new ManaImpl(s.getColor()));
                break;
                case NUMERAL:
                    for(int i = 0; i < s.getAmount(); i++) {
                        p.addMana(new ManaImpl(null));
                    }
                break;
                default:
                    throw new AssertionError();
            }
        }
    }
}
