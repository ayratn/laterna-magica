/**
 * Handler.java
 * 
 * Created on 23.04.2010
 */

package net.slightlymagic.laterna.magica.util;


/**
 * The class Handler.
 * 
 * @version V0.0 23.04.2010
 * @author Clemens Koza
 */
public interface Handler<T> {
    public void apply(T from);
}
