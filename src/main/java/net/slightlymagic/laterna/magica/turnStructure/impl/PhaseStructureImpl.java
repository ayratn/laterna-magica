/**
 * PhaseStructureImpl.java
 * 
 * Created on 16.10.2009
 */

package net.slightlymagic.laterna.magica.turnStructure.impl;


import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.slightlymagic.laterna.magica.Combat;
import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.action.stateBased.LethalDamageAction;
import net.slightlymagic.laterna.magica.action.stateBased.LoseOnDrawAction;
import net.slightlymagic.laterna.magica.action.stateBased.StateBasedAction;
import net.slightlymagic.laterna.magica.action.turnBased.DrawAction;
import net.slightlymagic.laterna.magica.action.turnBased.EmptyPoolsAction;
import net.slightlymagic.laterna.magica.action.turnBased.TurnBasedAction;
import net.slightlymagic.laterna.magica.action.turnBased.UntapAction;
import net.slightlymagic.laterna.magica.action.turnBased.TurnBasedAction.Type;
import net.slightlymagic.laterna.magica.edit.CompoundEdit;
import net.slightlymagic.laterna.magica.edit.impl.EditableListenerList;
import net.slightlymagic.laterna.magica.edit.property.EditableProperty;
import net.slightlymagic.laterna.magica.edit.property.EditablePropertyChangeSupport;
import net.slightlymagic.laterna.magica.event.PhaseChangedListener;
import net.slightlymagic.laterna.magica.event.PriorChangedListener;
import net.slightlymagic.laterna.magica.event.StepChangedListener;
import net.slightlymagic.laterna.magica.impl.AbstractGameContent;
import net.slightlymagic.laterna.magica.player.Player;
import net.slightlymagic.laterna.magica.turnStructure.PhaseStructure;
import net.slightlymagic.laterna.magica.zone.SortedZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The class PhaseStructureImpl.
 * 
 * @version V0.0 16.10.2009
 * @author Clemens Koza
 */
public class PhaseStructureImpl extends AbstractGameContent implements PhaseStructure {
    private static final Logger             log = LoggerFactory.getLogger(PhaseStructureImpl.class);
    
    protected EditableListenerList          listeners;
    protected EditablePropertyChangeSupport s;
    
    /**
     * The index of the player that has priority. The index is stored because it is easier to determine the next
     * player this way.
     */
    private EditableProperty<Integer>       prior;
    /**
     * Stores the player that last took an action, which is naturally the first player to pass priority in
     * sequence. If that player would again receive priority (from another player), all players have passed in
     * succession.
     * 
     * The index is stored because it is easier to compare to prior.
     */
    private EditableProperty<Integer>       firstPassed;
    /**
     * Stores if there is currently a prior player. There is no one if turn based actions are processed
     */
    private boolean                         hasPrior;
    
    private EditableProperty<Integer>       phase, step;
    
    private Map<Type, TurnBasedAction>      turnBasedActions;
    private Set<StateBasedAction>           stateBasedActions;
    
    private Combat                          combat;
    
    public PhaseStructureImpl(Game game) {
        super(game);
        listeners = new EditableListenerList(getGame());
        s = new EditablePropertyChangeSupport(getGame(), this);
        prior = new EditableProperty<Integer>(getGame(), null, "prior", -1);
        firstPassed = new EditableProperty<Integer>(getGame(), null, "firstPassed", 0);
        phase = new EditableProperty<Integer>(getGame(), null, "phase", -1);
        step = new EditableProperty<Integer>(getGame(), null, "step", -1);
        
        //no need for editable, since it's only created once
        turnBasedActions = new EnumMap<Type, TurnBasedAction>(Type.class);
        turnBasedActions.put(Type.DRAW, new DrawAction(getGame()));
        turnBasedActions.put(Type.EMPTY_POOLS, new EmptyPoolsAction(getGame()));
        turnBasedActions.put(Type.UNTAP, new UntapAction(getGame()));
        
        //no need for editable, since it's only created once
        stateBasedActions = new HashSet<StateBasedAction>();
        stateBasedActions.add(new LoseOnDrawAction(getGame()));
        stateBasedActions.add(new LethalDamageAction(getGame()));
    }
    
    
    public Phase getPhase() {
        int phase = this.phase.getValue();
        if(phase == -1) throw new IllegalStateException("Before first turn");
        //TODO enable phase adding and skipping
        return Phase.values()[phase];
    }
    
    
    public Step getStep() {
        int step = this.step.getValue();
        if(step == -1) throw new IllegalStateException("Before first turn");
        //TODO enable step adding and skipping
        return getPhase().getSteps().get(step);
    }
    
    
    public Player getPriorPlayer() {
        int prior = this.prior.getValue();
        if(prior == -1) throw new IllegalStateException("Before first turn");
        return hasPrior? getGame().getPlayers().get(prior):null;
    }
    
    
    public void takeAction(boolean took) {
        CompoundEdit ed = new CompoundEdit(getGame(), true,
                took? "Prior player took an action":"Prior player passed priority");
        
        Player oldPrior = prior.getValue().intValue() == -1? null:getPriorPlayer();
        
        hasPrior = false;
        
        if(took) {
            firstPassed.setValue(prior.getValue());
            //a player that took an action receives priority again
        } else {
            //the next player receives priority
            prior.setValue((prior.getValue() + 1) % getGame().getPlayers().size());
            if(prior.getValue().intValue() == firstPassed.getValue().intValue()) {
                passedInSuccession();
            }
        }
        receivePriority();
        
        hasPrior = true;
        
        Player newPrior = getPriorPlayer();
        fireNextPrior(oldPrior, newPrior);
        
        ed.end();
    }
    
    public Combat getCombat() {
        return combat;
    }
    
    /**
     * Gives the player that currently has priority the priority again.
     */
    private void receivePriority() {
        receivePriority(prior.getValue());
    }
    
    /**
     * Gives the specified player priority.
     */
    private void receivePriority(Player player) {
        receivePriority(getGame().getPlayers().indexOf(player));
    }
    
    /**
     * Gives the specified player priority.
     */
    private void receivePriority(int player) {
        if(prior.getValue().intValue() != player) prior.setValue(player);
        beforeReceivePriority();
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
                
                //TODO handle state-based actions
                for(StateBasedAction a:stateBasedActions) {
                    repeat |= a.execute();
                }
            } while(repeat);
            
            //TODO handle triggered abilities
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
     * <li>Finally, the active player receives priority</li>
     * </ul>
     */
    private void passedInSuccession() {
        SortedZone stack = getGame().getStack();
        if(!stack.isEmpty()) {
            MagicObject o = stack.getCards().get(stack.size() - 1);
            //this will remove the object from the stack
            o.getPlayInformation().getEffect().execute();
        } else nextStep();
        //When everything else happened, the active player receives priority
        receivePriority(getGame().getTurnStructure().getActivePlayer());
        firstPassed.setValue(prior.getValue());
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
                for(TurnBasedAction.Type t:getStep().getEndActions()) {
                    TurnBasedAction action = turnBasedActions.get(t);
                    if(action != null) action.execute();
                    else log.warn("Turn based Action " + t + " does not exist");
                }
                step.setValue((step.getValue() + 1) % getPhase().getSteps().size());
            }
            
            if(step.getValue().intValue() == 0) {
                nextPhase();
            }
            
            for(TurnBasedAction.Type t:getStep().getBeginningActions()) {
                TurnBasedAction action = turnBasedActions.get(t);
                if(action != null) action.execute();
                else log.warn("Turn based Action " + t + " does not exist");
            }
            
            Step newStep = getStep();
            fireNextStep(oldStep, newStep);
            
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
        if(step.getValue().intValue() != 0) step.setValue(0);
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
        if(step.getValue().intValue() != 0) step.setValue(0);
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
