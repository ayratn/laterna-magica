/**
 * PhaseStructureImpl.java
 * 
 * Created on 16.10.2009
 */

package net.slightlymagic.laterna.magica.turnStructure.impl;


import static java.util.Arrays.*;
import static net.slightlymagic.laterna.magica.action.stateBased.StateBasedAction.Type.*;
import static net.slightlymagic.laterna.magica.action.turnBased.TurnBasedAction.Type.*;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.slightlymagic.beans.properties.Property;
import net.slightlymagic.laterna.magica.Combat;
import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.action.play.TriggerAction;
import net.slightlymagic.laterna.magica.action.stateBased.LethalDamageAction;
import net.slightlymagic.laterna.magica.action.stateBased.LoseOnDrawAction;
import net.slightlymagic.laterna.magica.action.stateBased.LoseOnLifeAction;
import net.slightlymagic.laterna.magica.action.stateBased.StateBasedAction;
import net.slightlymagic.laterna.magica.action.turnBased.DamageAssignmentAction;
import net.slightlymagic.laterna.magica.action.turnBased.DamageDealingAction;
import net.slightlymagic.laterna.magica.action.turnBased.DeclareAttackersAction;
import net.slightlymagic.laterna.magica.action.turnBased.DeclareBlockersAction;
import net.slightlymagic.laterna.magica.action.turnBased.DefenderAction;
import net.slightlymagic.laterna.magica.action.turnBased.DrawAction;
import net.slightlymagic.laterna.magica.action.turnBased.EmptyPoolsAction;
import net.slightlymagic.laterna.magica.action.turnBased.OrderAttackersAction;
import net.slightlymagic.laterna.magica.action.turnBased.OrderBlockersAction;
import net.slightlymagic.laterna.magica.action.turnBased.TurnBasedAction;
import net.slightlymagic.laterna.magica.action.turnBased.TurnBasedAction.Type;
import net.slightlymagic.laterna.magica.action.turnBased.UntapAction;
import net.slightlymagic.laterna.magica.action.turnBased.WearOffAction;
import net.slightlymagic.laterna.magica.edit.CompoundEdit;
import net.slightlymagic.laterna.magica.event.PhaseChangedListener;
import net.slightlymagic.laterna.magica.event.PriorChangedListener;
import net.slightlymagic.laterna.magica.event.StepChangedListener;
import net.slightlymagic.laterna.magica.impl.AbstractGameContent;
import net.slightlymagic.laterna.magica.impl.CombatImpl;
import net.slightlymagic.laterna.magica.player.Player;
import net.slightlymagic.laterna.magica.turnStructure.PhaseStructure;
import net.slightlymagic.laterna.magica.zone.SortedZone;


/**
 * The class PhaseStructureImpl.
 * 
 * @version V0.0 16.10.2009
 * @author Clemens Koza
 */
public class PhaseStructureImpl extends AbstractGameContent implements PhaseStructure {
    /**
     * The index of the player that has priority. The index is stored because it is easier to determine the next
     * player this way.
     */
    private Property<Integer>                            prior;
    /**
     * Stores the player that last took an action, which is naturally the first player to pass priority in
     * sequence. If that player would again receive priority (from another player), all players have passed in
     * succession.
     * 
     * The index is stored because it is easier to compare to prior.
     */
    private Property<Integer>                            firstPassed;
    
    private Property<Integer>                            phase, step;
    
    private Map<TurnBasedAction.Type, TurnBasedAction>   turnBasedActions;
    private Map<StateBasedAction.Type, StateBasedAction> stateBasedActions;
    
    private Property<Combat>                             combat;
    private Set<TriggerAction>                           triggeredAbilities;
    private Property<TurnBasedAction.Type>               turnBasedAction;
    
    public PhaseStructureImpl(Game game) {
        super(game);
        List<Player> pl = getGame().getPlayers();
        prior = properties.property(null, new IndexProperty<Player>(s, "prior", -1, pl));
        firstPassed = properties.property(null, new IndexProperty<Player>(s, "firstPassed", 0, pl));
        phase = properties.property(null, new IndexProperty<Phase>(s, "phase", -1, asList(Phase.values())));
        step = properties.property(null, new IndexProperty<Step>(s, "step", -1, null) {
            @Override
            public List<Step> getValues() {
                Phase p = getPhase();
                return p != null? p.getSteps():null;
            }
        });
        
        combat = properties.property("combat");
        triggeredAbilities = properties.set("triggeredAbilities");
        turnBasedAction = properties.property("turnBasedAction");
        
        //no need for editable, since it's only created once
        turnBasedActions = new EnumMap<TurnBasedAction.Type, TurnBasedAction>(TurnBasedAction.Type.class);
        turnBasedActions.put(PHASING, null);
        turnBasedActions.put(UNTAP, new UntapAction(getGame()));
        turnBasedActions.put(DRAW, new DrawAction(getGame()));
        turnBasedActions.put(SCHEME, null);
        
        turnBasedActions.put(DEFENDER, new DefenderAction(getGame()));
        turnBasedActions.put(DECLARE_ATTACKERS, new DeclareAttackersAction(getGame()));
        turnBasedActions.put(DECLARE_BLOCKERS, new DeclareBlockersAction(getGame()));
        turnBasedActions.put(ORDER_BLOCKERS, new OrderBlockersAction(getGame()));
        turnBasedActions.put(ORDER_ATTACKERS, new OrderAttackersAction(getGame()));
        turnBasedActions.put(DAMAGE_ASSIGNMENT, new DamageAssignmentAction(getGame()));
        turnBasedActions.put(DAMAGE_DEALING, new DamageDealingAction(getGame()));
        
        turnBasedActions.put(HAND_LIMIT, null);
        turnBasedActions.put(WEAR_OFF, new WearOffAction(getGame()));
        turnBasedActions.put(EMPTY_POOLS, new EmptyPoolsAction(getGame()));
        
        //no need for editable, since it's only created once
        stateBasedActions = new EnumMap<StateBasedAction.Type, StateBasedAction>(StateBasedAction.Type.class);
        stateBasedActions.put(LOSE_ON_LIFE, new LoseOnLifeAction(getGame()));
        stateBasedActions.put(LOSE_ON_DRAW, new LoseOnDrawAction(getGame()));
        stateBasedActions.put(LOSE_ON_POISON, null);
        stateBasedActions.put(PHASED_TOKEN_CEASE, null);
        stateBasedActions.put(COPY_CEASE, null);
        stateBasedActions.put(CREATURE_TOUGHNESS, null);
        stateBasedActions.put(LETHAL_DAMAGE, new LethalDamageAction(getGame()));
        stateBasedActions.put(DEATHTOUCH_DAMAGE, null);
        stateBasedActions.put(PLANESWALKER_LOYALTY, null);
        stateBasedActions.put(PLANESWALKER_UNIQUENESS, null);
        stateBasedActions.put(LEGENDARY_UNIQUENESS, null);
        stateBasedActions.put(WORLD_UNIQUENESS, null);
        stateBasedActions.put(AURA_ILLEGAL, null);
        stateBasedActions.put(EQUIPMENT_ILLEGAL, null);
        stateBasedActions.put(UNATTACH, null);
        stateBasedActions.put(P1P1_M1M1_REMOVE, null);
        stateBasedActions.put(COUNTER_LIMIT, null);
    }
    
    public Phase getPhase() {
        //TODO enable phase adding and skipping
        int phase = this.phase.getValue();
        return phase == -1? null:Phase.values()[phase];
    }
    
    
    public Step getStep() {
        //TODO enable step adding and skipping
        int step = this.step.getValue();
        if(step == -1) return null;
        Phase p = getPhase();
        if(p == null) return null;
        return p.getSteps().get(step);
    }
    
    
    public Player getPriorPlayer() {
        int prior = this.prior.getValue();
        return prior == -1? null:getGame().getPlayers().get(prior);
    }
    
    @Override
    public Type getTurnBasedAction() {
        return turnBasedAction.getValue();
    }
    
    public void takeAction(boolean took) {
        CompoundEdit ed = new CompoundEdit(getGame(), true,
                took? "Prior player took an action":"Prior player passed priority");
        
        Player oldPrior = getPriorPlayer();
        
        int prior = this.prior.getValue();
        
        this.prior.setValue(-1);
        
        if(took) {
            firstPassed.setValue(prior);
            //a player that took an action receives priority again
        } else {
            //the next player receives priority
            do {
                prior = (prior + 1) % getGame().getPlayers().size();
            } while(!getGame().getPlayers().get(prior).isInGame());
            //All players passed, priority is back at the player who most recently did something
            if(prior == firstPassed.getValue().intValue()) {
                passedInSuccession();
                //When everything else happened, the active player receives priority
                prior = getGame().getPlayers().indexOf(getGame().getTurnStructure().getActivePlayer());
                firstPassed.setValue(prior);
            }
        }
        beforeReceivePriority();
        
        this.prior.setValue(prior);
        

        Player newPrior = getPriorPlayer();
        fireNextPrior(oldPrior, newPrior);
        
        ed.end();
    }
    
    public Combat getCombat() {
        return combat.getValue();
    }
    
    public Set<TriggerAction> getTriggeredAbilities() {
        return triggeredAbilities;
    }
    
    /**
     * Does everything that happens before a player receives priority, namely checking state-based actions and
     * putting triggered abilities on the stack as needed, repeated as long as needed.
     */
    private void beforeReceivePriority() {
        boolean repeat;
        do {
            do {
                //reset repeat at the beginning of every loop
                repeat = false;
                
                for(StateBasedAction.Type t:StateBasedAction.Type.values()) {
                    StateBasedAction action = stateBasedActions.get(t);
                    if(action != null) repeat |= action.execute();
                    else log.warn("State based Action " + t + " does not exist");
                }
            } while(repeat);
            
            //TODO sort triggered abilities
            
            if(!triggeredAbilities.isEmpty()) repeat = true;
            
            for(TriggerAction t:triggeredAbilities)
                t.execute();
            
            triggeredAbilities.clear();
        } while(repeat);
    }
    
    /**
     * Does everything that happens when all players pass in succession, namely
     * <ul>
     * <li>Resolving the topmost object of the stack if it is nonempty</li>
     * <li>Ending the current step if the stack was empty</li>
     * <li>Ending the current phase if the step was the last in the phase</li>
     * <li>Ending the current turn if the phase was the last in the turn</li>
     * <li>If necessary, turn-based actions are performed for the old and new step</li>
     * </ul>
     */
    private void passedInSuccession() {
        SortedZone stack = getGame().getStack();
        if(!stack.isEmpty()) {
            MagicObject o = stack.getCards().get(stack.size() - 1);
            //this will remove the object from the stack
            o.getPlayInformation().getEffect().execute();
        } else nextStep();
    }
    
    /**
     * Advances to the next step. If the current step is the last step of the phase, {@link #nextPhase()} is
     * called.
     */
    private void nextStep() {
        do {
            CompoundEdit e = new CompoundEdit(getGame(), true, "Advance to next step");
            Step oldStep = step.getValue().intValue() == -1? null:getStep();
            
            if(step.getValue().intValue() == -1) {
                step.setValue(0);
            } else {
                //TBAs at the end of the last step
                for(TurnBasedAction.Type t:getStep().getEndActions()) {
                    turnBasedAction.setValue(t);
                    TurnBasedAction action = turnBasedActions.get(t);
                    if(action != null) action.execute();
                    else log.warn("Turn based Action " + t + " does not exist");
                }
                turnBasedAction.setValue(null);
                step.setValue((step.getValue() + 1) % getPhase().getSteps().size());
            }
            
            if(step.getValue().intValue() == 0) {
                nextPhase();
            }
            
            //Inform listeners that the step has begone before notifying TBAs
            Step newStep = getStep();
            fireNextStep(oldStep, newStep);
            
            //TBAs at the beginning of the next step
            for(TurnBasedAction.Type t:getStep().getBeginningActions()) {
                turnBasedAction.setValue(t);
                TurnBasedAction action = turnBasedActions.get(t);
                if(action != null) action.execute();
                else log.warn("Turn based Action " + t + " does not exist");
            }
            turnBasedAction.setValue(null);
            
            e.end();
        } while(!getStep().isGetPriority());
    }
    
    /**
     * Advances to the next phase. If the current phase is the last phase of the turn, {@link #nextTurn()} is
     * called.
     */
    private void nextPhase() {
        CompoundEdit e = new CompoundEdit(getGame(), true, "Advance to next phase");
        Phase oldPhase = phase.getValue().intValue() == -1? null:getPhase();
        
        //Do this if the method is called "strangely"
        step.setValue(0);
        
        phase.setValue((phase.getValue() + 1) % Phase.values().length);
        if(phase.getValue().intValue() == 0) {
            nextTurn();
        }
        
        Phase newPhase = getPhase();
        fireNextPhase(oldPhase, newPhase);
        
        e.end();
    }
    
    /**
     * Advances to the next turn.
     */
    private void nextTurn() {
        CompoundEdit e = new CompoundEdit(getGame(), true, "Advance to next turn");
        //Do this if the method is called "strangely"
        step.setValue(0);
        
        if(phase.getValue().intValue() != 0) phase.setValue(0);
        getGame().getTurnStructure().nextTurn();
        e.end();
    }
    
    private void fireNextPrior(Player oldPrior, Player newPrior) {
        for(Iterator<PriorChangedListener> it = getPriorChangedListeners(); it.hasNext();)
            it.next().nextPrior(oldPrior, newPrior);
    }
    
    private void fireNextStep(Step oldStep, Step newStep) {
        for(Iterator<StepChangedListener> it = getStepChangedListeners(); it.hasNext();)
            it.next().nextStep(oldStep, newStep);
    }
    
    private void fireNextPhase(Phase oldPhase, Phase newPhase) {
        if(newPhase == Phase.COMBAT) combat.setValue(new CombatImpl(getGame()));
        else combat.setValue(null);
        
        for(Iterator<PhaseChangedListener> it = getPhaseChangedListeners(); it.hasNext();)
            it.next().nextPhase(oldPhase, newPhase);
    }
    
    
    public void addPriorChangedListener(PriorChangedListener l) {
        listeners.add(PriorChangedListener.class, l);
    }
    
    public void removePriorChangedListener(PriorChangedListener l) {
        listeners.remove(PriorChangedListener.class, l);
    }
    
    @SuppressWarnings("unchecked")
    public Iterator<PriorChangedListener> getPriorChangedListeners() {
        return listeners.getIterator(PriorChangedListener.class);
    }
    
    public void addStepChangedListener(StepChangedListener l) {
        listeners.add(StepChangedListener.class, l);
    }
    
    public void removeStepChangedListener(StepChangedListener l) {
        listeners.remove(StepChangedListener.class, l);
    }
    
    @SuppressWarnings("unchecked")
    public Iterator<StepChangedListener> getStepChangedListeners() {
        return listeners.getIterator(StepChangedListener.class);
    }
    
    
    public void addPhaseChangedListener(PhaseChangedListener l) {
        listeners.add(PhaseChangedListener.class, l);
    }
    
    public void removePhaseChangedListener(PhaseChangedListener l) {
        listeners.remove(PhaseChangedListener.class, l);
    }
    
    @SuppressWarnings("unchecked")
    public Iterator<PhaseChangedListener> getPhaseChangedListeners() {
        return listeners.getIterator(PhaseChangedListener.class);
    }
}
