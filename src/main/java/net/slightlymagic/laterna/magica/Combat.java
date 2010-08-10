/**
 * Combat.java
 * 
 * Created on 13.07.2010
 */

package net.slightlymagic.laterna.magica;


import java.util.Collection;
import java.util.List;
import java.util.Set;

import net.slightlymagic.laterna.magica.player.Player;


/**
 * The class Combat.
 * 
 * @version V0.0 13.07.2010
 * @author Clemens Koza
 */
public interface Combat extends GameContent {
    //Attackers
    
    /**
     * Declares a creature as an attacking creature
     */
    public Attacker declareAttacker(MagicObject attackingCreature);
    
    /**
     * Assigns a creature to attack a given defender.
     */
    public void assignAttacker(Attacker attacker, Defender defender);
    
    /**
     * Returns the {@link Attacker} representation of the specified creature, or {@code null} if the creature is
     * not attacking.
     * 
     * @throws IllegalArgumentException If the parameter is not a creature permanent controlled by the active
     *             player
     */
    public Attacker getAttacker(MagicObject attacker);
    
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
    public Blocker declareBlocker(MagicObject blockingCreature);
    
    /**
     * Assigns a creature to block a given attacker.
     * 
     * This method may be called multiple time to assign a blocker to block multiple attackers.
     */
    public void assignBlocker(Blocker blocker, Attacker attacker);
    
    /**
     * Returns the {@link Blocker} representation of the specified creature, or {@code null} if the creature is not
     * blocking.
     * 
     * @throws IllegalArgumentException If the parameter is not a creature permanent controlled by a defending
     *             player
     */
    public Blocker getBlocker(MagicObject blocker);
    
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
    public Defender getDefender(MagicObject defender);
    
    /**
     * Returns the {@link Defender} representation of the specified player, even if the player is not attacked.
     * 
     * @throws IllegalArgumentException If the parameter is not a defending player
     */
    public Defender getDefender(Player defender);
    
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
    
    public static interface Attacker {
        /**
         * Returns the attacking creature represented by this attack.
         */
        public MagicObject getAttacker();
        
        /**
         * Returns the blockers blocking this attacker in an unmodifiable list.
         * 
         * After determining damage assignment orders, the ordering of the list is relevant.
         */
        public List<? extends Blocker> getBlockers();
        
        /**
         * Sets this attacker's damage assignment order. The contents of the parameter list must be the same
         * objects as the stored blockers. Otherwise, an {@link IllegalArgumentException} is thrown.
         */
        public void setDamageAssignmentOrder(List<? extends Blocker> blockers);
        
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
        public MagicObject getBlocker();
        
        /**
         * Returns the atackers blocked by this attacker in an unmodifiable list.
         * 
         * After determining damage assignment orders, the ordering of the list is relevant.
         */
        public List<? extends Attacker> getAttackers();
        
        /**
         * Sets this blocker's damage assignment order. The contents of the parameter list must be the same objects
         * as the stored attackers. Otherwise, an {@link IllegalArgumentException} is thrown.
         */
        public void setDamageAssignmentOrder(List<? extends Attacker> attackers);
        
        /**
         * Returns if this blocker was removed from combat
         */
        public boolean isRemovedFromCombat();
    }
    
    public static interface Defender {
        /**
         * Returns the defending planeswalker, or null if this defender represents a player.
         */
        public MagicObject getDefendingPlaneswalker();
        
        /**
         * Returns the defending player, or null if this defender represents a planeswalker.
         */
        public Player getDefendingPlayer();
        
        /**
         * Returns either the defending player or the defending planeswarker's controller.
         */
        public Player getPlayer();
        
        /**
         * Returns the atackers attacking this defender
         */
        public Set<? extends Attacker> getAttackers();
        
        /**
         * Returns if this blocker was removed from combat
         */
        public boolean isRemovedFromCombat();
    }
}
