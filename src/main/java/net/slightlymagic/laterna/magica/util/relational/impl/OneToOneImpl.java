/**
 * EditableOneToOneImpl.java
 * 
 * Created on 16.08.2010
 */

package net.slightlymagic.laterna.magica.util.relational.impl;


import net.slightlymagic.laterna.magica.util.relational.OneToOne;


/**
 * The class EditableOneToOneImpl.
 * 
 * @version V0.0 16.08.2010
 * @author Clemens Koza
 */
public class OneToOneImpl<A, B> extends Entity<A> implements OneToOne<A, B> {
    private OneToOneImpl<B, A> otherSide;
    
    public OneToOneImpl() {
        this(null);
    }
    
    public OneToOneImpl(A value) {
        super(value);
    }
    
    public void setOtherSide(OneToOne<B, A> e) {
        OneToOneImpl<B, A> entity = (OneToOneImpl<B, A>) e;
        if(entity == null) {
            if(otherSide != null) otherSide.otherSide = null;
            otherSide = null;
        } else {
            if(entity.otherSide != null && entity.otherSide != this) throw new IllegalArgumentException(
                    "EditableEntity already has relation");
            
            setOtherSide(null);
            this.otherSide = entity;
            entity.otherSide = this;
        }
    }
    
    public OneToOne<B, A> getOtherSide() {
        return otherSide;
    }
    
    public B getOtherSideValue() {
        return otherSide == null? null:otherSide.getValue();
    }
}
