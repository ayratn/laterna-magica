/**
 * CompoundActionImpl.java
 * 
 * Created on 16.04.2010
 */

package net.slightlymagic.laterna.magica.action;


import static java.util.Arrays.*;
import static java.util.Collections.*;

import java.util.List;


/**
 * The class CompoundActionImpl.
 * 
 * @version V0.0 16.04.2010
 * @author Clemens Koza
 */
public class CompoundActionImpl extends AbstractGameAction {
    private List<? extends GameAction> actions;
    
    /**
     * Constructs a compound action with the specified child actions. At least one action is necessary, or a
     * {@link IndexOutOfBoundsException} is thrown.
     */
    public CompoundActionImpl(GameAction... actions) {
        this(asList(actions));
    }
    
    /**
     * Constructs a compound action with the specified child actions. At least one action is necessary, or a
     * {@link IndexOutOfBoundsException} is thrown.
     */
    public CompoundActionImpl(List<? extends GameAction> actions) {
        super(actions.get(0).getGame());
        this.actions = unmodifiableList(actions);
    }
    
    public List<? extends GameAction> getActions() {
        return actions;
    }
    
    @Override
    public boolean execute() {
        for(GameAction a:getActions())
            if(!a.execute()) return false;
        return true;
    }
}
