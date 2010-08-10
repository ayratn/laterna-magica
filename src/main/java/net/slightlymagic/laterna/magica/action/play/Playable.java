/**
 * Playable.java
 * 
 * Created on 21.04.2010
 */

package net.slightlymagic.laterna.magica.action.play;


import net.slightlymagic.laterna.magica.GameContent;


/**
 * The class Playable.
 * 
 * @version V0.0 21.04.2010
 * @author Clemens Koza
 */
public interface Playable extends GameContent {
    public void play(PlayAction a);
    
    /**
     * Returns if the ability can currently be played.
     */
    public boolean isLegal(PlayAction a);
    
    /**
     * Returns the {@link PlayInformation} that was used to play this playable. The PlayInformation is
     * automatically set by {@link #play(PlayAction)}. This property is only defined after a playable was
     * {@link #play(PlayAction) played} and before it resolved.
     * 
     * @throws IllegalStateException If the property is not defined (see above)
     */
    public PlayInformation getPlayInformation();
    
    /**
     * Called after the playable resolved
     */
    public void resetPlayInformation();
}
