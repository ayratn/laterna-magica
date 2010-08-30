/**
 * GuiChannels.java
 * 
 * Created on 23.08.2010
 */

package net.slightlymagic.laterna.magica.gui.actor;


import java.util.concurrent.Executors;

import net.slightlymagic.laterna.magica.Combat.Attacker;
import net.slightlymagic.laterna.magica.Combat.Blocker;
import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.action.play.PlayAction;
import net.slightlymagic.laterna.magica.player.Player;

import org.jetlang.channels.Channel;
import org.jetlang.channels.MemoryChannel;
import org.jetlang.fibers.Fiber;
import org.jetlang.fibers.PoolFiberFactory;


/**
 * The class GuiChannels.
 * 
 * @version V0.0 23.08.2010
 * @author Clemens Koza
 */
public class GuiChannels {
    /**
     * Channel for receiving {@link PlayAction}s to execute when the player has priority
     */
    public final Channel<PlayAction>  actions      = new MemoryChannel<PlayAction>();
    
    /**
     * Channel for receiving {@link Attacker}s
     */
    public final Channel<Attacker>    attackers    = new MemoryChannel<Attacker>();
    
    /**
     * Channel for receiving {@link Blocker}s
     */
    public final Channel<Blocker>     blockers     = new MemoryChannel<Blocker>();
    
    /**
     * Channel for publishing {@link MagicObject}s when the user clicks on them
     */
    public final Channel<MagicObject> objects      = new MemoryChannel<MagicObject>();
    
    /**
     * Channel for publishing {@link Player}s when the user clicks on them
     */
    public final Channel<Player>      players      = new MemoryChannel<Player>();
    
    /**
     * Channel for publishing when the user clicks "pass priority"
     */
    public final Channel<Void>        passPriority = new MemoryChannel<Void>();
    
    /**
     * Using one fiber for all callbacks essentially means that they will be executed sequentially. Say, one user
     * input after another. This is not a problem, since none of the callbacks will block.
     */
    public final Fiber                fiber;
    
    public GuiChannels() {
        PoolFiberFactory f = new PoolFiberFactory(Executors.newCachedThreadPool());
        fiber = start(f.create());
    }
    
    private Fiber start(Fiber f) {
        f.start();
        return f;
    }
}
