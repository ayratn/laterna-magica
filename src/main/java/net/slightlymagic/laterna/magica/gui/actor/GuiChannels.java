/**
 * GuiChannels.java
 * 
 * Created on 23.08.2010
 */

package net.slightlymagic.laterna.magica.gui.actor;


import java.util.concurrent.Executors;

import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.action.play.PlayAction;

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
     * Channel for publishing {@link MagicObject}s when the user clicks on them
     */
    public final Channel<MagicObject> objects      = new MemoryChannel<MagicObject>();
    
    /**
     * Channel for publishing when the user clicks "pass priority"
     */
    public final Channel<Void>        passPriority = new MemoryChannel<Void>();
    
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
