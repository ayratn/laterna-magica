/**
 * CombatImpl.java
 * 
 * Created on 14.07.2010
 */

package net.slightlymagic.laterna.magica.impl;


import static com.google.common.base.Predicates.*;
import static com.google.common.base.Suppliers.*;
import static java.util.Collections.*;
import static net.slightlymagic.laterna.magica.characteristics.CardType.*;
import static net.slightlymagic.laterna.magica.util.MagicaCollections.*;
import static net.slightlymagic.laterna.magica.util.MagicaPredicates.*;
import static net.slightlymagic.laterna.magica.util.MagicaSuppliers.*;
import static net.slightlymagic.laterna.magica.zone.Zone.Zones.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.slightlymagic.laterna.magica.Combat;
import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.edit.CompoundEdit;
import net.slightlymagic.laterna.magica.player.Player;


import com.google.common.base.Predicate;
import com.google.common.base.Supplier;


/**
 * The class CombatImpl.
 * 
 * @version V0.0 14.07.2010
 * @author Clemens Koza
 */
public class CombatImpl extends AbstractGameContent implements Combat {
    private final Supplier<Player>       activePlayer  = active(game(ofInstance(this)));
    
    @SuppressWarnings("unchecked")
    private final Predicate<MagicObject> legalAttacker = and(isIn(ofInstance(BATTLEFIELD)), card(has(CREATURE)),
                                                               controller(activePlayer));
    
    @SuppressWarnings("unchecked")
    private final Predicate<MagicObject> legalBlocker  = and(isIn(ofInstance(BATTLEFIELD)), card(has(CREATURE)),
                                                               not(controller(activePlayer)));
    
    @SuppressWarnings("unchecked")
    private final Predicate<MagicObject> legalDefPw    = and(isIn(ofInstance(BATTLEFIELD)),
                                                               card(has(PLANESWALKER)),
                                                               not(controller(activePlayer)));
    
    //TODO consider teams
    private final Predicate<Player>      legalDefPl    = not(equalTo(activePlayer.get()));
    
    private Map<MagicObject, AttackerImpl> attackers, attackersView;
    private Map<MagicObject, BlockerImpl>  blockers, blockersView;
    private Map<Object, AbstractDefender>  defenders, defendersView;
    
    public CombatImpl(Game game) {
        super(game);
        attackers = editableMap(getGame(), new HashMap<MagicObject, AttackerImpl>());
        attackersView = unmodifiableMap(attackers);
        blockers = editableMap(getGame(), new HashMap<MagicObject, BlockerImpl>());
        blockersView = unmodifiableMap(blockers);
        defenders = editableMap(getGame(), new HashMap<Object, AbstractDefender>());
        defendersView = unmodifiableMap(defenders);
    }
    
    //Attackers
    
    public Attacker declareAttacker(MagicObject attackingCreature) {
        if(attackers.keySet().contains(attackingCreature)) throw new IllegalStateException(
                "Creature is already attacking");
        AttackerImpl a = new AttackerImpl(attackingCreature);
        attackers.put(attackingCreature, a);
        return a;
    }
    
    public void assignAttacker(Attacker attacker, Defender defender) {
        if(!attackers.values().contains(attacker)) throw new IllegalStateException("Creature is not attacking");
        ((AttackerImpl) attacker).setDefender((AbstractDefender) defender);
    }
    
    public AttackerImpl getAttacker(MagicObject attacker) {
        if(!legalAttacker.apply(attacker)) throw new IllegalArgumentException();
        return attackers.get(attacker);
    }
    
    public Collection<? extends Attacker> getAttackers() {
        return attackersView.values();
    }
    
    public void removeFromCombat(Attacker attacker) {
        ((AttackerImpl) attacker).setRemovedFromCombat(true);
    }
    
    //Attackers
    
    //Blockers
    
    public Blocker declareBlocker(MagicObject blockingCreature) {
        if(blockers.keySet().contains(blockingCreature)) throw new IllegalStateException(
                "Creature is already blocking");
        BlockerImpl b = new BlockerImpl(blockingCreature);
        blockers.put(blockingCreature, b);
        return b;
    }
    
    public void assignBlocker(Blocker blocker, Attacker attacker) {
        if(!blockers.values().contains(blocker)) throw new IllegalStateException("Creature is not attacking");
        ((BlockerImpl) blocker).addAttacker((AttackerImpl) attacker);
    }
    
    public BlockerImpl getBlocker(MagicObject blocker) {
        if(!legalBlocker.apply(blocker)) throw new IllegalArgumentException();
        return blockers.get(blocker);
    }
    
    public Collection<? extends Blocker> getBlockers() {
        return blockersView.values();
    }
    
    public void removeFromCombat(Blocker blocker) {
        ((BlockerImpl) blocker).setRemovedFromCombat(true);
    }
    
    //Blockers
    
    //Defenders
    
    public AbstractDefender getDefender(MagicObject defender) {
        if(!legalDefPw.apply(defender)) throw new IllegalArgumentException();
        AbstractDefender d = defenders.get(defender);
        if(d == null) defenders.put(defender, d = new PlaneswalkerDefenderImpl(defender));
        return d;
    }
    
    public AbstractDefender getDefender(Player defender) {
        if(!legalDefPl.apply(defender)) throw new IllegalArgumentException();
        AbstractDefender d = defenders.get(defender);
        if(d == null) defenders.put(defender, d = new PlayerDefenderImpl(defender));
        return d;
    }
    
    public Collection<? extends Defender> getDefenders() {
        //ensure that all defenders are contained
        for(Player p:getGame().getPlayers())
            if(legalDefPl.apply(p)) getDefender(p);
        for(MagicObject o:getGame().getBattlefield().getCards())
            if(legalDefPw.apply(o)) getDefender(o);
        return defendersView.values();
    }
    
    public void removeFromCombat(Defender defender) {
        ((AbstractDefender) defender).setRemovedFromCombat(true);
    }
    
    //Defenders
    
    public List<Player> getAttackingPlayers() {
        //TODO implement properly
        List<Player> p = new ArrayList<Player>();
        p.add(activePlayer.get());
        return p;
    }
    
    public List<Player> getDefendingPlayers() {
        //TODO implement properly
        List<Player> p = new ArrayList<Player>(getGame().getPlayers());
        p.remove(activePlayer.get());
        return p;
    }
    
    
    private class AttackerImpl implements Attacker {
        private MagicObject       attacker;
        private boolean           removedFromCombat;
        private List<BlockerImpl> blockers = editableList(getGame(), new ArrayList<BlockerImpl>());
        private List<BlockerImpl> view     = unmodifiableList(blockers);
        private AbstractDefender  defender;
        
        public AttackerImpl(MagicObject attacker) {
            setAttacker(attacker);
        }
        
        private void setAttacker(MagicObject attacker) {
            this.attacker = attacker;
        }
        
        public MagicObject getAttacker() {
            return attacker;
        }
        
        private void addBlocker(BlockerImpl b) {
            if(!b.attackers.contains(this)) {
                CompoundEdit ed = new CompoundEdit(getGame(), true, "assign blocker");
                b.attackers.add(this);
                blockers.add(b);
                ed.end();
            }
        }
        
        private void removeBlocker(BlockerImpl b) {
            if(b.attackers.contains(this)) {
                CompoundEdit ed = new CompoundEdit(getGame(), true, "remove blocker assignment");
                b.attackers.remove(this);
                blockers.remove(b);
                ed.end();
            }
        }
        
        public List<BlockerImpl> getBlockers() {
            return view;
        }
        
        @SuppressWarnings("unchecked")
        public void setDamageAssignmentOrder(List<? extends Blocker> blockers) {
            order(this.blockers, (List<BlockerImpl>) blockers);
        }
        
        private void setRemovedFromCombat(boolean removedFromCombat) {
            this.removedFromCombat = removedFromCombat;
        }
        
        public boolean isRemovedFromCombat() {
            return removedFromCombat;
        }
        
        private void setDefender(AbstractDefender defender) {
            if(this.defender != null) this.defender.attackers.remove(this);
            this.defender = defender;
            if(this.defender != null) this.defender.attackers.add(this);
        }
        
        public Defender getDefender() {
            return defender;
        }
    }
    
    private class BlockerImpl implements Blocker {
        private MagicObject        blocker;
        private boolean            removedFromCombat;
        private List<AttackerImpl> attackers = editableList(getGame(), new ArrayList<AttackerImpl>());
        private List<AttackerImpl> view      = unmodifiableList(attackers);
        
        public BlockerImpl(MagicObject blocker) {
            setBlocker(blocker);
        }
        
        public void setBlocker(MagicObject blocker) {
            this.blocker = blocker;
        }
        
        public MagicObject getBlocker() {
            return blocker;
        }
        
        private void addAttacker(AttackerImpl a) {
            if(!a.blockers.contains(this)) {
                CompoundEdit ed = new CompoundEdit(getGame(), true, "assign blocker");
                a.blockers.add(this);
                attackers.add(a);
                ed.end();
            }
        }
        
        private void removeAttacker(AttackerImpl a) {
            if(a.blockers.contains(this)) {
                CompoundEdit ed = new CompoundEdit(getGame(), true, "assign blocker");
                a.blockers.remove(this);
                attackers.remove(a);
                ed.end();
            }
        }
        
        public List<AttackerImpl> getAttackers() {
            return view;
        }
        
        @SuppressWarnings("unchecked")
        public void setDamageAssignmentOrder(List<? extends Attacker> attackers) {
            order(this.attackers, (List<AttackerImpl>) attackers);
        }
        
        private void setRemovedFromCombat(boolean removedFromCombat) {
            this.removedFromCombat = removedFromCombat;
        }
        
        public boolean isRemovedFromCombat() {
            return removedFromCombat;
        }
    }
    
    private abstract class AbstractDefender implements Defender {
        private boolean           removedFromCombat;
        private Set<AttackerImpl> attackers = editableSet(getGame(), new HashSet<AttackerImpl>());
        private Set<AttackerImpl> view      = unmodifiableSet(attackers);
        
        public MagicObject getDefendingPlaneswalker() {
            return null;
        }
        
        public Player getDefendingPlayer() {
            return null;
        }
        
        //adding removing attackers is done via Attacker.setDefender()
        
        public Set<AttackerImpl> getAttackers() {
            return view;
        }
        
        private void setRemovedFromCombat(boolean removedFromCombat) {
            this.removedFromCombat = removedFromCombat;
        }
        
        public boolean isRemovedFromCombat() {
            return removedFromCombat;
        }
    }
    
    private class PlaneswalkerDefenderImpl extends AbstractDefender {
        private MagicObject defender;
        
        public PlaneswalkerDefenderImpl(MagicObject defender) {
            setDefendingPlaneswalker(defender);
        }
        
        private void setDefendingPlaneswalker(MagicObject defender) {
            this.defender = defender;
        }
        
        @Override
        public MagicObject getDefendingPlaneswalker() {
            return defender;
        }
        
        public Player getPlayer() {
            return getDefendingPlaneswalker().getController();
        }
    }
    
    private class PlayerDefenderImpl extends AbstractDefender {
        private Player defender;
        
        public PlayerDefenderImpl(Player defender) {
            setDefendingPlayer(defender);
        }
        
        private void setDefendingPlayer(Player defender) {
            this.defender = defender;
        }
        
        @Override
        public Player getDefendingPlayer() {
            return defender;
        }
        
        public Player getPlayer() {
            return getDefendingPlayer();
        }
    }
    
    private static <T> void order(List<T> to, List<T> from) {
        if(!new HashSet<T>(to).equals(new HashSet<T>(from))) throw new IllegalArgumentException();
        synchronized(to) {
            to.clear();
            to.addAll(from);
        }
    }
}
