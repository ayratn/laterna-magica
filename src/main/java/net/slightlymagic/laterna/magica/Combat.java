/**
 * Combat.java
 * 
 * Created on 15.08.2010
 */

package net.slightlymagic.laterna.magica;


import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.slightlymagic.laterna.magica.action.GameAction;
import net.slightlymagic.laterna.magica.action.turnBased.TurnBasedAction;
import net.slightlymagic.laterna.magica.card.CardObject;
import net.slightlymagic.laterna.magica.player.Player;


/**
 * The class Combat.
 * 
 * @version V0.0 15.08.2010
 * @author Clemens Koza
 */
public interface Combat {
    public static interface Attacker {
        /**
         * Returns the attacking creature represented by this attack.
         */
        public CardObject getAttacker();
        
        /**
         * Returns the blockers blocking this attacker in an unmodifiable list.
         * 
         * After determining damage assignment orders, the ordering of the list is relevant.
         */
        public Map<? extends Blocker, ? extends BlockAssignment> getBlockers();
        
        /**
         * Sets this attacker's damage assignment order. The contents of the parameter list must be the same
         * objects as the stored blockers. Otherwise, an {@link IllegalArgumentException} is thrown.
         */
        public void setDamageAssignmentOrder(List<? extends Blocker> blockers);
        
        /**
         * Returns this attacker's damage assignment order.
         */
        public List<? extends Blocker> getDamageAssignmentOrder();
        
        /**
         * Returns if this attacker was removed from combat
         */
        public boolean isRemovedFromCombat();
        
        public Defender getDefender();
    }
    
    public static interface Blocker {
        /**
         * Returns the blocking creature represented by this attack.
         */
        public CardObject getBlocker();
        
        /**
         * Returns the atackers blocked by this attacker in an unmodifiable list.
         * 
         * After determining damage assignment orders, the ordering of the list is relevant.
         */
        public Map<? extends Attacker, ? extends BlockAssignment> getAttackers();
        
        /**
         * Sets this blocker's damage assignment order. The contents of the parameter list must be the same objects
         * as the stored attackers. Otherwise, an {@link IllegalArgumentException} is thrown.
         */
        public void setDamageAssignmentOrder(List<? extends Attacker> attackers);
        
        /**
         * Returns this blocker's damage assignment order.
         */
        public List<? extends Attacker> getDamageAssignmentOrder();
        
        /**
         * Returns if this blocker was removed from combat
         */
        public boolean isRemovedFromCombat();
    }
    
    public static interface Defender {
        /**
         * Returns either the defending player or the defending planeswarker's controller.
         */
        public Player getPlayer();
        
        /**
         * Returns the attackers attacking this defender
         */
        public Map<? extends Attacker, ? extends AttackAssignment> getAttackers();
        
        /**
         * Returns if this blocker was removed from combat
         */
        public boolean isRemovedFromCombat();
    }
    
    public static interface PlayerDefender {
        /**
         * Returns the defending player, or null if this defender represents a planeswalker.
         */
        public Player getDefendingPlayer();
    }
    
    public static interface PlaneswalkerDefender {
        /**
         * Returns the defending planeswalker, or null if this defender represents a player.
         */
        public CardObject getDefendingPlaneswalker();
    }
    
    public static interface AttackAssignment {
        /**
         * Sets the amount of damage assigned by the attacker
         */
        public void setAttackerAssignedDamage(int amount);
        
        /**
         * Returns the amount of damage assigned by the attacker
         * 
         * @throws IllegalStateException if the attacker has not yet assigned damage
         */
        public int getAttackerAssignedDamage() throws IllegalStateException;
    }
    
    public static interface BlockAssignment {
        /**
         * Sets the amount of damage assigned by the attacker
         */
        public void setAttackerAssignedDamage(int amount);
        
        /**
         * Returns the amount of damage assigned by the attacker
         */
        public int getAttackerAssignedDamage();
        
        /**
         * Sets the amount of damage assigned by the blocker
         */
        public void setBlockerAssignedDamage(int amount);
        
        /**
         * Returns the amount of damage assigned by the blocker
         */
        public int getBlockerAssignedDamage();
    }
    
    //Attackers
    
    /**
     * Declares a creature as an attacking creature
     */
    public Attacker declareAttacker(CardObject attackingCreature);
    
    /**
     * Assigns a creature to attack a given defender.
     */
    public AttackAssignment assignAttacker(Attacker attacker, Defender defender);
    
    /**
     * Returns the {@link Attacker} representation of the specified creature, or {@code null} if the creature is
     * not attacking.
     * 
     * @throws IllegalArgumentException If the parameter is not a creature permanent controlled by the active
     *             player
     */
    public Attacker getAttacker(CardObject attacker);
    
    /**
     * Returns all declared attackers.
     */
    public Collection<? extends Attacker> getAttackers();
    
    public void removeFromCombat(Attacker attacker);
    
    //Attackers
    
    //Blockers
    
    /**
     * Declares a creature as a blocking creature
     */
    public Blocker declareBlocker(CardObject blockingCreature);
    
    /**
     * Assigns a creature to block a given attacker.
     * 
     * This method may be called multiple time to assign a blocker to block multiple attackers.
     */
    public BlockAssignment assignBlocker(Blocker blocker, Attacker attacker);
    
    /**
     * Returns the {@link Blocker} representation of the specified creature, or {@code null} if the creature is not
     * blocking.
     * 
     * @throws IllegalArgumentException If the parameter is not a creature permanent controlled by a defending
     *             player
     */
    public Blocker getBlocker(CardObject blocker);
    
    /**
     * Returns all declared blockers.
     */
    public Collection<? extends Blocker> getBlockers();
    
    public void removeFromCombat(Blocker blocker);
    
    //Blockers
    
    //Defenders
    
    /**
     * Returns the {@link Defender} representation of the specified planeswalker, even if the planeswalker is not
     * attacked.
     * 
     * @throws IllegalArgumentException If the parameter is not a planeswalker permanent controlled by a defending
     *             player
     */
    public PlaneswalkerDefender getDefender(CardObject defender);
    
    /**
     * Returns the {@link Defender} representation of the specified player, even if the player is not attacked.
     * 
     * @throws IllegalArgumentException If the parameter is not a defending player
     */
    public PlayerDefender getDefender(Player defender);
    
    /**
     * Returns all defending players and planeswalkers.
     */
    public Collection<? extends Defender> getDefenders();
    
    public void removeFromCombat(Defender defender);
    
    //Defenders
    
    /**
     * Returns the list of attacking players. This is usually the active player/all active players
     */
    public List<Player> getAttackingPlayers();
    
    /**
     * Returns the list of defending players
     */
    public List<Player> getDefendingPlayers();
    
    //Lifecycle
    /* These methods implement the flow of the combat phase. More precisely, they will be called by turn based
     * actions in the right order, where this class is primary for enforcing the rules and the Actor for making
     * decisions. Both are mediated by the turn based actions.
     */

    /**
     * Sets the currently executed turn based action. This value narrows down what modifications may be performed
     * on the combat object.
     */
    public void setAction(TurnBasedAction.Type action);
    
    //Beginning step
    
    /**
     * Sets the defending players. This method will throw an {@link IllegalArgumentException} if the defenders are
     * not valid, for example if the attacker or teammembers are contained, or the game variant restricts the set
     * of defenders otherwise.
     */
    public void setDefendingPlayers(Set<Player> defenders);
    
    //Declare Attackers Step
    
    /**
     * Returns whether the assignment of attackers is legal. This method checks {@magic.ruleRef
     * 20100716/5081c} and {@magic.ruleRef 20100716/2081d}. This method must behave correctly if
     * there are multiple attacking players. Currently, the rules don't specify such a case (when a team attacks,
     * it is handled as a single player, not the individual players).
     */
    public boolean isLegalAttackers(Player attacker);
    
    /**
     * Taps all attacking creatures. This method must respect Vigilance and other applicable effects. It implements
     * {@magic.ruleRef 20100716/5081f}. This method must behave correctly if there are multiple
     * attacking players. Currently, the rules don't specify such a case (when a team attacks, it is handled as a
     * single player, not the individual players).
     */
    public void tapAttackers(Player attacker);
    
    /**
     * Returns the overall cost required for attacking. This method implements {@magic.ruleRef
     * 20100716/5081g}. This method must behave correctly if there are multiple attacking players. Currently, the
     * rules don't specify such a case (when a team attacks, it is handled as a single player, not the individual
     * players).
     */
    public GameAction getAttackersCost(Player attacker);
    
    //Declare Blockers Step
    
    /**
     * Returns whether the assignment of blockers is legal. This method checks {@magic.ruleRef
     * 20100716/5091b} and {@magic.ruleRef 20100716/2091c}. This method works correctly in respect
     * to {@magic.ruleRef 20100716/8024}.
     */
    public boolean isLegalBlockers(Player defender);
    
    /**
     * Returns the overall cost required for blocking. This method implements {@magic.ruleRef
     * 20100716/5091d}. This method works correctly in respect to {@magic.ruleRef 20100716/8024}.
     */
    public GameAction getBlockersCost(Player defender);
    
    /**
     * Checks if the damage assignment order is legal. In particular, this checks if the assignment order was
     * completely specified.
     */
    public boolean isLegalAttackersAssignmentOrder(Player attacker);
    
    /**
     * Checks if the damage assignment order is legal. In particular, this checks if the assignment order was
     * completely specified.
     */
    public boolean isLegalBlockersAssignmentOrder(Player defender);
    
    //Combat Damage Step
    
    /**
     * Initializes the Combat for the current combat damage step.
     */
    public void startCombatDamageStep();
    
    /**
     * Returns whether there are creatures with first- or double strike in combat and whether this is a first
     * strike damage step.
     */
    public boolean isFirstStrikeDamageStep();
    
    /**
     * Checks if the damage assignment is legal for the particular attacker. This check respects {@magic.ruleRef
     *  20100716/5101}, taking previously assigned attackers into account, and also {@magic.ruleRef
     *  20100716/5105}.
     */
    public boolean isLegalAttackerAssignment(Attacker attacker);
    
    /**
     * Checks if the damage assignment is legal for the particular blocker. This check respects {@magic.ruleRef
     *  20100716/5101}, taking previously assigned attackers into account, and also {@magic.ruleRef
     *  20100716/5105}.
     */
    public boolean isLegalBlockerAssignment(Blocker blocker);
    
    /**
     * Deals all damage to combatants. See {@magic.ruleRef 20100716/5102}.
     */
    public void dealDamage();
    
    /**
     * Returns if another combat damage step should happen after this one. If so, this method will clear all
     * assigned damage to be ready for the next step.
     */
    public boolean nextDamageStep();
}
