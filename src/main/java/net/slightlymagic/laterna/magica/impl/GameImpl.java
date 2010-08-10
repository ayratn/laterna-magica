/**
 * GameImpl.java
 * 
 * Created on 04.09.2009
 */

package net.slightlymagic.laterna.magica.impl;


import java.util.ArrayList;
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
    
    private Zone                         ante, battlefield, exile;
    private SortedZone                   stack;
    
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
        
        ante = new ZoneImpl(this, Zones.ANTE);
        battlefield = new ZoneImpl(this, Zones.BATTLEFIELD);
        exile = new ZoneImpl(this, Zones.EXILE);
        stack = new ZoneImpl(this, Zones.STACK);
        
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
        return ante;
    }
    
    public Zone getBattlefield() {
        return battlefield;
    }
    
    public Zone getExile() {
        return exile;
    }
    
    public SortedZone getStack() {
        return stack;
    }
    
    public Zone getZone(Zones type) {
        switch(type) {
            case ANTE:
                return getAnte();
            case BATTLEFIELD:
                return getBattlefield();
            case EXILE:
                return getExile();
            case STACK:
                return getStack();
            default:
                throw new IllegalArgumentException("zone == " + type);
        }
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
