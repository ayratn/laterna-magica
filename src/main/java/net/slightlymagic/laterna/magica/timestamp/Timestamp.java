/**
 * Timestamp.java
 * 
 * Created on 12.07.2009
 */

package net.slightlymagic.laterna.magica.timestamp;


import static java.lang.String.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.slightlymagic.laterna.magica.edit.CompoundEdit;
import net.slightlymagic.laterna.magica.edit.Edit;
import net.slightlymagic.laterna.magica.edit.impl.EditableListenerList;
import net.slightlymagic.laterna.magica.event.MoveCardListener;
import net.slightlymagic.laterna.magica.event.TimestampListener;
import net.slightlymagic.laterna.magica.impl.AbstractGameContent;
import net.slightlymagic.laterna.magica.util.ExtendedListenerList;


import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;


/**
 * The class Timestamp. Timestamps are used to determine the applying order of continuous effects in the same
 * layer. For details, see {@magic.ruleRef 612.6 CR 612.6}. For no object with a timestamp, the
 * timestamp has to stay the same. Additionally, a timestamp value is unique in a game.
 * 
 * @version V0.0 12.07.2009
 * @author Clemens Koza
 */
public class Timestamp extends AbstractGameContent implements Comparable<Timestamp> {
    private TimestampFactory       f;
    private int                    timestamp;
    
    protected ExtendedListenerList listeners;
    
    Timestamp(TimestampFactory f) {
        super(f.getGame());
        listeners = new EditableListenerList(getGame());
        this.f = f;
        updateTimestamp();
    }
    
    /**
     * Return's the timestamp's value as an int.
     */
    public int getTimestamp() {
        return timestamp;
    }
    
    /**
     * Advances this Timestamp to the next timestamp and returns it.
     */
    public int updateTimestamp() {
        CompoundEdit edit = new CompoundEdit(getGame(), true, "Update Timestamp");
        f.nextTimestamp();
        new UpdateTimestampEdit().execute();
        edit.end();
        return getTimestamp();
    }
    
    public int compareTo(Timestamp o) {
        assert o.f == f;
        return timestamp - o.timestamp;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((f == null)? 0:f.hashCode());
        result = prime * result + timestamp;
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null) return false;
        if(getClass() != obj.getClass()) return false;
        Timestamp other = (Timestamp) obj;
        if(f == null) {
            if(other.f != null) return false;
        } else if(!f.equals(other.f)) return false;
        if(timestamp != other.timestamp) return false;
        return true;
    }
    
    @Override
    public String toString() {
        return valueOf(timestamp);
    }
    
    protected void fireTimestampUpdated() {
        PeekingIterator<TimestampListener> t = Iterators.peekingIterator(getTimestampListeners());
        PeekingIterator<TimestampListener> f = Iterators.peekingIterator(this.f.getTimestampListeners());
        
        TimestampListener l;
        Set<TimestampListener> prev = new HashSet<TimestampListener>();
        
        while(t.hasNext() && t.peek() instanceof MoveCardListener.Internal)
            if(prev.add(l = t.next())) l.timestampUpdated(this);
        while(f.hasNext() && f.peek() instanceof MoveCardListener.Internal)
            if(prev.add(l = f.next())) l.timestampUpdated(this);
        
        while(t.hasNext())
            if(prev.add(l = t.next())) l.timestampUpdated(this);
        while(f.hasNext())
            if(prev.add(l = f.next())) l.timestampUpdated(this);
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
    
    private class UpdateTimestampEdit extends Edit {
        private static final long serialVersionUID = -2740256948787388566L;
        private int               before;
        
        public UpdateTimestampEdit() {
            super(Timestamp.this.getGame());
        }
        
        @Override
        public void execute() {
            before = timestamp;
            timestamp = f.getCurrentTimestamp();
        }
        
        @Override
        public void rollback() {
            timestamp = before;
        }
        
        @Override
        public String toString() {
            return format("Update timestamp to %d", timestamp);
        }
    }
}
