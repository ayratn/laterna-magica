/**
 * ObjectCopy.java
 * 
 * Created on 28.03.2010
 */

package net.slightlymagic.concurrent;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.Executors;

import org.jetlang.channels.Channel;
import org.jetlang.channels.MemoryChannel;
import org.jetlang.fibers.Fiber;
import org.jetlang.fibers.PoolFiberFactory;


/**
 * The class ObjectCopy.
 * 
 * @version V0.0 28.03.2010
 * @author Clemens Koza
 */
public final class ObjectCopyNew {
    public static void main(String[] args) {
        ObjectCopyNew c = new ObjectCopyNew();
        System.out.println(c.copy("a"));
        System.out.println(c.copy("b"));
        System.out.println(c.copy("c"));
        c.dispose();
    }
    
    private final Channel<Object> input;
    private final CopyActor       copy;
    
    public ObjectCopyNew() {
        input = new MemoryChannel<Object>();
        
        Fiber f = new PoolFiberFactory(Executors.newCachedThreadPool()).create();
        copy = new CopyActor(input, f);
        copy.start();
    }
    
    @SuppressWarnings("unchecked")
    public <T> T copy(T o) {
        input.publish(o);
        try {
            return (T) copy.is.readObject();
        } catch(IOException ex) {
            throw new AssertionError(ex);
        } catch(ClassNotFoundException ex) {
            throw new AssertionError(ex);
        }
    }
    
    public void dispose() {
        input.publish(copy.STOP);
    }
    
    @Override
    protected void finalize() throws Throwable {
        dispose();
        super.finalize();
    }
    
    private static class CopyActor extends Actor<Object, Void> {
        private final Object             STOP = new Object();
        
        private final ObjectInputStream  is;
        private final ObjectOutputStream os;
        
        public CopyActor(Channel<Object> inChannel, Fiber fiber) {
            super(inChannel, null, fiber);
            try {
                PipedOutputStream os = new PipedOutputStream();
                PipedInputStream is = new PipedInputStream(os);
                this.os = new ObjectOutputStream(new BufferedOutputStream(os));
                //flush the stream so the input stream can read the header
                this.os.flush();
                this.is = new ObjectInputStream(new BufferedInputStream(is));
            } catch(IOException ex) {
                throw new AssertionError(ex);
            }
        }
        
        @Override
        protected Void act(Object input) {
            try {
                os.writeObject(input);
                os.flush();
            } catch(IOException ex) {
                throw new AssertionError(ex);
            }
            return null;
        }
        
        @Override
        protected boolean shouldDispose(Object input) {
            return input == STOP;
        }
    }
}
