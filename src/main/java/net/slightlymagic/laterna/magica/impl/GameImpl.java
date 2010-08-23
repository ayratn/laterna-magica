/**
 * GameImpl.java
 * 
 * Created on 04.09.2009
 */

package net.slightlymagic.laterna.magica.impl;


import static net.slightlymagic.laterna.magica.zone.Zone.Zones.*;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.slightlymagic.laterna.magica.Combat;
import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.counter.EditableCounter;
import net.slightlymagic.laterna.magica.counter.EditableCounterImpl;
import net.slightlymagic.laterna.magica.edit.CompoundEdit;
import net.slightlymagic.laterna.magica.edit.GameState;
import net.slightlymagic.laterna.magica.edit.impl.EditableListenerList;
import net.slightlymagic.laterna.magica.effect.GlobalEffects;
import net.slightlymagic.laterna.magica.effect.impl.GlobalEffectsImpl;
import net.slightlymagic.laterna.magica.effect.replacement.ReplacementEngine;
import net.slightlymagic.laterna.magica.event.GameStartListener;
import net.slightlymagic.laterna.magica.player.Player;
import net.slightlymagic.laterna.magica.timestamp.TimestampFactory;
import net.slightlymagic.laterna.magica.turnStructure.PhaseStructure;
import net.slightlymagic.laterna.magica.turnStructure.TurnStructure;
import net.slightlymagic.laterna.magica.turnStructure.impl.PhaseStructureImpl;
import net.slightlymagic.laterna.magica.turnStructure.impl.TurnStructureImpl;
import net.slightlymagic.laterna.magica.util.ExtendedListenerList;
import net.slightlymagic.laterna.magica.util.MagicaCollections;
import net.slightlymagic.laterna.magica.zone.SortedZone;
import net.slightlymagic.laterna.magica.zone.Zone;
import net.slightlymagic.laterna.magica.zone.Zone.Zones;
import net.slightlymagic.laterna.magica.zone.impl.ZoneImpl;


/**
 * The class GameImpl.
 * 
 * @version V0.0 04.09.2009
 * @author Clemens Koza
 */
public class GameImpl implements Game {
    protected ExtendedListenerList       listeners;
    
    private GameState                    gameState;
    private Random                       random;
    private GlobalEffects                globalEffects;
    private TimestampFactory             timestampFactory;
    private ReplacementEngine            replacementEngine;
    private TurnStructure                turnStructure;
    private PhaseStructureImpl           phaseStructure;
    private List<Player>                 players;
    
    private Map<String, EditableCounter> counters;
    
    private Map<Zones, Zone>             zones;
    
    public GameImpl() {
        gameState = new GameState(this);
        CompoundEdit edit = new CompoundEdit(this, true, "Create game");
        listeners = new EditableListenerList(this);
        random = new MagicaRandom(this);
        globalEffects = new GlobalEffectsImpl(this);
        timestampFactory = new TimestampFactory(this);
        replacementEngine = new ReplacementEngine(this);
        turnStructure = new TurnStructureImpl(this);
        phaseStructure = new PhaseStructureImpl(this);
        players = MagicaCollections.editableList(this, new ArrayList<Player>());
        counters = MagicaCollections.editableMap(this, new HashMap<String, EditableCounter>());
        
        zones = new EnumMap<Zone.Zones, Zone>(Zones.class);
        for(Zones z:new Zones[] {ANTE, BATTLEFIELD, COMMAND, EXILE, STACK})
            zones.put(z, new ZoneImpl(this, z));
        
        addGameStartListener(new GameInitializer());
        edit.end();
    }
    
    public GameState getGameState() {
        return gameState;
    }
    
    public Random getRandom() {
        return random;
    }
    
    public GlobalEffects getGlobalEffects() {
        return globalEffects;
    }
    
    public TimestampFactory getTimestampFactory() {
        return timestampFactory;
    }
    
    public ReplacementEngine getReplacementEngine() {
        return replacementEngine;
    }
    
    public TurnStructure getTurnStructure() {
        return turnStructure;
    }
    
    public PhaseStructure getPhaseStructure() {
        return phaseStructure;
    }
    
    public Combat getCombat() {
        return phaseStructure.getCombat();
    }
    
    public List<Player> getPlayers() {
        return players;
    }
    
    public Zone getAnte() {
        return zones.get(ANTE);
    }
    
    public Zone getBattlefield() {
        return getZone(BATTLEFIELD);
    }
    
    public Zone getCommand() {
        return getZone(COMMAND);
    }
    
    public Zone getExile() {
        return getZone(EXILE);
    }
    
    public SortedZone getStack() {
        return (SortedZone) getZone(STACK);
    }
    
    public Zone getZone(Zones type) {
        Zone z = zones.get(type);
        if(z == null) throw new IllegalArgumentException("type == " + z);
        else return z;
    }
    
    public EditableCounter getCounter(String name) {
        EditableCounter result = counters.get(name);
        if(result == null) counters.put(name, result = new EditableCounterImpl(this));
        return result;
    }
    
    public void startGame() {
        CompoundEdit e = new CompoundEdit(this, true, "Start " + this);
        
        for(Iterator<GameStartListener> it = getGameStartListeners(); it.hasNext();)
            it.next().gameStarted(this);
        
        e.end();
    }
    
    public void addGameStartListener(GameStartListener l) {
        listeners.add(GameStartListener.class, l);
    }
    
    @SuppressWarnings("unchecked")
    protected Iterator<GameStartListener> getGameStartListeners() {
        return listeners.getIterator(GameStartListener.Internal.class, GameStartListener.class);
    }
}
