/**
 * EditableOneToOneImpl.java
 * 
 * Created on 16.08.2010
 */

package net.slightlymagic.laterna.magica.util.relational.editableImpl;


import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.edit.property.EditableProperty;
import net.slightlymagic.laterna.magica.edit.property.EditablePropertyChangeSupport;
import net.slightlymagic.laterna.magica.util.relational.OneToOne;


/**
 * The class EditableOneToOneImpl.
 * 
 * @version V0.0 16.08.2010
 * @author Clemens Koza
 */
public class EditableOneToOneImpl<A, B> extends EditableEntity<A> implements OneToOne<A, B> {
    private final EditableProperty<EditableOneToOneImpl<B, A>> otherSide;
    
    public EditableOneToOneImpl(EditablePropertyChangeSupport s) {
        this(s, null);
    }
    
    public EditableOneToOneImpl(EditablePropertyChangeSupport s, A value) {
        this(s.getGame(), s, value);
    }
    
    public EditableOneToOneImpl(Game game) {
        this(game, null);
    }
    
    public EditableOneToOneImpl(Game game, A value) {
        this(game, null, value);
    }
    
    private EditableOneToOneImpl(Game game, EditablePropertyChangeSupport s, A value) {
        super(game, s, value);
        
        otherSide = new EditableProperty<EditableOneToOneImpl<B, A>>(getGame(), s, "otherSide");
    }
    
    public void setOtherSide(OneToOne<B, A> e) {
        EditableOneToOneImpl<B, A> entity = (EditableOneToOneImpl<B, A>) e;
        if(entity == null) {
            if(otherSide.getValue() != null) otherSide.getValue().otherSide.setValue(null);
            otherSide.setValue(null);
        } else {
            if(entity.otherSide.getValue() != null && entity.otherSide.getValue() != this) throw new IllegalArgumentException(
                    "Entity already has relation");
            
            setOtherSide(null);
            this.otherSide.setValue(entity);
            entity.otherSide.setValue(this);
        }
    }
    
    public EditableOneToOneImpl<B, A> getOtherSide() {
        return otherSide.getValue();
    }
    
    public B getOtherSideValue() {
        return otherSide.getValue() == null? null:otherSide.getValue().getValue();
    }
}
