/**
 * EditableManySideImpl.java
 * 
 * Created on 16.08.2010
 */

package net.slightlymagic.laterna.magica.util.relational.impl;


import net.slightlymagic.laterna.magica.util.relational.ManySide;
import net.slightlymagic.laterna.magica.util.relational.OneSide;


/**
 * The class EditableManySideImpl.
 * 
 * @version V0.0 16.08.2010
 * @author Clemens Koza
 */
public class ManySideImpl<M, O> extends Entity<M> implements ManySide<M, O> {
    OneSideImpl<O, M> oneSide;
    
    public ManySideImpl() {
        this(null);
    }
    
    public ManySideImpl(M value) {
        super(value);
    }
    
    public void setOneSide(OneSide<O, M> e) {
        OneSideImpl<O, M> entity = (OneSideImpl<O, M>) e;
        if(oneSide != null) oneSide.manySide.remove(this);
        oneSide = entity;
        if(oneSide != null) oneSide.manySide.add(this);
    }
    
    public OneSide<O, M> getOneSide() {
        return oneSide;
    }
    
    public O getOneSideValue() {
        return oneSide == null? null:oneSide.getValue();
    }
}
