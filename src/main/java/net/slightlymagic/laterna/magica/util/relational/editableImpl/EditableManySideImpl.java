/**
 * EditableManySideImpl.java
 * 
 * Created on 16.08.2010
 */

package net.slightlymagic.laterna.magica.util.relational.editableImpl;


import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.edit.property.EditableProperty;
import net.slightlymagic.laterna.magica.util.relational.ManySide;
import net.slightlymagic.laterna.magica.util.relational.OneSide;


/**
 * The class EditableManySideImpl.
 * 
 * @version V0.0 16.08.2010
 * @author Clemens Koza
 */
public class EditableManySideImpl<M, O> extends EditableEntity<M> implements ManySide<M, O> {
    final EditableProperty<EditableOneSideImpl<O, M>> oneSide;
    
    public EditableManySideImpl(Game game) {
        this(game, null);
    }
    
    public EditableManySideImpl(Game game, M value) {
        super(game, value);
        oneSide = new EditableProperty<EditableOneSideImpl<O, M>>(getGame(), null, "oneSide");
    }
    
    public void setOneSide(OneSide<O, M> e) {
        EditableOneSideImpl<O, M> entity = (EditableOneSideImpl<O, M>) e;
        if(oneSide.getValue() != null) oneSide.getValue().manySide.remove(this);
        oneSide.setValue(entity);
        if(entity != null) entity.manySide.add(this);
    }
    
    public EditableOneSideImpl<O, M> getOneSide() {
        return oneSide.getValue();
    }
    
    public O getOneSideValue() {
        return oneSide.getValue() == null? null:oneSide.getValue().getValue();
    }
}
