/**
 * PlayerImpl.java
 * 
 * Created on 11.10.2009
 */

package net.slightlymagic.laterna.magica.player.impl;


import static net.slightlymagic.laterna.magica.zone.Zone.Zones.*;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.slightlymagic.beans.properties.Property;
import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.counter.EditableCounter;
import net.slightlymagic.laterna.magica.counter.EditableCounterImpl;
import net.slightlymagic.laterna.magica.deck.Deck;
import net.slightlymagic.laterna.magica.edit.CompoundEdit;
import net.slightlymagic.laterna.magica.event.DrawListener;
import net.slightlymagic.laterna.magica.impl.AbstractGameContent;
import net.slightlymagic.laterna.magica.mana.ManaPool;
import net.slightlymagic.laterna.magica.mana.impl.ManaPoolImpl;
import net.slightlymagic.laterna.magica.player.LifeTotal;
import net.slightlymagic.laterna.magica.player.MagicActor;
import net.slightlymagic.laterna.magica.player.Player;
import net.slightlymagic.laterna.magica.zone.SortedZone;
import net.slightlymagic.laterna.magica.zone.Zone;
import net.slightlymagic.laterna.magica.zone.Zone.Zones;
import net.slightlymagic.laterna.magica.zone.impl.ZoneImpl;


/**
 * The class PlayerImpl.
 * 
 * @version V0.0 11.10.2009
 * @author Clemens Koza
 */
public class PlayerImpl extends AbstractGameContent implements Player {
    private Property<Deck>               deck   = properties.property("deck");
    private Property<Status>             status = properties.property("gameStatus", Status.IN_GAME);
    
    private MagicActor                   actor;
    private String                       name;
    private LifeTotal                    life;
    private ManaPool                     pool;
    
    private Map<String, EditableCounter> counters;
    
    private Map<Zones, Zone>             zones;
    
    public PlayerImpl(Game game, String name) {
        super(game);
        
        CompoundEdit e = new CompoundEdit(getGame(), true, "Create " + name);
        this.name = name;
        life = new LifeTotalImpl(this);
        pool = new ManaPoolImpl(this);
        
        counters = properties.map("counters");
        
        zones = new EnumMap<Zone.Zones, Zone>(Zones.class);
        for(Zones z:new Zones[] {GRAVEYARD, HAND, LIBRARY})
            zones.put(z, new ZoneImpl(getGame(), z, this));
        
        e.end();
    }
    
    public MagicActor getActor() {
        if(actor == null) actor = new NoopActor(this);
        return actor;
    }
    
    public void setActor(MagicActor a) {
        if(a.getPlayer() != this) throw new IllegalArgumentException();
        actor = a;
    }
    
    public Deck getDeck() {
        return deck.getValue();
    }
    
    public void setDeck(Deck deck) {
        this.deck.setValue(deck);
    }
    
    public LifeTotal getLifeTotal() {
        return life;
    }
    
    public ManaPool getManaPool() {
        return pool;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    public Zone getZone(Zones type) {
        Zone z = zones.get(type);
        if(z == null) return getGame().getZone(type);
        else return z;
    }
    
    public SortedZone getGraveyard() {
        return (SortedZone) getZone(GRAVEYARD);
    }
    
    public Zone getHand() {
        return getZone(HAND);
    }
    
    public SortedZone getLibrary() {
        return (SortedZone) getZone(LIBRARY);
    }
    
    @Override
    public List<Player> getOpponents() {
        //TODO account for teams
        List<Player> players = new ArrayList<Player>(getGame().getPlayers());
        players.remove(this);
        return players;
    }
    
    public EditableCounter getCounter(String name) {
        EditableCounter result = counters.get(name);
        if(result == null) counters.put(name, result = new EditableCounterImpl(getGame()));
        return result;
    }
    
    
    public boolean drawCard() {
        return new DrawEvent(this).execute();
    }
    
    public void drawCards(int count) {
        for(int i = 0; i < count; i++)
            drawCard();
    }
    
    @Override
    public Status getPlayerStatus() {
        return status.getValue();
    }
    
    @Override
    public boolean isInGame() {
        return getPlayerStatus() == Status.IN_GAME;
    }
    
    public void winGame() {
        if(!isInGame()) throw new IllegalStateException(this + " is not in the game");
        for(Player p:getGame().getPlayersInGame())
            if(p != this) p.loseGame();
        
        List<Player> p = getGame().getPlayersInGame();
        if(p.size() == 1) {
            assert p.get(0) == this;
            log.debug(this + " wins the game");
            leaveGame(Status.WON);
        }
    }
    
    public void loseGame() {
        if(!isInGame()) throw new IllegalStateException(this + " is not in the game");
        log.debug(this + " loses the game");
        leaveGame(Status.LOST);
    }
    
    public void drawGame() {
        if(!isInGame()) throw new IllegalStateException(this + " is not in the game");
        log.debug(this + "draws the game");
        leaveGame(Status.DRAWN);
    }
    
    private void leaveGame(Status status) {
        this.status.setValue(status);
        
        //Check all zones for cards/spells/abilities this player owns
        for(Zones t:Zones.values()) {
            if(!t.isOwnedZone()) {
                checkZone(getGame().getZone(t));
            } else for(Player p:getGame().getPlayers()) {
                checkZone(p.getZone(t));
            }
        }
        
        //TODO let the player leave the game properly
        //cancel all delayed triggered abilities of the player
        //cancel all static effects generated by spells or abilities
        //(those from static abilities should be canceled by moving the card already)
    }
    
    private void checkZone(Zone z) {
        List<MagicObject> l = new ArrayList<MagicObject>();
        for(MagicObject o:z.getCards())
            if(o.getOwner() == this) l.add(o);
        for(MagicObject o:l)
            o.setZone(null);
    }
    
    protected boolean fireDrawEvent(DrawEvent e) {
        CompoundEdit ed = new CompoundEdit(getGame(), true, this + " draws a card");
        
        List<MagicObject> library = getLibrary().getCards();
        boolean draw = !library.isEmpty();
        if(draw) {
            library.get(library.size() - 1).setZone(getHand());
        }
        
        // Guaranteed to return a non-null array
        Object[] l = listeners.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for(int i = l.length - 2; i >= 0; i -= 2) {
            if(l[i] == DrawListener.class) {
                if(draw) ((DrawListener) l[i + 1]).cardDrawn(e);
                else ((DrawListener) l[i + 1]).cardNotDrawn(e);
            }
        }
        ed.end();
        
        return draw;
    }
    
    public void addDrawListener(DrawListener l) {
        listeners.add(DrawListener.class, l);
    }
    
    public void removeDrawListener(DrawListener l) {
        listeners.remove(DrawListener.class, l);
    }
    
    @SuppressWarnings("unchecked")
    public Iterator<DrawListener> getDrawListeners() {
        return listeners.getIterator(DrawListener.Internal.class, DrawListener.class);
    }
}
