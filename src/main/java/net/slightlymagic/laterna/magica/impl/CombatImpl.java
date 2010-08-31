/**
 * CombatImpl.java
 * 
 * Created on 16.08.2010
 */

package net.slightlymagic.laterna.magica.impl;


import static java.lang.String.*;
import static java.util.Collections.*;
import static net.slightlymagic.beans.relational.Relations.*;
import static net.slightlymagic.laterna.magica.card.State.StateType.*;
import static net.slightlymagic.laterna.magica.impl.CombatUtil.*;

import java.beans.PropertyChangeListener;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.slightlymagic.beans.properties.Property;
import net.slightlymagic.beans.relational.ManySide;
import net.slightlymagic.beans.relational.ManyToMany;
import net.slightlymagic.beans.relational.OneSide;
import net.slightlymagic.laterna.magica.Combat;
import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.action.GameAction;
import net.slightlymagic.laterna.magica.action.turnBased.TurnBasedAction;
import net.slightlymagic.laterna.magica.action.turnBased.TurnBasedAction.Type;
import net.slightlymagic.laterna.magica.card.CardObject;
import net.slightlymagic.laterna.magica.cost.impl.DummyCostFunction;
import net.slightlymagic.laterna.magica.effects.damage.DamagePermanentEvent;
import net.slightlymagic.laterna.magica.effects.damage.DamagePlayerEvent;
import net.slightlymagic.laterna.magica.player.Player;

import com.google.common.collect.AbstractIterator;


/**
 * The class CombatImpl.
 * 
 * @version V0.0 16.08.2010
 * @author Clemens Koza
 */
public class CombatImpl extends AbstractGameContent implements Combat {
    private static <T> boolean equal(Collection<? extends T> c1, Collection<? extends T> c2) {
        return c1.containsAll(c2) && c2.containsAll(c1);
    }
    
    private abstract class Combatant extends AbstractGameContent {
        private Property<Boolean> removedFromCombat = properties.property("removedFromCombat", false);
        
        public Combatant() {
            super(CombatImpl.this.getGame());
        }
        
        public void setRemovedFromCombat(boolean removedFromCombat) {
            log.debug("Remove from combat: " + this + " --> " + removedFromCombat);
            this.removedFromCombat.setValue(removedFromCombat);
        }
        
        public boolean isRemovedFromCombat() {
            return removedFromCombat.getValue();
        }
        
        @Override
        public void addPropertyChangeListener(PropertyChangeListener listener) {
            s.addPropertyChangeListener(listener);
        }
        
        @Override
        public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
            s.addPropertyChangeListener(propertyName, listener);
        }
        
        @Override
        public void removePropertyChangeListener(PropertyChangeListener listener) {
            s.removePropertyChangeListener(listener);
        }
        
        @Override
        public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
            s.removePropertyChangeListener(propertyName, listener);
        }
    }
    
    private abstract class CreatureCombatant<A, B> extends Combatant {
        private ManyToMany<A, B, BlockAssignmentImpl> combatant;
        private CardObject                            creature;
        private Property<List<B>>                     order = properties.property("damageAssignmentOrder");
        
        @SuppressWarnings({"unchecked", "rawtypes"})
        protected CreatureCombatant(CardObject creature, String otherSide) {
            combatant = (ManyToMany) manyToMany(properties, otherSide, this);
            setCreature(creature);
        }
        
        protected ManyToMany<A, B, BlockAssignmentImpl> getCombatant() {
            return combatant;
        }
        
        protected void setCreature(CardObject creature) {
            this.creature = creature;
        }
        
        protected CardObject getCreature() {
            return creature;
        }
        
        protected Map<? extends B, ? extends BlockAssignmentImpl> getOtherCreatures() {
            return combatant.getOtherSideValues();
        }
        
        protected void setOrder(List<? extends B> otherCreatures) {
            if(!equal(otherCreatures, getOtherCreatures().keySet())) throw new IllegalArgumentException(
                    "Damage assignment order must contain all creatures");
            log.debug("Set DAO: " + this + " --> " + otherCreatures);
            order.setValue(unmodifiableList(new ArrayList<B>(otherCreatures)));
        }
        
        public List<? extends B> getDamageAssignmentOrder() {
            return order.getValue();
        }
    }
    
    private class AttackerImpl extends CreatureCombatant<AttackerImpl, BlockerImpl> implements Attacker {
        private ManySide<AttackerImpl, DefenderImpl> defender   = manySide(properties, "defender", this);
        private Property<AttackAssignmentImpl>       assignment = properties.property("attackAssignment");
        
        public AttackerImpl(CardObject creature) {
            super(creature, "blockers");
        }
        
        @Override
        protected void setCreature(CardObject creature) {
            checkAction(TurnBasedAction.Type.DECLARE_ATTACKERS);
            if(!isLegalAttacker(creature)) throw new IllegalArgumentException();
            super.setCreature(creature);
        }
        
        public CardObject getAttacker() {
            return getCreature();
        }
        
        public Map<? extends BlockerImpl, ? extends BlockAssignmentImpl> getBlockers() {
            return getOtherCreatures();
        }
        
        public DefenderImpl getDefender() {
            return defender.getOneSideValue();
        }
        
        @SuppressWarnings("unchecked")
        public void setDamageAssignmentOrder(List<? extends Blocker> blockers) {
            checkAttackerAssignmentOrderPlayer(getAttacker().getController());
            setOrder((List<? extends BlockerImpl>) blockers);
        }
        
        public AttackAssignmentImpl setDefender(DefenderImpl defender) {
            checkAction(TurnBasedAction.Type.DECLARE_ATTACKERS);
            log.debug("Set defender: " + this + " --> " + defender);
            this.defender.setOneSide(defender.defender);
            AttackAssignmentImpl assignment = new AttackAssignmentImpl(this, defender);
            this.assignment.setValue(assignment);
            return assignment;
        }
        
        @Override
        public AttackAssignment getAttackerAssignment() {
            return assignment.getValue();
        }
        
        @Override
        public String toString() {
            return "[Attacker " + getAttacker() + "]";
        }
    }
    
    private class BlockerImpl extends CreatureCombatant<BlockerImpl, AttackerImpl> implements Blocker {
        public BlockerImpl(CardObject creature) {
            super(creature, "attackers");
        }
        
        @Override
        protected void setCreature(CardObject creature) {
            checkAction(TurnBasedAction.Type.DECLARE_BLOCKERS);
            if(!isLegalBlocker(creature)) throw new IllegalArgumentException();
            super.setCreature(creature);
        }
        
        public CardObject getBlocker() {
            return getCreature();
        }
        
        public Map<? extends AttackerImpl, ? extends BlockAssignmentImpl> getAttackers() {
            return getOtherCreatures();
        }
        
        @SuppressWarnings("unchecked")
        public void setDamageAssignmentOrder(List<? extends Attacker> attackers) {
            checkBlockerAssignmentOrderPlayer(getBlocker().getController());
            setOrder((List<? extends AttackerImpl>) attackers);
        }
        
        public BlockAssignmentImpl addAttacker(AttackerImpl attacker) {
            checkAction(TurnBasedAction.Type.DECLARE_BLOCKERS);
            log.debug("Add attacker: " + this + " --> " + attacker);
            if(attacker.getDefender().getPlayer() != getBlocker().getController()) throw new IllegalArgumentException();
            BlockAssignmentImpl assignment = new BlockAssignmentImpl(attacker, this);
            getCombatant().add(attacker.getCombatant(), assignment);
            return assignment;
        }
        
        @Override
        public String toString() {
            return "[Blocker " + getBlocker() + "]";
        }
    }
    
    private abstract class DefenderImpl extends Combatant implements Defender {
        private OneSide<DefenderImpl, AttackerImpl> defender = oneSide(properties, "attackers", this);
        
        @Override
        public Map<? extends AttackerImpl, ? extends AttackAssignmentImpl> getAttackers() {
            return new AbstractMap<AttackerImpl, AttackAssignmentImpl>() {
                private final Set<Entry<AttackerImpl, AttackAssignmentImpl>> entrySet;
                
                {
                    entrySet = new AbstractSet<Entry<AttackerImpl, AttackAssignmentImpl>>() {
                        @Override
                        public int size() {
                            return defender.getManySideValues().size();
                        }
                        
                        @Override
                        public Iterator<Entry<AttackerImpl, AttackAssignmentImpl>> iterator() {
                            return new AbstractIterator<Entry<AttackerImpl, AttackAssignmentImpl>>() {
                                private Iterator<AttackerImpl> delegate = defender.getManySideValues().iterator();
                                
                                @Override
                                protected Entry<AttackerImpl, AttackAssignmentImpl> computeNext() {
                                    if(!delegate.hasNext()) return endOfData();
                                    final AttackerImpl e = delegate.next();
                                    return new Entry<AttackerImpl, AttackAssignmentImpl>() {
                                        public AttackerImpl getKey() {
                                            return e;
                                        }
                                        
                                        public AttackAssignmentImpl getValue() {
                                            return e.assignment.getValue();
                                        }
                                        
                                        public AttackAssignmentImpl setValue(AttackAssignmentImpl value) {
                                            throw new UnsupportedOperationException();
                                        }
                                        
                                        @Override
                                        public int hashCode() {
                                            Object key = getKey(), val = getValue();
                                            return (key == null? 0:key.hashCode())
                                                    ^ (val == null? 0:val.hashCode());
                                        }
                                        
                                        @Override
                                        public boolean equals(Object o) {
                                            if(!(o instanceof Entry<?, ?>)) return false;
                                            Entry<?, ?> other = (Entry<?, ?>) o;
                                            
                                            Object key = getKey(), val = getValue();
                                            if(key == null? other.getKey() != null:!key.equals(other.getKey())) return false;
                                            if(val == null? other.getKey() != null:!val.equals(other.getKey())) return false;
                                            return true;
                                        }
                                    };
                                }
                            };
                        };
                    };
                }
                
                @Override
                public Set<Entry<AttackerImpl, AttackAssignmentImpl>> entrySet() {
                    return entrySet;
                }
            };
        }
    }
    
    private class PlayerDefenderImpl extends DefenderImpl implements PlayerDefender {
        private Player defender;
        
        public PlayerDefenderImpl(Player defender) {
            if(!isLegalDefendingPlayer(defender)) throw new IllegalArgumentException();
            this.defender = defender;
        }
        
        @Override
        public Player getDefendingPlayer() {
            return defender;
        }
        
        @Override
        public Player getPlayer() {
            return defender;
        }
        
        @Override
        public String toString() {
            return "[PlayerDefender " + getDefendingPlayer() + "]";
        }
    }
    
    private class PlaneswalkerDefenderImpl extends DefenderImpl implements PlaneswalkerDefender {
        private CardObject defender;
        
        public PlaneswalkerDefenderImpl(CardObject defender) {
            if(!isLegalDefendingPlaneswalker(defender)) throw new IllegalArgumentException();
            this.defender = defender;
        }
        
        @Override
        public CardObject getDefendingPlaneswalker() {
            return defender;
        }
        
        @Override
        public Player getPlayer() {
            return defender.getController();
        }
        
        @Override
        public String toString() {
            return "[PlaneswalkerDefender " + getDefendingPlaneswalker() + "]";
        }
    }
    
    private class AttackAssignmentImpl implements AttackAssignment {
        private AttackerImpl      attacker;
        private DefenderImpl      defender;
        
        private Property<Integer> attackerDamage = properties.property("attackerDamage", 0);
        
        public AttackAssignmentImpl(AttackerImpl attacker, DefenderImpl defender) {
            this.attacker = attacker;
            this.defender = defender;
        }
        
        public AttackerImpl getAttacker() {
            return attacker;
        }
        
        public DefenderImpl getDefender() {
            return defender;
        }
        
        private void resetAttackerAssignedDamage() {
            log.debug("Resetting damage assignment: " + attacker);
            attackerDamage.setValue(0);
        }
        
        @Override
        public void setAttackerAssignedDamage(int amount) {
            checkAttackerAssignmentAttacker(getAttacker());
            log.debug("Setting damage assignment: " + attacker + " --> " + amount);
            attackerDamage.setValue(amount <= 0? 0:amount);
        }
        
        @Override
        public int getAttackerAssignedDamage() {
            return attackerDamage.getValue();
        }
        
        public void addPropertyChangeListener(PropertyChangeListener listener) {
            s.addPropertyChangeListener(listener);
        }
        
        public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
            s.addPropertyChangeListener(propertyName, listener);
        }
        
        public void removePropertyChangeListener(PropertyChangeListener listener) {
            s.removePropertyChangeListener(listener);
        }
        
        public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
            s.removePropertyChangeListener(propertyName, listener);
        }
    }
    
    private class BlockAssignmentImpl implements BlockAssignment {
        private AttackerImpl      attacker;
        private BlockerImpl       blocker;
        
        private Property<Integer> attackerDamage = properties.property("attackerDamage", 0);
        private Property<Integer> blockerDamage  = properties.property("blockerDamage", 0);
        
        public BlockAssignmentImpl(AttackerImpl attacker, BlockerImpl blocker) {
            this.attacker = attacker;
            this.blocker = blocker;
        }
        
        public AttackerImpl getAttacker() {
            return attacker;
        }
        
        public BlockerImpl getBlocker() {
            return blocker;
        }
        
        private void resetAttackerAssignedDamage() {
            log.debug("Resetting damage assignment: " + attacker);
            attackerDamage.setValue(0);
        }
        
        @Override
        public void setAttackerAssignedDamage(int amount) {
            checkAttackerAssignmentAttacker(getAttacker());
            log.debug("Setting damage assignment: " + attacker + " --> " + amount);
            attackerDamage.setValue(amount <= 0? 0:amount);
        }
        
        @Override
        public int getAttackerAssignedDamage() {
            return attackerDamage.getValue();
        }
        
        private void resetBlockerAssignedDamage() {
            log.debug("Resetting damage assignment: " + blocker);
            blockerDamage.setValue(0);
        }
        
        @Override
        public void setBlockerAssignedDamage(int amount) {
            checkBlockerAssignmentBlocker(getBlocker());
            log.debug("Setting damage assignment: " + blocker + " --> " + amount);
            blockerDamage.setValue(amount <= 0? 0:amount);
        }
        
        @Override
        public int getBlockerAssignedDamage() {
            return blockerDamage.getValue();
        }
        
        public void addPropertyChangeListener(PropertyChangeListener listener) {
            s.addPropertyChangeListener(listener);
        }
        
        public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
            s.addPropertyChangeListener(propertyName, listener);
        }
        
        public void removePropertyChangeListener(PropertyChangeListener listener) {
            s.removePropertyChangeListener(listener);
        }
        
        public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
            s.removePropertyChangeListener(propertyName, listener);
        }
    }
    
    private Map<MagicObject, AttackerImpl> attackers, attackersView;
    private Map<MagicObject, BlockerImpl>  blockers, blockersView;
    private Map<Object, DefenderImpl>      defenders, defendersView;
    
    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        s.addPropertyChangeListener(listener);
    }
    
    @Override
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        s.addPropertyChangeListener(propertyName, listener);
    }
    
    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        s.removePropertyChangeListener(listener);
    }
    
    @Override
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        s.removePropertyChangeListener(propertyName, listener);
    }
    
    public CombatImpl(Game game) {
        super(game);
        
        attackers = properties.map("attackers");
        attackersView = unmodifiableMap(attackers);
        blockers = properties.map("blockers");
        blockersView = unmodifiableMap(blockers);
        defenders = properties.map("defenders");
        defendersView = unmodifiableMap(defenders);
    }
    
    //Attackers
    
    public Attacker declareAttacker(CardObject attackingCreature) {
        checkAction(TurnBasedAction.Type.DECLARE_ATTACKERS);
        if(attackers.keySet().contains(attackingCreature)) throw new IllegalStateException(
                "Creature is already attacking");
        AttackerImpl a = new AttackerImpl(attackingCreature);
        attackers.put(attackingCreature, a);
        return a;
    }
    
    public AttackAssignment assignAttacker(Attacker attacker, Defender defender) {
        checkAction(TurnBasedAction.Type.DECLARE_ATTACKERS);
        if(!attackers.values().contains(attacker)) throw new IllegalStateException("Creature is not attacking");
        if(!defenders.values().contains(defender)) throw new IllegalStateException("Illegal defender");
        return ((AttackerImpl) attacker).setDefender((DefenderImpl) defender);
    }
    
    public AttackerImpl getAttacker(CardObject attacker) {
        AttackerImpl a = attackers.get(attacker);
        //check legality only after looking up the attacker.
        //A creature already attacking may become illegal, but still be attacking.
        if(a == null && !isLegalAttacker(attacker)) throw new IllegalArgumentException(valueOf(attacker));
        return a;
    }
    
    public Collection<? extends Attacker> getAttackers() {
        return attackersView.values();
    }
    
    public void removeFromCombat(Attacker attacker) {
        if(!attackers.values().contains(attacker)) throw new IllegalStateException("Creature is not attacking");
        ((AttackerImpl) attacker).setRemovedFromCombat(true);
    }
    
    //Attackers
    
    //Blockers
    
    public Blocker declareBlocker(CardObject blockingCreature) {
        checkAction(TurnBasedAction.Type.DECLARE_BLOCKERS);
        if(blockers.keySet().contains(blockingCreature)) throw new IllegalStateException(
                "Creature is already blocking");
        BlockerImpl b = new BlockerImpl(blockingCreature);
        blockers.put(blockingCreature, b);
        return b;
    }
    
    public BlockAssignment assignBlocker(Blocker blocker, Attacker attacker) {
        checkAction(TurnBasedAction.Type.DECLARE_BLOCKERS);
        if(!blockers.values().contains(blocker)) throw new IllegalStateException("Creature is not blocking");
        if(!attackers.values().contains(attacker)) throw new IllegalStateException("Creature is not attacking");
        return ((BlockerImpl) blocker).addAttacker((AttackerImpl) attacker);
    }
    
    public BlockerImpl getBlocker(CardObject blocker) {
        BlockerImpl b = blockers.get(blocker);
        //check legality only after looking up the blocker.
        //A creature already blocking may become illegal, but still be blocking.
        if(b == null && !isLegalBlocker(blocker)) throw new IllegalArgumentException(valueOf(blocker));
        return b;
    }
    
    public Collection<? extends Blocker> getBlockers() {
        return blockersView.values();
    }
    
    public void removeFromCombat(Blocker blocker) {
        ((BlockerImpl) blocker).setRemovedFromCombat(true);
    }
    
    //Blockers
    
    //Defenders
    
    public PlaneswalkerDefender getDefender(CardObject defender) {
        PlaneswalkerDefenderImpl d = (PlaneswalkerDefenderImpl) defenders.get(defender);
        if(d == null) {
            if(!isLegalDefendingPlaneswalker(defender)) throw new IllegalArgumentException(valueOf(defender));
            defenders.put(defender, d = new PlaneswalkerDefenderImpl(defender));
        }
        return d;
    }
    
    public PlayerDefender getDefender(Player defender) {
        PlayerDefenderImpl d = (PlayerDefenderImpl) defenders.get(defender);
        if(d == null) {
            if(!isLegalDefendingPlayer(defender)) throw new IllegalArgumentException(valueOf(defender));
            defenders.put(defender, d = new PlayerDefenderImpl(defender));
        }
        return d;
    }
    
    public Collection<? extends Defender> getDefenders() {
        //ensure that all defenders are contained
        for(Player p:getGame().getPlayers())
            if(isLegalDefendingPlayer(p)) getDefender(p);
        for(MagicObject o:getGame().getBattlefield().getCards())
            if(isLegalDefendingPlaneswalker((CardObject) o)) getDefender((CardObject) o);
        return defendersView.values();
    }
    
    public void removeFromCombat(Defender defender) {
        ((DefenderImpl) defender).setRemovedFromCombat(true);
    }
    
    //Defenders
    
    public List<Player> getAttackingPlayers() {
        //TODO implement properly
        List<Player> p = new ArrayList<Player>();
        p.add(getGame().getTurnStructure().getActivePlayer());
        return p;
    }
    
    public List<Player> getDefendingPlayers() {
        //TODO implement properly
        List<Player> p = new ArrayList<Player>(getGame().getPlayers());
        for(Iterator<Player> it = p.iterator(); it.hasNext();)
            if(!isLegalDefendingPlayer(it.next())) it.remove();
        return p;
    }
    
    //Lifecycle
    /* These methods implement the flow of the combat phase. More precisely, they will be called by turn based
     * actions in the right order, where this class is primary for enforcing the rules and the MagicActor for making
     * decisions. Both are mediated by the turn based actions.
     */

    //controls for what modifications are legal
    private Property<TurnBasedAction.Type> action   = properties.property("action");
    private Property<Player>               attDAO   = properties.property("attDAO");
    private Property<Player>               blockDAO = properties.property("blockDAO");
    private Property<AttackerImpl>         attDA    = properties.property("attDA");
    private Property<BlockerImpl>          blockDA  = properties.property("blockDA");
    
    public void setAction(TurnBasedAction.Type action) {
        switch(action) {
            case DEFENDER:
            case DECLARE_ATTACKERS:
            case DECLARE_BLOCKERS:
            case ORDER_BLOCKERS:
            case ORDER_ATTACKERS:
            case DAMAGE_ASSIGNMENT:
            case DAMAGE_DEALING:
            break;
            default:
                throw new IllegalArgumentException(valueOf(action));
        }
        log.debug("Entering " + action);
        this.action.setValue(action);
    }
    
    public void setAttackerAssignmentOrderPlayer(Player p) {
        if(!getAttackingPlayers().contains(p)) throw new IllegalArgumentException(valueOf(p));
        log.debug("Attacker assignment order " + p);
        attDAO.setValue(p);
    }
    
    public void setBlockerAssignmentOrderPlayer(Player p) {
        if(!getDefendingPlayers().contains(p)) throw new IllegalArgumentException(valueOf(p));
        log.debug("Blocker assignment order " + p);
        blockDAO.setValue(p);
    }
    
    public void setAttackerAssignmentAttacker(Attacker a) {
        if(!getAttackers().contains(a)) throw new IllegalArgumentException(valueOf(a));
        log.debug("Attacker assignment " + a);
        attDA.setValue((AttackerImpl) a);
    }
    
    public void setBlockerAssignmentBlocker(Blocker b) {
        if(!getBlockers().contains(b)) throw new IllegalArgumentException(valueOf(b));
        log.debug("Blocker assignment " + b);
        blockDA.setValue((BlockerImpl) b);
    }
    
    private <T> void check(T actual, T expected) {
        if(expected == null? actual != null:!expected.equals(actual)) {
            throw new IllegalStateException("Expected: " + expected + ", actual: " + actual);
        }
    }
    
    private void checkAction(TurnBasedAction.Type action) {
        check(this.action.getValue(), action);
    }
    
    private void checkAttackerAssignmentOrderPlayer(Player p) {
        checkAction(Type.ORDER_BLOCKERS);
        check(attDAO.getValue(), p);
    }
    
    private void checkBlockerAssignmentOrderPlayer(Player p) {
        checkAction(Type.ORDER_ATTACKERS);
        check(blockDAO.getValue(), p);
    }
    
    private void checkAttackerAssignmentAttacker(AttackerImpl a) {
        checkAction(Type.DAMAGE_ASSIGNMENT);
        check(attDA.getValue(), a);
    }
    
    private void checkBlockerAssignmentBlocker(BlockerImpl b) {
        checkAction(Type.DAMAGE_ASSIGNMENT);
        check(blockDA.getValue(), b);
    }
    
    //Beginning step
    
    @Override
    public void setDefendingPlayers(Set<Player> defenders) {
        checkAction(TurnBasedAction.Type.DEFENDER);
        log.debug("Setting defending players");
        //TODO implement
    }
    
    //Declare Attackers Step
    
    public boolean isLegalAttackers(Player attacker) {
        checkAction(TurnBasedAction.Type.DECLARE_ATTACKERS);
        log.debug("Checking legal attackers for " + attacker);
        //check whether every attackerDamage has a defender assigned
        for(Attacker a:getAttackers()) {
            if(a.isRemovedFromCombat() || a.getAttacker().getController() != attacker) continue;
            if(a.getDefender() == null) return false;
            //TODO implement restrictions & requirements
            //although this is in the loop, don't forget for blocking requirements
            //i.e. creatures not even checked in this loop
        }
        return true;
    }
    
    public void tapAttackers(Player attacker) {
        checkAction(TurnBasedAction.Type.DECLARE_ATTACKERS);
        log.debug("Tapping attackers for " + attacker);
        //TODO respect Vigilance
        for(Attacker a:getAttackers()) {
            if(a.isRemovedFromCombat() || a.getAttacker().getController() != attacker) continue;
            a.getAttacker().getState().setState(TAPPED, true);
        }
    }
    
    public GameAction getAttackersCost(Player attacker) {
        checkAction(TurnBasedAction.Type.DECLARE_ATTACKERS);
        log.debug("Calculating attacking cost for " + attacker);
        
        //TODO implement
        return DummyCostFunction.EMPTY.apply(getGame());
    }
    
    //Declare Blockers Step
    
    public boolean isLegalBlockers(Player defender) {
        checkAction(TurnBasedAction.Type.DECLARE_BLOCKERS);
        log.debug("Checking legal blockers for " + defender);
        //check whether every blockerDamage has at least one attackerDamage assigned
        for(Blocker b:getBlockers()) {
            if(b.isRemovedFromCombat() || b.getBlocker().getController() != defender) continue;
            if(b.getAttackers().isEmpty()) return false;
            //TODO implement restrictions & requirements
            //although this is in the loop, don't forget for blocking requirements
            //i.e. creatures not even checked in this loop
            if(b.getAttackers().size() > 1) return false;
        }
        return true;
    }
    
    public GameAction getBlockersCost(Player defender) {
        checkAction(TurnBasedAction.Type.DECLARE_BLOCKERS);
        log.debug("Calculating blocking cost for " + defender);
        //TODO implement
        return DummyCostFunction.EMPTY.apply(getGame());
    }
    
    public boolean isLegalAttackersAssignmentOrder(Player attacker) {
        checkAction(TurnBasedAction.Type.ORDER_BLOCKERS);
        log.debug("Checking legal attacker DAO for " + attacker);
        for(AttackerImpl a:attackers.values()) {
            if(a.isRemovedFromCombat() || a.getAttacker().getController() != attacker) continue;
            
            //checks whether the attackerDamage has an order assigned
            //setting the order checks if all blockers were assigned
            if(a.getDamageAssignmentOrder() == null) return false;
        }
        return true;
    }
    
    public boolean isLegalBlockersAssignmentOrder(Player defender) {
        checkAction(TurnBasedAction.Type.ORDER_ATTACKERS);
        log.debug("Checking legal blocker DAO for " + defender);
        for(BlockerImpl b:blockers.values()) {
            if(b.isRemovedFromCombat() || b.getBlocker().getController() != defender) continue;
            
            //checks whether the blockerDamage has an order assigned
            //setting the order checks if all attackers were assigned
            if(b.getDamageAssignmentOrder() == null) return false;
        }
        return true;
    }
    
    //Combat Damage Step
    
    private static enum CombatDamageStep {
        BEFORE, FIRST, BETWEEN, REGULAR, AFTER
    }
    
    private Property<CombatDamageStep> firstStrike       = properties.property("firstStrike",
                                                                 CombatDamageStep.BEFORE);
    
    private Set<AttackerImpl>          attackersThisStep = properties.set("attackersThisStep");
    private Set<BlockerImpl>           blockersThisStep  = properties.set("blockersThisStep");
    
    @Override
    public void startCombatDamageStep() {
        checkAction(TurnBasedAction.Type.DAMAGE_ASSIGNMENT);
        switch(firstStrike.getValue()) {
            case BEFORE:
                /* 510.5.: If at least one attacking or blocking creature has first strike (see rule 702.7) or
                 * double strike (see rule 702.4) as the combat damage step begins, the only creatures that assign
                 * combat damage in that step are those with first strike or double strike.
                 */

                for(AttackerImpl a:attackers.values())
                    if(dealsDamage(a, true)) attackersThisStep.add(a);
                for(BlockerImpl b:blockers.values())
                    if(dealsDamage(b, true)) blockersThisStep.add(b);
                
                if(!attackersThisStep.isEmpty() && !blockersThisStep.isEmpty()) {
                    firstStrike.setValue(CombatDamageStep.FIRST);
                    log.debug("Starting first strike combat damage step");
                    break;
                }
                //Drop through if no attackers & blockers
            case BETWEEN:
                /* 510.5.: The only creatures that assign combat damage in that step are the remaining attackers
                 * and blockers that had neither first strike nor double strike as the first combat damage step
                 * began, as well as the remaining attackers and blockers that currently have double strike. After
                 * that step, the phase proceeds to the end of combat step.
                 */

                Set<AttackerImpl> attackers = new HashSet<AttackerImpl>(this.attackers.values());
                for(Iterator<AttackerImpl> it = attackers.iterator(); it.hasNext();) {
                    AttackerImpl a = it.next();
                    if(attackersThisStep.contains(a) && !dealsDamage(a, false)) it.remove();
                }
                attackersThisStep.clear();
                attackersThisStep.addAll(attackers);
                
                Set<BlockerImpl> blockers = new HashSet<BlockerImpl>(this.blockers.values());
                for(Iterator<BlockerImpl> it = blockers.iterator(); it.hasNext();) {
                    BlockerImpl b = it.next();
                    if(blockersThisStep.contains(b) && !dealsDamage(b, false)) it.remove();
                }
                blockersThisStep.clear();
                blockersThisStep.addAll(blockers);
                
                firstStrike.setValue(CombatDamageStep.REGULAR);
                log.debug("Starting regular combat damage step");
            break;
            case AFTER:
                throw new IllegalStateException("Already performed the last damage step");
            default:
                throw new IllegalStateException("Currently performing a damage step");
        }
    }
    
    @Override
    public boolean isFirstStrikeDamageStep() {
        switch(firstStrike.getValue()) {
            case FIRST:
                return true;
            case REGULAR:
                return false;
            default:
                throw new IllegalStateException("startCombatDamageStep was not called");
        }
    }
    
    //Damage Assignment
    
    public boolean isLegalAttackerAssignment(Attacker a) {
        AttackerImpl attacker = (AttackerImpl) a;
        checkAttackerAssignmentAttacker(attacker);
        log.debug("Check legal attacker DA for " + attacker);
        
        //those removed from combat don't deal damage, so no assignment is necessary
        if(attacker.isRemovedFromCombat()) return true;
        
        int damage = getAmmount(attacker);
        for(BlockerImpl blocker:attacker.getDamageAssignmentOrder()) {
            if(blocker.isRemovedFromCombat()) continue;
            //Substract the amount of damage assigned to this blocker
            damage -= attacker.getBlockers().get(blocker).getAttackerAssignedDamage();
            //if the damage to this creature is not lethal but
            //the attacker has not assigned all of its damage it's illegal
            if(!isLethal(blocker) && damage != 0) return false;
        }
        //if we get here, either all damage was assigned, or all blockers were assigned lethal damage
        //the combination damage is left, but some blockers were not assigned lethal damage is impossible
        
        if(attacker.getBlockers().isEmpty() || hasTrample(attacker)) {
            //if the attacker is not blocked or has trample
            damage -= attacker.getAttackerAssignment().getAttackerAssignedDamage();
        } else {
            //otherwise, no damage must be assigned
            if(attacker.getAttackerAssignment().getAttackerAssignedDamage() != 0) return false;
        }
        
        //will happen if too much/little damage was assigned to creatures,
        //or the wrong amount of damage was assigned to the defending player
        return damage == 0;
    }
    
    public boolean isLegalBlockerAssignment(Blocker b) {
        BlockerImpl blocker = (BlockerImpl) b;
        checkBlockerAssignmentBlocker(blocker);
        log.debug("Check legal blocker DA for " + blocker);
        
        //those removed from combat don't deal damage, so no assignment is necessary
        if(blocker.isRemovedFromCombat()) return true;
        
        int damage = getAmmount(blocker);
        for(AttackerImpl attacker:blocker.getDamageAssignmentOrder()) {
            if(attacker.isRemovedFromCombat()) continue;
            //Substract the amount of damage assigned to this attacker
            damage -= blocker.getAttackers().get(attacker).getBlockerAssignedDamage();
            //if the damage to this creature is not lethal but
            //the blocker has not assigned all of its damage it's illegal
            if(!isLethal(attacker) && damage != 0) return false;
        }
        //if we get here, either all damage was assigned, or all attackers were assigned lethal damage
        //the combination damage is left, but some attackers were not assigned lethal damage is impossible
        
        //will happen if too much/little damage was assigned to creatures
        return damage == 0;
    }
    
    //Damage dealing
    
    @Override
    public void dealDamage() {
        checkAction(Type.DAMAGE_DEALING);
        log.debug("Dealing damage");
        
        //TODO do this simultaneously
        for(Attacker a:getAttackers()) {
            if(a.isRemovedFromCombat()) continue;
            for(Entry<? extends Blocker, ? extends BlockAssignment> e:a.getBlockers().entrySet()) {
                Blocker b = e.getKey();
                BlockAssignment ass = e.getValue();
                if(b.isRemovedFromCombat()) continue;
                //assign attacker -> blocker damage
                new DamagePermanentEvent(b.getBlocker(), a.getAttacker(), ass.getAttackerAssignedDamage(), true,
                        true).execute();
                //assign blocker -> attacker damage
                new DamagePermanentEvent(a.getAttacker(), b.getBlocker(), ass.getBlockerAssignedDamage(), true,
                        true).execute();
            }
            
            Defender d = a.getDefender();
            if(d.isRemovedFromCombat()) continue;
            

            //assign attacker -> defender damage
            AttackAssignment ass = a.getAttackerAssignment();
            if(d instanceof PlaneswalkerDefender) {
                new DamagePermanentEvent(((PlaneswalkerDefender) d).getDefendingPlaneswalker(), a.getAttacker(),
                        ass.getAttackerAssignedDamage(), true, true).execute();
            } else if(d instanceof PlayerDefender) {
                new DamagePlayerEvent(((PlayerDefender) d).getDefendingPlayer(), a.getAttacker(),
                        ass.getAttackerAssignedDamage(), true, true).execute();
            } else throw new AssertionError(d);
        }
    }
    
    @Override
    public boolean nextDamageStep() {
        //reset all assigned damage
        for(AttackerImpl a:attackers.values()) {
            a.assignment.getValue().resetAttackerAssignedDamage();
            for(BlockAssignmentImpl b:a.getBlockers().values()) {
                b.resetAttackerAssignedDamage();
                b.resetBlockerAssignedDamage();
            }
        }
        
        switch(firstStrike.getValue()) {
            case FIRST:
                firstStrike.setValue(CombatDamageStep.BETWEEN);
                return true;
            case REGULAR:
                firstStrike.setValue(CombatDamageStep.AFTER);
                return false;
            case AFTER:
                throw new IllegalStateException("Already performed the last damage step");
            default:
                throw new IllegalStateException("startCombatDamageStep was not called");
        }
    }
}
