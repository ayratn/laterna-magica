/**
 * TimestampFactory.java
 * 
 * Created on 04.09.2009
 */

package net.slightlymagic.laterna.magica.timestamp;


import static java.lang.String.*;

import java.util.Iterator;

import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.edit.Edit;
import net.slightlymagic.laterna.magica.edit.impl.EditableListenerList;
import net.slightlymagic.laterna.magica.event.TimestampListener;
import net.slightlymagic.laterna.magica.impl.AbstractGameContent;
import net.slightlymagic.laterna.magica.util.ExtendedListenerList;



/**
 * The class TimestampFactory.
 * 
 * @version V0.0 04.09.2009
 * @author Clemens Koza
 */
public class TimestampFactory extends AbstractGameContent {
    private int                    currentTimestamp;
    
    protected ExtendedListenerList listeners;
    
    public TimestampFactory(Game game) {
        super(game);
        listeners = new EditableListenerList(getGame());
    }
    
    public Timestamp newTimestamp() {
        return new Timestamp(this);
    }
    
    /**
     * Returns the Timestamp that was previously given.
     */
    public int getCurrentTimestamp() {
        return currentTimestamp;
    }
    
    /**
     * Advances the factory to the next timestamp and returns it.
     */
    int nextTimestamp() {
        new NextTimestampEdit().execute();
        return getCurrentTimestamp();
    }
    
    public void addTimestampListener(TimestampListener l) {
        if(l instanceof TimestampListener.Internal) {
            listeners.add(TimestampListener.Internal.class, (TimestampListener.Internal) l);
        } else listeners.add(TimestampListener.class, l);
    }
    
    public void removeTimestampListener(TimestampListener l) {
        if(l instanceof TimestampListener.Internal) {
            listeners.remove(TimestampListener.Internal.class, (TimestampListener.Internal) l);
        } else listeners.remove(TimestampListener.class, l);
    }
    
    @SuppressWarnings("unchecked")
    public Iterator<TimestampListener> getTimestampListeners() {
        return listeners.getIterator(TimestampListener.Internal.class, TimestampListener.class);
    }
    
    private class NextTimestampEdit extends Edit {
        private static final long serialVersionUID = 6541389563852255187L;
        private int               currentTimestamp;
        
        public NextTimestampEdit() {
            super(TimestampFactory.this.getGame());
        }
        
        @Override
        public void execute() {
            //Set the timestamp after that of this move as current
            currentTimestamp = TimestampFactory.this.currentTimestamp;
            TimestampFactory.this.currentTimestamp++;
        }
        
        @Override
        public void rollback() {
            //Resets to the current timestamp of this move
            TimestampFactory.this.currentTimestamp = currentTimestamp;
        }
        
        @Override
        public String toString() {
            return format("Advance timestamp factory to %d", currentTimestamp + 1);
        }
    }
}
