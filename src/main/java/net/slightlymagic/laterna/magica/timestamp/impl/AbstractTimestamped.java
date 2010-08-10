/**
 * AbstractTimestamped.java
 * 
 * Created on 03.09.2009
 */

package net.slightlymagic.laterna.magica.timestamp.impl;


import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.impl.AbstractGameContent;
import net.slightlymagic.laterna.magica.timestamp.Timestamp;
import net.slightlymagic.laterna.magica.timestamp.Timestamped;


/**
 * The class AbstractTimestamped.
 * 
 * @version V0.0 03.09.2009
 * @author Clemens Koza
 */
public abstract class AbstractTimestamped extends AbstractGameContent implements Timestamped {
    private Timestamp t;
    
    /**
     * Creates an AbstractTimestamped with a new, independent timestamp.
     */
    public AbstractTimestamped(Game game) {
        super(game);
        t = getGame().getTimestampFactory().newTimestamp();
    }
    
    /**
     * Creates an AbstractTimestamped with the given timestamp. This is desirable for example for continuous
     * effects of static abilities, these always have the timestamp of the object they're printed on.
     */
    public AbstractTimestamped(Game game, Timestamp t) {
        super(game);
        this.t = t;
    }
    
    public Timestamp getTimestamp() {
        return t;
    }
    
    public int compareTo(Timestamped o) {
        return getTimestamp().compareTo(o.getTimestamp());
    }
}
