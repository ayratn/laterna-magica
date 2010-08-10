/**
 * Player.java
 * 
 * Created on 05.09.2009
 */

package net.slightlymagic.laterna.magica.player;


import java.util.Iterator;

import net.slightlymagic.laterna.magica.GameContent;
import net.slightlymagic.laterna.magica.counter.Counter;
import net.slightlymagic.laterna.magica.deck.Deck;
import net.slightlymagic.laterna.magica.event.DrawListener;
import net.slightlymagic.laterna.magica.mana.ManaPool;
import net.slightlymagic.laterna.magica.zone.SortedZone;
import net.slightlymagic.laterna.magica.zone.Zone;
import net.slightlymagic.laterna.magica.zone.Zone.Zones;



/**
 * The class Player.
 * 
 * @version V0.0 05.09.2009
 * @author Clemens Koza
 */
public interface Player extends GameContent {
    /**
     * The {@code deck} property name
     */
    public static final String DECK              = "deck";
    public static final String LAND_DROP_COUNTER = "LandDrop";
    
    public void setActor(Actor a);
    
    /**
     * Returns this player's actor.
     */
    public Actor getActor();
    
    /**
     * Sets the player's deck.
     */
    public void setDeck(Deck deck);
    
    /**
     * Returns the deck the player begins the game with.
     */
    public Deck getDeck();
    
    /**
     * Returns the player's life total.
     */
    public LifeTotal getLifeTotal();
    
    /**
     * Returns the player's mana pool.
     */
    public ManaPool getManaPool();
    
    /**
     * Returns one of the game's non-owned or the player's owned zones. Throws an {@link IllegalArgumentException}
     * for {@code null}.
     */
    public Zone getZone(Zones type);
    
    /**
     * Returns the player's graveyard
     * 
     * @see Zones#GRAVEYARD
     */
    public SortedZone getGraveyard();
    
    /**
     * Returns the player's hand
     * 
     * @see Zones#HAND
     */
    public Zone getHand();
    
    /**
     * Returns the player's library
     * 
     * @see Zones#LIBRARY
     */
    public SortedZone getLibrary();
    
    /**
     * Returns a counter for the given name. Creates one if there's no such counter.
     */
    public Counter getCounter(String name);
    
    /**
     * Draws a single card. Note that the return value does not necessarily mean if a card was put from library to
     * hand. The draw action could have been replaced.
     */
    public boolean drawCard();
    
    /**
     * Draws a number of cards. This is the same as repeatedly drawing a single card.
     */
    public void drawCards(int count);
    
    /**
     * Lets the player win this game. In a multiplayer game with limited range of influence, this instead causes
     * all players within range to lose. A player then wins if he is the only one still in the game.
     */
    public void winGame();
    
    /**
     * Lets the player lose this game. This causes the player to leave the game.
     */
    public void loseGame();
    
    /**
     * Lets the player draw this game. This causes the player to leave the game.
     */
    public void drawGame();
    
    public void addDrawListener(DrawListener l);
    
    public void removeDrawListener(DrawListener l);
    
    public Iterator<DrawListener> getDrawListeners();
}
