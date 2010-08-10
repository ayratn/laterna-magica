/**
 * PlayAction.java
 * 
 * Created on 30.03.2010
 */

package net.slightlymagic.laterna.magica.action.play;


import java.util.List;

import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.ability.AbilityObject;
import net.slightlymagic.laterna.magica.ability.TriggeredAbility;
import net.slightlymagic.laterna.magica.action.AbstractGameAction;
import net.slightlymagic.laterna.magica.action.CompoundActionImpl;
import net.slightlymagic.laterna.magica.action.GameAction;
import net.slightlymagic.laterna.magica.cost.ManaCost;
import net.slightlymagic.laterna.magica.edit.Edit;
import net.slightlymagic.laterna.magica.player.Player;
import net.slightlymagic.laterna.magica.turnStructure.PhaseStructure;


/**
 * The class PlayAction. A play action is an action a player can begin by himself when he has priority, for example
 * playing a spell or ability, or taking one of the special actions
 * 
 * @version V0.0 30.03.2010
 * @author Clemens Koza
 */
public abstract class PlayAction extends AbstractGameAction {
    private Player      controller;
    private MagicObject ob;
    
    public PlayAction(Player controller) {
        this(controller, null);
    }
    
    public PlayAction(Player controller, MagicObject ob) {
        super(controller.getGame());
        this.controller = controller;
        this.ob = ob;
    }
    
    protected void setObject(MagicObject ob) {
        this.ob = ob;
    }
    
    /**
     * Returns the played spell's or ability's controller.
     */
    public Player getController() {
        return controller;
    }
    
    /**
     * Returns the played spell or ability.
     */
    public MagicObject getObject() {
        return ob;
    }
    
    /**
     * Returns if it is currently legal to take this action. It may be illegal for several reasons. To make it
     * easier for subclasses to override this, it is splitted into {@link #isLegalTiming()} and
     * {@link #isLegalState()}. Subclasses are encouraged to do the same for more specific checks.
     */
    public boolean isLegal() {
        return getObject().isLegal(this);
    }
    
    /**
     * Play actions check if execution is legal and return false if not.
     * 
     * Otherwise, {@link #execute0()} is called. Afterwards, {@link PhaseStructure#takeAction(boolean)
     * PhaseStructure.takeAction(true)} is called. Then {@link #execute0()}'s return value is returned.
     */
    @Override
    public boolean execute() {
        //triggered abilities return false, but can trigger any time
        if(!(ob instanceof TriggeredAbility || isLegal())) return false;
        if(!execute0()) return false;
        getGame().getPhaseStructure().takeAction(true);
        return true;
    }
    
    /**
     * Implement this method to execute the action
     */
    protected boolean execute0() {
        Edit state = getGame().getGameState().getCurrent();
        
        //Steps of playing a spell (601.2):
        
        //Putting the spell onto the stack
        getObject().play(this);
        
        PlayInformation info = getObject().getPlayInformation();
        
        //Choose modes, Splice, alternative and additional costs
        //(here is the place to choose how to pay for hybrid/variable symbols)
        info.makeChoices();
        
        //Choose appropriate targets for the choices made
        info.chooseTargets();
        
        //Distribute effects, if applicable
        info.distributeEffect();
        
        //Determine the total cost, including cost reductions/increases
        GameAction cost = info.getCost();
        
        //If the cost contains mana, activate mana abilities
        if(isManaCost(cost)) {
            //TODO let activate mana abilities
        }
        
        //Pay the cost
        if(!cost.execute()) {
            //rollback to before the ability
            getGame().getGameState().stepTo(state);
            return false;
        }
        
        //If it's a mana ability, immediately resolve it
        if((ob instanceof AbilityObject) && ((AbilityObject) ob).getAbility().isManaAbility()) {
            info.getEffect().execute();
            ob.resetPlayInformation();
        }
        
        return true;
    }
    
    private static boolean isManaCost(GameAction cost) {
        //is the cost a mana cost?
        if(cost instanceof ManaCost) return true;
        else if(cost instanceof CompoundActionImpl) {
            //does the cost contain a mana cost?
            List<? extends GameAction> actions = ((CompoundActionImpl) cost).getActions();
            for(GameAction action:actions)
                if(isManaCost(action)) return true;
            //otherwise...
            return false;
        } else return false;
    }
}
