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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


/**
 * The class ObjectCopy.
 * 
 * @version V0.0 28.03.2010
 * @author Clemens Koza
 */
public final class ObjectCopy {
    public static void main(String[] args) {
        ObjectCopy c = new ObjectCopy();
        System.out.println(c.copy("a"));
        System.out.println(c.copy("b"));
        System.out.println(c.copy("c"));
    }
    
    private Thread                t;
    private BlockingQueue<Object> queue;
    private ObjectOutputStream    os;
    private ObjectInputStream     is;
    
    public ObjectCopy() {
        try {
            PipedOutputStream os = new PipedOutputStream();
            PipedInputStream is = new PipedInputStream(os);
            this.os = new ObjectOutputStream(new BufferedOutputStream(os));
            //flush the stream so the input stream can read the header
            this.os.flush();
            this.is = new ObjectInputStream(new BufferedInputStream(is));
            queue = new ArrayBlockingQueue<Object>(1);
            t = new Thread(new Writer(this));
            t.setDaemon(true);
            t.start();
        } catch(IOException ex) {
            throw new AssertionError(ex);
        }
    }
    
    @SuppressWarnings("unchecked")
    public <T> T copy(T o) {
        try {
            queue.put(o);
            return (T) is.readObject();
        } catch(IOException ex) {
            throw new AssertionError(ex);
        } catch(ClassNotFoundException ex) {
            throw new AssertionError(ex);
        } catch(InterruptedException ex) {
            throw new AssertionError(ex);
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
        is.close();
        os.close();
        t.interrupt();
        super.finalize();
    }
    
    //static so that ObjectCopy can be garbage collected
    private static class Writer implements Runnable {
        private BlockingQueue<Object> queue;
        private ObjectOutputStream    os;
        
        private Writer(ObjectCopy c) {
            queue = c.queue;
            os = c.os;
        }
        
        public void run() {
            try {
                while(true) {
                    os.writeObject(queue.take());
                    os.flush();
                }
            } catch(IOException ex) {
                throw new AssertionError(ex);
            } catch(InterruptedException ex) {
                //ends the thread from the interruption
                return;
            }
        }
    }
}
