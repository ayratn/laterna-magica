/**
 * GameContent.java
 * 
 * Created on 12.07.2009
 */

package net.slightlymagic.laterna.magica;




/**
 * <p>
 * The class GameContent. This interface specifies the capabilities that any object in a magic game must provide.
 * </p>
 * 
 * @version V0.0 12.07.2009
 * @author Clemens Koza
 */
public interface GameContent extends Cloneable {
    /**
     * <p>
     * Returns the game to which this content belongs.
     * </p>
     */
    public Game getGame();
}
