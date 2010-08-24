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

import net.slightlymagic.beans.properties.Property;
import net.slightlymagic.laterna.magica.event.MoveCardListener;
import net.slightlymagic.laterna.magica.event.TimestampListener;
import net.slightlymagic.laterna.magica.impl.AbstractGameContent;

import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;


/**
 * The class Timestamp. Timestamps are used to determine the applying order of continuous effects in the same
 * layer. For details, see {@magic.ruleRef 20100716/R6136}. For no object with a timestamp, the
 * timestamp has to stay the same. Additionally, a timestamp value is unique in a game.
 * 
 * @version V0.0 12.07.2009
 * @author Clemens Koza
 */
public class Timestamp extends AbstractGameContent implements Comparable<Timestamp> {
    private TimestampFactory  f;
    private Property<Integer> timestamp;
    
    Timestamp(TimestampFactory f) {
        super(f.getGame());
        this.f = f;
        timestamp = properties.property("timestamp", 0);
        updateTimestamp();
    }
    
    /**
     * Return's the timestamp's value as an int.
     */
    public int getTimestamp() {
        return timestamp.getValue();
    }
    
    /**
     * Advances this Timestamp to the next timestamp and returns it.
     */
    public int updateTimestamp() {
        timestamp.setValue(f.nextTimestamp());
        return getTimestamp();
    }
    
    public int compareTo(Timestamp o) {
        assert o.f == f;
        return timestamp.getValue().compareTo(o.timestamp.getValue());
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((f == null)? 0:f.hashCode());
        result = prime * result + ((timestamp == null)? 0:timestamp.hashCode());
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
        if(timestamp == null) {
            if(other.timestamp != null) return false;
        } else if(!timestamp.equals(other.timestamp)) return false;
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
}
