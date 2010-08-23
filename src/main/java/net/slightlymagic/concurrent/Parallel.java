/**
 * Parallel.java
 * 
 * Created on 23.08.2010
 */

package net.slightlymagic.concurrent;


import org.jetlang.channels.Channel;
import org.jetlang.core.Callback;
import org.jetlang.core.Disposable;
import org.jetlang.fibers.Fiber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The class Parallel.
 * 
 * @version V0.0 23.08.2010
 * @author Clemens Koza
 */
public class Parallel {
    private static final Logger log = LoggerFactory.getLogger(Parallel.class);
    
    /**
     * Returns the next value published to the channel. This method blocks until a value is published.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getValue(Fiber fiber, Channel<T> channel) {
        final Object[] value = new Object[2];
        
        log.debug("Waiting for value...");
        synchronized(value) {
            Disposable d = channel.subscribe(fiber, new Callback<T>() {
                public void onMessage(T message) {
                    log.debug("receiving: " + message);
                    synchronized(value) {
                        value[0] = "";
                        value[1] = message;
                        value.notify();
                    }
                };
            });
            while(value[0] == null)
                try {
                    value.wait();
                } catch(InterruptedException ex) {}
            d.dispose();
        }
        log.debug("Value received: " + value[1]);
        return (T) value[1];
    }
}
