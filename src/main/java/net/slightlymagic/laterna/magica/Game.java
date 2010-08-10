/**
 * Game.java
 * 
 * Created on 12.07.2009
 */

package net.slightlymagic.laterna.magica;


import java.util.List;
import java.util.Random;

import net.slightlymagic.laterna.magica.counter.Counter;
import net.slightlymagic.laterna.magica.edit.GameState;
import net.slightlymagic.laterna.magica.effect.GlobalEffects;
import net.slightlymagic.laterna.magica.effect.replacement.ReplacementEngine;
import net.slightlymagic.laterna.magica.event.GameStartListener;
import net.slightlymagic.laterna.magica.player.Player;
import net.slightlymagic.laterna.magica.timestamp.TimestampFactory;
import net.slightlymagic.laterna.magica.turnStructure.PhaseStructure;
import net.slightlymagic.laterna.magica.turnStructure.TurnStructure;
import net.slightlymagic.laterna.magica.zone.SortedZone;
import net.slightlymagic.laterna.magica.zone.Zone;
import net.slightlymagic.laterna.magica.zone.Zone.Zones;



/**
 * <p>
 * The class Game. This class represents a game of magic.
 * </p>
 * 
 * @version V0.0 12.07.2009
 * @author Clemens Koza
 */
public interface Game extends Cloneable {
    /**
     * <p>
     * Returns the GameState for this game.
     * </p>
     */
    public GameState getGameState();
    
    /**
     * <p>
     * Returns the Random for this game.
     * </p>
     */
    public Random getRandom();
    
    /**
     * <p>
     * Returns the GlobalEffects for this game.
     * </p>
     */
    public GlobalEffects getGlobalEffects();
    
    /**
     * <p>
     * Returns the timestamp factory.
     * </p>
     */
    public TimestampFactory getTimestampFactory();
    
    /**
     * <p>
     * Returns the replacement engine.
     * </p>
     */
    public ReplacementEngine getReplacementEngine();
    
    /**
     * <p>
     * Returns the game's turn structure.
     * </p>
     */
    public TurnStructure getTurnStructure();
    
    /**
     * <p>
     * Returns the game's phase structure.
     * </p>
     */
    public PhaseStructure getPhaseStructure();
    
    /**
     * Returns the current combat. The combat is only set during the combat phase.
     */
    public Combat getCombat();
    
    /**
     * <p>
     * Returns the list of players in the game, in the normal order of their turns. The beginning player comes
     * first.
     * </p>
     */
    public List<Player> getPlayers();
    
    /**
     * <p>
     * Returns one of the game's non-owned zones. Throws an {@link IllegalArgumentException} for owned zones and
     * {@code null}.
     * </p>
     */
    public Zone getZone(Zones type);
    
    /**
     * <p>
     * Returns the game's ante zone
     * </p>
     * 
     * @see Zones#ANTE
     */
    public Zone getAnte();
    
    /**
     * <p>
     * Returns the game's battlefield
     * </p>
     * 
     * @see Zones#BATTLEFIELD
     */
    public Zone getBattlefield();
    
    /**
     * <p>
     * Returns the game's exile zone
     * </p>
     * 
     * @see Zones#EXILE
     */
    public Zone getExile();
    
    /**
     * <p>
     * Returns the game's stack
     * </p>
     * 
     * @see Zones#STACK
     */
    public SortedZone getStack();
    
    /**
     * Returns a counter for the given name. Creates one if there's no such counter.
     */
    public Counter getCounter(String name);
    
    /**
     * This method is called to start a game after it was fully prepared (for example, players were created and
     * added to the game). This method will inform registered listeners that the configuration is finished.
     * 
     * Calling this method more than once results in unspecified behavior.
     */
    public void startGame();
    
    /**
     * Registers the listener to be notified when {@link #startGame()} is called.
     */
    public void addGameStartListener(GameStartListener l);
}
