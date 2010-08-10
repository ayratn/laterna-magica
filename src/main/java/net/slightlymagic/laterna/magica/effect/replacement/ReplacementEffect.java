/**
 * ReplacementEffect.java
 * 
 * Created on 22.03.2010
 */

package net.slightlymagic.laterna.magica.effect.replacement;




/**
 * The class ReplacementEffect. A replacement effect replaces applicable events by new ones.
 * 
 * @version V0.0 22.03.2010
 * @author Clemens Koza
 */
public interface ReplacementEffect {
    /**
     * The types of replacement effects. If there is more than one applicable replacement effect, a self
     * replacement effect must be chosen if possible. Next, a controller changing effect must be chosen if
     * possible. After all such effects were applied, any other effects may be chosen.
     */
    public static enum ReplacementType {
        /**
         * Type for a self-replacement effect. See {@magic.ruleRef 614.14 CR 614.14}
         */
        SELF,
        /**
         * Type for a replacement effect that changes under whose control a permanent would come into play
         */
        CONTROLLER,
        /**
         * Type for normal replacement effects
         */
        OTHER;
    }
    
    /**
     * Returns the type of this replacement effect.
     */
    public ReplacementType getType();
    
    /**
     * Returns if the effect should be applied to the event. If true, {@link #replace(ReplaceableEvent)} may be
     * called.
     */
    public boolean apply(ReplaceableEvent e);
    
    /**
     * Returns a modified event for the given event.
     */
    public ReplaceableEvent replace(ReplaceableEvent e);
}
