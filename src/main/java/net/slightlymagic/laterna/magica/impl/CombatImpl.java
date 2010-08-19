/**ctivePlayer.g
 * CombatImpl.java
 * 
 * Created on 16.08.2010
 */

package net.slightlymagic.laterna.magica.impl;


import static com.google.common.base.Predicates.*;
import static com.google.common.base.Suppliers.*;
import static java.lang.String.*;
import static java.util.Collections.*;
import static net.slightlymagic.laterna.magica.card.State.StateType.*;
import static net.slightlymagic.laterna.magica.characteristics.CardType.*;
import static net.slightlymagic.laterna.magica.util.MagicaCollections.*;
import static net.slightlymagic.laterna.magica.util.MagicaFunctions.*;
import static net.slightlymagic.laterna.magica.util.MagicaPredicates.*;
import static net.slightlymagic.laterna.magica.util.relational.Relations.*;
import static net.slightlymagic.laterna.magica.zone.Zone.Zones.*;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.slightlymagic.laterna.magica.Combat;
import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.action.GameAction;
import net.slightlymagic.laterna.magica.action.turnBased.TurnBasedAction;
import net.slightlymagic.laterna.magica.card.CardObject;
import net.slightlymagic.laterna.magica.cost.impl.DummyCostFunction;
import net.slightlymagic.laterna.magica.edit.property.EditableProperty;
import net.slightlymagic.laterna.magica.player.Player;
import net.slightlymagic.laterna.magica.util.MagicaCollections;
import net.slightlymagic.laterna.magica.util.relational.ManySide;
import net.slightlymagic.laterna.magica.util.relational.ManyToMany;
import net.slightlymagic.laterna.magica.util.relational.OneSide;

import com.google.common.base.Predicate;
import com.google.common.collect.AbstractIterator;


/**
 * The class CombatImpl.
 * 
 * @version V0.0 16.08.2010
 * @author Clemens Koza
 */
public class CombatImpl extends AbstractGameContent implements Combat {
    private static final Predicate<Player>      active           = new Predicate<Player>() {
                                                                     @Override
                                                                     public boolean apply(Player input) {
                                                                         return input == input.getGame().getTurnStructure().getActivePlayer();
                                                                     }
                                                                 };
    
    private static final Predicate<MagicObject> permanent        = isIn(ofInstance(BATTLEFIELD));
    private static final Predicate<MagicObject> untapped         = not(is(TAPPED));
    private static final Predicate<MagicObject> creature         = and(permanent, card(has(CREATURE)));
    private static final Predicate<MagicObject> planeswalker     = and(permanent, card(has(PLANESWALKER)));
    private static final Predicate<CardObject>  untappedCreature = and(creature, untapped);
    
    private final Predicate<CardObject>         legalAttacker    = and(untappedCreature,
                                                                         compose(active, controller));
    //TODO consider teams
    private final Predicate<Player>             legalDefPl       = not(active);
    private final Predicate<CardObject>         legalDefPw       = and(planeswalker,
                                                                         compose(legalDefPl, controller));
    private final Predicate<CardObject>         legalBlocker     = and(untappedCreature,
                                                                         compose(legalDefPl, controller));
    

    private TurnBasedAction.Type                action;
    
    private <T> EditableProperty<T> editable(String name, T value) {
        return new EditableProperty<T>(getGame(), null, name, value);
    }
    
    private <T> Set<T> editableSet() {
        return MagicaCollections.editableSet(getGame(), new HashSet<T>());
    }
    
    private static <T> boolean equal(Collection<? extends T> c1, Collection<? extends T> c2) {
        return c1.containsAll(c2) && c2.containsAll(c1);
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
    
    private abstract class CreatureCombatant<A, B> extends Combatant {
        @SuppressWarnings({"unchecked", "rawtypes"})
        private ManyToMany<A, B, BlockAssignmentImpl> combatant = (ManyToMany) manyToMany(getGame(), this);
        private CardObject                            creature;
        private EditableProperty<List<B>>             order     = editable("order", null);
        
        protected CreatureCombatant(CardObject creature) {
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
            order.setValue(unmodifiableList(new ArrayList<B>(otherCreatures)));
        }
        
        public List<? extends B> getDamageAssignmentOrder() {
            return order.getValue();
        }
    }
    
    private class AttackerImpl extends CreatureCombatant<AttackerImpl, BlockerImpl> implements Attacker {
        private ManySide<AttackerImpl, DefenderImpl>   defender   = manySide(getGame(), this);
        private EditableProperty<AttackAssignmentImpl> assignment = editable("attackAssignment", null);
        
        public AttackerImpl(CardObject creature) {
            super(creature);
        }
        
        @Override
        protected void setCreature(CardObject creature) {
            checkAction(TurnBasedAction.Type.DECLARE_ATTACKERS);
            if(!legalAttacker.apply(creature)) throw new IllegalArgumentException();
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
            checkAction(TurnBasedAction.Type.ORDER_BLOCKERS);
            setOrder((List<? extends BlockerImpl>) blockers);
        }
        
        public AttackAssignmentImpl setDefender(DefenderImpl defender) {
            checkAction(TurnBasedAction.Type.DECLARE_ATTACKERS);
            this.defender.setOneSide(defender.defender);
            AttackAssignmentImpl assignment = new AttackAssignmentImpl();
            this.assignment.setValue(assignment);
            return assignment;
        }
    }
    
    private class BlockerImpl extends CreatureCombatant<BlockerImpl, AttackerImpl> implements Blocker {
        public BlockerImpl(CardObject creature) {
            super(creature);
        }
        
        @Override
        protected void setCreature(CardObject creature) {
            checkAction(TurnBasedAction.Type.DECLARE_BLOCKERS);
            if(!legalBlocker.apply(creature)) throw new IllegalArgumentException();
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
            checkAction(TurnBasedAction.Type.ORDER_ATTACKERS);
            setOrder((List<? extends AttackerImpl>) attackers);
        }
        
        public BlockAssignmentImpl addAttacker(AttackerImpl attacker) {
            checkAction(TurnBasedAction.Type.DECLARE_BLOCKERS);
            if(attacker.getDefender().getPlayer() != getBlocker().getController()) throw new IllegalArgumentException();
            BlockAssignmentImpl assignment = new BlockAssignmentImpl();
            getCombatant().add(attacker.getCombatant(), assignment);
            return assignment;
        }
    }
    
    private abstract class DefenderImpl extends Combatant implements Defender {
        private OneSide<DefenderImpl, AttackerImpl> defender = oneSide(getGame(), this);
        
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
        private CardObject defender;
        
        public PlaneswalkerDefenderImpl(CardObject defender) {
            if(!legalDefPw.apply(defender)) throw new IllegalArgumentException();
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
    }
    
    private class AttackAssignmentImpl implements AttackAssignment {
        private EditableProperty<Integer> attacker = editable("attacker", null);
        
        private void resetAttackerAssignedDamage() {
            attacker.setValue(null);
        }
        
        @Override
        public void setAttackerAssignedDamage(int amount) {
            attacker.setValue(amount);
        }
        
        @Override
        public int getAttackerAssignedDamage() throws IllegalStateException {
            if(attacker.getValue() == null) throw new IllegalStateException();
            return attacker.getValue();
        }
    }
    
    private class BlockAssignmentImpl implements BlockAssignment {
        private EditableProperty<Integer> attacker = editable("attacker", null);
        private EditableProperty<Integer> blocker  = editable("blocker", null);
        
        private void resetAttackerAssignedDamage() {
            attacker.setValue(null);
        }
        
        @Override
        public void setAttackerAssignedDamage(int amount) {
            attacker.setValue(amount);
        }
        
        @Override
        public int getAttackerAssignedDamage() throws IllegalStateException {
            if(attacker.getValue() == null) throw new IllegalStateException();
            return attacker.getValue();
        }
        
        private void resetBlockerAssignedDamage() {
            blocker.setValue(null);
        }
        
        @Override
        public void setBlockerAssignedDamage(int amount) {
            blocker.setValue(amount);
        }
        
        @Override
        public int getBlockerAssignedDamage() throws IllegalStateException {
            if(blocker.getValue() == null) throw new IllegalStateException();
            return blocker.getValue();
        }
    }
    
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
        return ((AttackerImpl) attacker).setDefender((DefenderImpl) defender);
    }
    
    public AttackerImpl getAttacker(CardObject attacker) {
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
    
    public PlaneswalkerDefender getDefender(CardObject defender) {
        if(!legalDefPw.apply(defender)) throw new IllegalArgumentException();
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
            if(legalDefPw.apply((CardObject) o)) getDefender((CardObject) o);
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
            if(!legalDefPl.apply(it.next())) it.remove();
        return p;
    }
    
    //Lifecycle
    /* These methods implement the flow of the combat phase. More precisely, they will be called by turn based
     * actions in the right order, where this class is primary for enforcing the rules and the Actor for making
     * decisions. Both are mediated by the turn based actions.
     */

    public void setAction(TurnBasedAction.Type action) {
        switch(action) {
            case DEFENDER:
            case DECLARE_ATTACKERS:
            case DECLARE_BLOCKERS:
            case ORDER_BLOCKERS:
            case ORDER_ATTACKERS:
            case DAMAGE_ASSIGNMENT:
            case DAMAGE_DEALING:
                this.action = action;
            break;
            default:
                throw new IllegalArgumentException(valueOf(action));
        }
    }
    
    private void checkAction(TurnBasedAction.Type action) {
        if(this.action != action) throw new IllegalStateException("Expected: " + action + ", actual: "
                + this.action);
    }
    
    //Declare Attackers Step
    
    public boolean isLegalAttackers(Player attacker) {
        checkAction(TurnBasedAction.Type.DECLARE_ATTACKERS);
        //check whether every attacker has a defender assigned
        for(Attacker a:getAttackers())
            if(a.getAttacker().getController() == attacker && a.getDefender() == null) return false;
        //TODO implement restrictions & requirements
        return true;
    }
    
    public void tapAttackers(Player attacker) {
        checkAction(TurnBasedAction.Type.DECLARE_ATTACKERS);
        for(Attacker a:getAttackers())
            if(a.getAttacker().getController() == attacker) a.getAttacker().getState().setState(TAPPED, true);
    }
    
    public GameAction getAttackersCost(Player attacker) {
        checkAction(TurnBasedAction.Type.DECLARE_ATTACKERS);
        //TODO implement
        return DummyCostFunction.EMPTY.apply(getGame());
    }
    
    //Declare Blockers Step
    
    public boolean isLegalBlockers(Player defender) {
        checkAction(TurnBasedAction.Type.DECLARE_BLOCKERS);
        //check whether every blocker has at least one attacker assigned
        for(Blocker a:getBlockers())
            if(a.getBlocker().getController() == defender && a.getAttackers().isEmpty()) return false;
        //TODO implement restrictions & requirements
        return true;
    }
    
    public GameAction getBlockersCost(Player defender) {
        checkAction(TurnBasedAction.Type.DECLARE_BLOCKERS);
        //TODO implement
        return DummyCostFunction.EMPTY.apply(getGame());
    }
    
    public boolean isLegalAttackersAssignmentOrder(Player attacker) {
        checkAction(TurnBasedAction.Type.ORDER_ATTACKERS);
        for(AttackerImpl a:attackers.values()) {
            //ignore attackers of other players
            if(a.getAttacker().getController() != attacker) continue;
            
            //checks whether the attacker has an order assigned
            //setting the order checks if all blockers were assigned
            if(a.getDamageAssignmentOrder() == null) return false;
        }
        return true;
    }
    
    public boolean isLegalBlockersAssignmentOrder(Player defender) {
        checkAction(TurnBasedAction.Type.ORDER_BLOCKERS);
        for(BlockerImpl b:blockers.values()) {
            //ignore blockers of other players
            if(b.getBlocker().getController() != defender) continue;
            
            //checks whether the blocker has an order assigned
            //setting the order checks if all attackers were assigned
            if(b.getDamageAssignmentOrder() == null) return false;
        }
        return true;
    }
    
    //Combat Damage Step
    
    private static enum CombatDamageStep {
        BEFORE, FIRST, BETWEEN, REGULAR, AFTER
    }
    
    private EditableProperty<CombatDamageStep> firstStrike       = editable("firstStrike", CombatDamageStep.BEFORE);
    
    private Set<AttackerImpl>                  attackersThisStep = editableSet();
    private Set<BlockerImpl>                   blockersThisStep  = editableSet();
    
    /**
     * Returns whether the creature deals, depending on {@code first}, first strike or regular damage.
     */
    private boolean dealsDamage(CreatureCombatant<?, ?> c, boolean first) {
        //TODO determine first & double strike creatures
        boolean firstStrike = false, doubleStrike = false;
        
        return first == (firstStrike || doubleStrike);
    }
    
    /**
     * Returns the amount of damage dealt by the creature
     */
    private int getAmmount(CreatureCombatant<?, ?> c) {
        //TODO implement properly
        return c.getCreature().getCharacteristics().get(0).getPower();
    }
    
    /**
     * Returns whether the creature was assigned lethal damage
     */
    private boolean isLethal(CreatureCombatant<?, ?> c) {
        //TODO implement properly
        int toughness = c.getCreature().getCharacteristics().get(0).getToughness();
        for(BlockAssignmentImpl b:c.getOtherCreatures().values())
            toughness -= c instanceof AttackerImpl? b.getBlockerAssignedDamage():b.getAttackerAssignedDamage();
        return toughness <= 0;
    }
    
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
                    break;
                }
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
    
    public boolean isLegalAttackerAssignment(Attacker attacker) {
        checkAction(TurnBasedAction.Type.DAMAGE_ASSIGNMENT);
        //TODO implement
        return true;
    }
    
    public boolean isLegalBlockerAssignment(Blocker blocker) {
        checkAction(TurnBasedAction.Type.DAMAGE_ASSIGNMENT);
        //TODO implement
        return true;
    }
    
    //Damage dealing
    
    @Override
    public void dealDamage() {
        //TODO implement
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
