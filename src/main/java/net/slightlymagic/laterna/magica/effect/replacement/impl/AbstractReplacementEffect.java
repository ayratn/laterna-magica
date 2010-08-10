/**
 * AbstractReplacementEffect.java
 * 
 * Created on 22.03.2010
 */

package net.slightlymagic.laterna.magica.effect.replacement.impl;


import net.slightlymagic.laterna.magica.effect.replacement.ReplaceableEvent;
import net.slightlymagic.laterna.magica.effect.replacement.ReplacementEffect;


/**
 * The class AbstractReplacementEffect. Makes the implementation of replacement effects easier by providing a
 * ReplacementType attribute and automatic checking if the action is of the expected class.
 * 
 * @version V0.0 22.03.2010
 * @author Clemens Koza
 */
public abstract class AbstractReplacementEffect<T extends ReplaceableEvent> implements ReplacementEffect {
    private Class<T>        clazz;
    private ReplacementType type;
    
    public AbstractReplacementEffect(Class<T> clazz, ReplacementType type) {
        this.clazz = clazz;
        this.type = type;
    }
    
    public ReplacementType getType() {
        return type;
    }
    
    public boolean apply(ReplaceableEvent e) {
        if(!clazz.isInstance(e)) return false;
        return apply0(clazz.cast(e));
    }
    
    /**
     * Use this method for additional restrictions on what events to replace.
     */
    protected boolean apply0(T e) {
        return true;
    }
    
    public ReplaceableEvent replace(ReplaceableEvent e) {
        return replace0(clazz.cast(e));
    }
    
    /**
     * Use this method for actually replacing the event.
     */
    protected abstract ReplaceableEvent replace0(T e);
}
