/**
 * CombatImpl.java
 * 
 * Created on 16.08.2010
 */

package net.slightlymagic.laterna.magica.impl;


import static com.google.common.base.Predicates.*;
import static com.google.common.base.Suppliers.*;
import static java.util.Collections.*;
import static net.slightlymagic.laterna.magica.characteristics.CardType.*;
import static net.slightlymagic.laterna.magica.util.MagicaCollections.*;
import static net.slightlymagic.laterna.magica.util.MagicaPredicates.*;
import static net.slightlymagic.laterna.magica.util.MagicaSuppliers.*;
import static net.slightlymagic.laterna.magica.util.relational.Relations.*;
import static net.slightlymagic.laterna.magica.zone.Zone.Zones.*;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.Combat;
import net.slightlymagic.laterna.magica.edit.property.EditableProperty;
import net.slightlymagic.laterna.magica.player.Player;
import net.slightlymagic.laterna.magica.util.relational.ManySide;
import net.slightlymagic.laterna.magica.util.relational.ManyToMany;
import net.slightlymagic.laterna.magica.util.relational.OneSide;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.AbstractIterator;


/**
 * The class CombatImpl.
 * 
 * @version V0.0 16.08.2010
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
    

    private final Predicate<MagicObject> legalDefPw    = and(isIn(ofInstance(BATTLEFIELD)),
                                                               card(has(PLANESWALKER)));
    
    //TODO consider teams
    private final Predicate<Player>      legalDefPl    = not(equalTo(activePlayer.get()));
    
    
    private <T> EditableProperty<T> editable(String name, T value) {
        return new EditableProperty<T>(getGame(), null, name, value);
    }
    
    
    private abstract class Combatant {
        private EditableProperty<Boolean> removedFromCombat = editable("removedFromCombat", false);
        
        public void setRemovedFromCombat(boolean removedFromCombat) {
            this.removedFromCombat.setValue(removedFromCombat);
        }
        
        public boolean isRemovedFromCombat() {
            return removedFromCombat.getValue();
        }
    }
    
    private class AttackerImpl extends Combatant implements Attacker {
        private ManyToMany<AttackerImpl, BlockerImpl, BlockAssignmentImpl> attacker   = manyToMany(getGame(), this);
        
        private MagicObject                                                creature;
        private ManySide<AttackerImpl, DefenderImpl>                       defender   = manySide(getGame(), this);
        private EditableProperty<AttackAssignmentImpl>                     assignment = editable(
                                                                                              "attackAssignment",
                                                                                              null);
        
        public AttackerImpl(MagicObject creature) {
            if(!legalAttacker.apply(creature)) throw new IllegalArgumentException();
            this.creature = creature;
        }
        
        public MagicObject getAttacker() {
            return creature;
        }
        
        public Map<? extends BlockerImpl, ? extends BlockAssignmentImpl> getBlockers() {
            return attacker.getOtherSideValues();
        }
        
        public DefenderImpl getDefender() {
            return defender.getOneSideValue();
        }
        
        public void setDamageAssignmentOrder(List<? extends Blocker> blockers) {}
        
        public AttackAssignmentImpl setDefender(DefenderImpl defender) {
            this.defender.setOneSide(defender.defender);
            AttackAssignmentImpl assignment = new AttackAssignmentImpl();
            this.assignment.setValue(assignment);
            return assignment;
        }
    }
    
    private class BlockerImpl extends Combatant implements Blocker {
        private ManyToMany<BlockerImpl, AttackerImpl, BlockAssignmentImpl> blocker = manyToMany(getGame(), this);
        
        private MagicObject                                                creature;
        
        public BlockerImpl(MagicObject creature) {
            if(!legalBlocker.apply(creature)) throw new IllegalArgumentException();
            this.creature = creature;
        }
        
        public MagicObject getBlocker() {
            return creature;
        }
        
        public Map<? extends AttackerImpl, ? extends BlockAssignmentImpl> getAttackers() {
            return blocker.getOtherSideValues();
        }
        
        public void setDamageAssignmentOrder(List<? extends Attacker> blockers) {}
        
        public BlockAssignmentImpl addAttacker(AttackerImpl attacker) {
            BlockAssignmentImpl assignment = new BlockAssignmentImpl();
            blocker.add(attacker.attacker, assignment);
            return assignment;
        }
    }
    
    private abstract class DefenderImpl extends Combatant implements Defender {
        private OneSide<DefenderImpl, AttackerImpl> defender = oneSide(getGame(), this);
        
        @Override
        public Map<? extends AttackerImpl, ? extends AttackAssignmentImpl> getAttackers() {
            return new AbstractMap<AttackerImpl, AttackAssignmentImpl>() {
                private Set<Entry<AttackerImpl, AttackAssignmentImpl>> entrySet = new AbstractSet<Entry<AttackerImpl, AttackAssignmentImpl>>() {
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
                                                                                                        if(!(o instanceof Entry)) return false;
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
            if(!legalDefPl.apply(defender)) throw new IllegalArgumentException();
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
    }
    
    private class PlaneswalkerDefenderImpl extends DefenderImpl implements PlaneswalkerDefender {
        private MagicObject defender;
        
        public PlaneswalkerDefenderImpl(MagicObject defender) {
            if(!legalDefPw.apply(defender)) throw new IllegalArgumentException();
            this.defender = defender;
        }
        
        @Override
        public MagicObject getDefendingPlaneswalker() {
            return defender;
        }
        
        @Override
        public Player getPlayer() {
            return defender.getController();
        }
    }
    
    private class AttackAssignmentImpl implements AttackAssignment {}
    
    private class BlockAssignmentImpl implements BlockAssignment {}
    
    private Map<MagicObject, AttackerImpl> attackers, attackersView;
    private Map<MagicObject, BlockerImpl>  blockers, blockersView;
    private Map<Object, DefenderImpl>      defenders, defendersView;
    
    public CombatImpl(Game game) {
        super(game);
        attackers = editableMap(getGame(), new HashMap<MagicObject, AttackerImpl>());
        attackersView = unmodifiableMap(attackers);
        blockers = editableMap(getGame(), new HashMap<MagicObject, BlockerImpl>());
        blockersView = unmodifiableMap(blockers);
        defenders = editableMap(getGame(), new HashMap<Object, DefenderImpl>());
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
    
    public AttackAssignment assignAttacker(Attacker attacker, Defender defender) {
        if(!attackers.values().contains(attacker)) throw new IllegalStateException("Creature is not attacking");
        return ((AttackerImpl) attacker).setDefender((DefenderImpl) defender);
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
    
    public BlockAssignment assignBlocker(Blocker blocker, Attacker attacker) {
        if(!blockers.values().contains(blocker)) throw new IllegalStateException("Creature is not blocking");
        if(!attackers.values().contains(attacker)) throw new IllegalStateException("Creature is not attacking");
        return ((BlockerImpl) blocker).addAttacker((AttackerImpl) attacker);
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
    
    public PlaneswalkerDefender getDefender(MagicObject defender) {
        if(!legalDefPw.apply(defender) || !legalDefPl.apply(defender.getController())) throw new IllegalArgumentException();
        PlaneswalkerDefenderImpl d = (PlaneswalkerDefenderImpl) defenders.get(defender);
        if(d == null) defenders.put(defender, d = new PlaneswalkerDefenderImpl(defender));
        return d;
    }
    
    public PlayerDefender getDefender(Player defender) {
        if(!legalDefPl.apply(defender)) throw new IllegalArgumentException();
        PlayerDefenderImpl d = (PlayerDefenderImpl) defenders.get(defender);
        if(d == null) defenders.put(defender, d = new PlayerDefenderImpl(defender));
        return d;
    }
    
    public Collection<? extends Defender> getDefenders() {
        //ensure that all defenders are contained
        for(Player p:getGame().getPlayers())
            if(legalDefPl.apply(p)) getDefender(p);
        for(MagicObject o:getGame().getBattlefield().getCards())
            if(legalDefPw.apply(o) && legalDefPl.apply(o.getController())) getDefender(o);
        return defendersView.values();
    }
    
    public void removeFromCombat(Defender defender) {
        ((DefenderImpl) defender).setRemovedFromCombat(true);
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
}
