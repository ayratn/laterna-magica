/**
 * PhaseStructure.java
 * 
 * Created on 16.10.2009
 */

package net.slightlymagic.laterna.magica.turnStructure;


import static java.util.Arrays.*;
import static java.util.Collections.*;
import static net.slightlymagic.laterna.magica.action.turnBased.TurnBasedAction.Type.*;
import static net.slightlymagic.laterna.magica.turnStructure.PhaseStructure.Step.*;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.slightlymagic.laterna.magica.GameContent;
import net.slightlymagic.laterna.magica.action.turnBased.TurnBasedAction;
import net.slightlymagic.laterna.magica.action.turnBased.TurnBasedAction.Type;
import net.slightlymagic.laterna.magica.event.PhaseChangedListener;
import net.slightlymagic.laterna.magica.event.PriorChangedListener;
import net.slightlymagic.laterna.magica.event.StepChangedListener;
import net.slightlymagic.laterna.magica.player.Player;


/**
 * The class PhaseStructure. The phase structure manages the flow of phases, steps and priority through a single
 * turn. Because this class implements the concept of priority, it has a close connection to the stack.
 * 
 * <ul>
 * <li>Whenever a phase or step begins, at first turn based actions are handled, then the active player receives
 * priority</li>
 * <li>Whenever a spell or (non-mana) ability resolves, the active player receives priority</li>
 * <li>Whenever a player would receive priority, the following happens:
 * <ul>
 * <li>State-based actions are checked first. If there were state-based actions, repeat from the beginning.</li>
 * <li>Triggered abilities that triggered since the last time a player received priority go on the stack. If there
 * were triggered abilities, repeat from the beginning.</li>
 * <li>The player that would have received priority actually receives priority.</li>
 * </ul>
 * </li>
 * <li>Whenever a player has priority, he may do one of the following:
 * <ul>
 * <li>That player may cast spells and activate abilities (including mana abilities). After that, he receives
 * priority again.</li>
 * <li>That player may take a special action, such as playing a land. Such actions don't use the stack. After that,
 * he receives priority again.</li>
 * <li>The player may pass priority, and the next player receives priority. (assuming not all players have passed
 * in succession)</li>
 * </ul>
 * </li>
 * <li>When all players pass priority in succession, one of the following happens:
 * <ul>
 * <li>If the stack is nonempty, the topmost element of the stack resolves</li>
 * <li>If the stack is empty, the current phase or step ends, causing turn based actions to happen. See
 * {@magic.ruleRef 20100716/R5002}</li>
 * </ul>
 * </li>
 * </ul>
 * 
 * @version V0.0 16.10.2009
 * @author Clemens Koza
 */
public interface PhaseStructure extends GameContent {
    /**
     * The phases of a turn. See {@magic.ruleRef 20100716/R5001}.
     */
    public static enum Phase {
        /**
         * {@magic.ruleRef 20100716/R501}
         */
        BEGINNING(BEGINNING_UNTAP, BEGINNING_UPKEEP, BEGINNING_DRAW),
        /**
         * {@magic.ruleRef 20100716/R505}
         */
        MAIN1(MAIN),
        /**
         * {@magic.ruleRef 20100716/R506}
         */
        COMBAT(COMBAT_BEGINNING, COMBAT_ATTACKERS, COMBAT_BLOCKERS, COMBAT_DAMAGE, COMBAT_END),
        /**
         * {@magic.ruleRef 20100716/R505}
         */
        MAIN2(MAIN),
        /**
         * {@magic.ruleRef 20100716/R512}
         */
        ENDING(ENDING_END, ENDING_CLEANUP);
        
        private final String     name;
        private final List<Step> steps;
        
        private Phase(Step... steps) {
            this.steps = unmodifiableList(asList(steps));
            
            String s = super.toString().toLowerCase();
            Matcher m = Pattern.compile("(^|_)(.)").matcher(s);
            StringBuffer result = new StringBuffer();
            while(m.find()) {
                boolean b = m.group(1).length() != 0;
                m.appendReplacement(result, Matcher.quoteReplacement((b? " ":"") + m.group(2).toUpperCase()));
            }
            m.appendTail(result);
            name = result.toString();
        }
        
        /**
         * The steps that make up the phase.
         */
        public List<Step> getSteps() {
            return steps;
        }
        
        @Override
        public String toString() {
            return name;
        }
    }
    
    /**
     * The steps. The association to phases is given through {@link Phase#getSteps()}. Note that both main phases
     * consist of the same main step.
     */
    public static enum Step {
        /**
         * {@magic.ruleRef 20100716/R502}
         */
        BEGINNING_UNTAP(false, PHASING, UNTAP),
        /**
         * {@magic.ruleRef 20100716/R503}
         */
        BEGINNING_UPKEEP,
        /**
         * {@magic.ruleRef 20100716/R504}
         */
        BEGINNING_DRAW(DRAW),
        /**
         * {@magic.ruleRef 20100716/R505}
         */
        MAIN,
        /**
         * {@magic.ruleRef 20100716/R507}
         */
        COMBAT_BEGINNING(DEFENDER),
        /**
         * {@magic.ruleRef 20100716/R508}
         */
        COMBAT_ATTACKERS(ATTACKERS),
        /**
         * {@magic.ruleRef 20100716/R509}
         */
        COMBAT_BLOCKERS(BLOCKERS, BLOCKER_ORDER, ATTACKER_ORDER),
        /**
         * {@magic.ruleRef 20100716/R510}
         */
        COMBAT_DAMAGE(DAMAGE_ASSIGNMENT, DAMAGE_DEALING),
        /**
         * {@magic.ruleRef 20100716/R511}
         */
        COMBAT_END,
        /**
         * {@magic.ruleRef 20100716/R513}
         */
        ENDING_END,
        /**
         * {@magic.ruleRef 20100716/R514}
         */
        //TODO not getting priority during cleanup is conditional
        ENDING_CLEANUP(false, HAND_LIMIT, WEAR_OFF);
        
        private final String     name;
        private final List<Type> beginning;
        private final boolean    getPriority;
        private final List<Type> end;
        
        private Step(Type... actions) {
            this(true, actions);
        }
        
        private Step(boolean getPriority, Type... actions) {
            this.beginning = unmodifiableList(asList(actions));
            this.getPriority = getPriority;
            this.end = unmodifiableList(asList(EMPTY_POOLS));
            
            String s = super.toString().toLowerCase();
            Matcher m = Pattern.compile("(^|_)(.)").matcher(s);
            StringBuffer result = new StringBuffer();
            while(m.find()) {
                boolean b = m.group(1).length() != 0;
                m.appendReplacement(result, Matcher.quoteReplacement((b? " ":"") + m.group(2).toUpperCase()));
            }
            m.appendTail(result);
            name = result.toString();
        }
        
        public List<TurnBasedAction.Type> getBeginningActions() {
            return beginning;
        }
        
        /**
         * Whether players get priority during the step by default.
         */
        public boolean isGetPriority() {
            return getPriority;
        }
        
        public List<TurnBasedAction.Type> getEndActions() {
            return end;
        }
        
        @Override
        public String toString() {
            return name;
        }
    }
    
    /**
     * Returns the current phase.
     */
    public Phase getPhase();
    
    /**
     * Returns the current step. Note: For the {@link Step#MAIN main}-Step, the phase is ambiguous. If the phase is
     * also important in this case, check also {@link #getPhase()}
     */
    public Step getStep();
    
    /**
     * Returns the player who has priority.
     */
    public Player getPriorPlayer();
    
    /**
     * Signals that the player who has priority has or has not taken an action. A parameter of true signals that
     * the player has taken an action, false means that he passed priority.
     * 
     * This method implements the real progressing in a turn. Depending on the situation, it will end steps, phases
     * or turns, and cause state- and turn-based actions to be checked.
     */
    public void takeAction(boolean took);
    
    public Iterator<PhaseChangedListener> getPhaseChangedListeners();
    
    public void removePhaseChangedListener(PhaseChangedListener l);
    
    public void addPhaseChangedListener(PhaseChangedListener l);
    
    public Iterator<StepChangedListener> getStepChangedListeners();
    
    public void removeStepChangedListener(StepChangedListener l);
    
    public void addStepChangedListener(StepChangedListener l);
    
    public Iterator<PriorChangedListener> getPriorChangedListeners();
    
    public void removePriorChangedListener(PriorChangedListener l);
    
    public void addPriorChangedListener(PriorChangedListener l);
}
