/**
 * Timestamped.java
 * 
 * Created on 02.09.2009
 */

package net.slightlymagic.laterna.magica.timestamp;


import net.slightlymagic.laterna.magica.GameContent;


/**
 * The class Timestamped. A timestamped object has a {@link Timestamp} associated with it.
 * 
 * @version V0.0 02.09.2009
 * @author Clemens Koza
 */
public interface Timestamped extends Comparable<Timestamped>, GameContent {
    /**
     * @return The object's timestamp
     */
    public Timestamp getTimestamp();
}
