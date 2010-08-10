/**
 * ReplaceableEvent.java
 * 
 * Created on 22.03.2010
 */

package net.slightlymagic.laterna.magica.effect.replacement;


import static com.google.common.base.Suppliers.*;
import static net.slightlymagic.laterna.magica.util.MagicaSuppliers.*;
import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.action.AbstractGameAction;
import net.slightlymagic.laterna.magica.player.Player;

import com.google.common.base.Supplier;


/**
 * The class ReplaceableEvent. A replaceable event may be replaced by {@link ReplacementEffect}s before it is
 * executed.
 * 
 * @version V0.0 22.03.2010
 * @author Clemens Koza
 */
public abstract class ReplaceableEvent extends AbstractGameAction {
    private static final long      serialVersionUID = 6669563660646566843L;
    private final Supplier<Player> affected;
    
    public ReplaceableEvent(MagicObject affected) {
        this(you(ofInstance(affected)));
    }
    
    public ReplaceableEvent(Player affected) {
        this(ofInstance(affected));
    }
    
    public ReplaceableEvent(Supplier<Player> affected) {
        super(affected.get().getGame());
        this.affected = affected;
    }
    
    /**
     * Returns the player affected by this action. The affected player gets to order replacement effects if there
     * are more than one to replace this event.
     */
    public Player getAffectedPlayer() {
        return affected.get();
    }
    
    /**
     * Calling this replaces the event and {@link #execute0() executes} that event.
     */
    @Override
    public boolean execute() {
        return getGame().getReplacementEngine().execute(this);
    }
    
    /**
     * Implementation for executing this event. Usually, this will create and execute one or more edits, but
     * execute is not limited to that. execute0() is called by the {@link ReplacementEngine}. Users call
     * {@link #execute()}.
     * 
     * <p>
     * Note: Be careful in the case of nested replaceable effects: If a replaceable effect executes the effect it
     * replaced as part of its execute0() method, that effect must be executed by calling execute0(). Otherwise,
     * the inner effect would be replaced again, likely resulting in infinite recursion.
     * </p>
     */
    protected abstract boolean execute0();
}
