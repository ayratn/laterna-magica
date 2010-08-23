/**
 * MagicActor.java
 * 
 * Created on 23.08.2010
 */

package net.slightlymagic.concurrent;


import org.jetlang.channels.Channel;
import org.jetlang.core.Callback;
import org.jetlang.fibers.Fiber;


/**
 * The class MagicActor.
 * 
 * @version V0.0 23.08.2010
 * @author Clemens Koza
 */
public abstract class Actor<I, O> {
    private Channel<I> inChannel;
    private Channel<O> outChannel;
    private Fiber      fiber;
    
    public Actor(Channel<I> inChannel, Channel<O> outChannel, Fiber fiber) {
        this.inChannel = inChannel;
        this.outChannel = outChannel;
        this.fiber = fiber;
    }
    
    public void start() {
        // set up subscription listener
        Callback<I> onReceive = new Callback<I>() {
            public void onMessage(I input) {
                // process poison pill, dispose current actor and pass the message
                // on to the next actor in the chain (above)
                if(shouldDispose(input)) {
                    fiber.dispose();
                } else {
                    O output = act(input);
                    if(outChannel != null) {
                        outChannel.publish(output);
                    }
                }
            }
        };
        
        // subscribe to incoming channel
        inChannel.subscribe(fiber, onReceive);
        // start the fiber
        fiber.start();
    }
    
    /**
     * Processes the incoming message
     */
    protected abstract O act(I input);
    
    /**
     * Determines if the message should stop the actor. This implementation always returns {@code false}.
     */
    protected boolean shouldDispose(I input) {
        return false;
    }
}
