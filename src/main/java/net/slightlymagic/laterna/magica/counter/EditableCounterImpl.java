/**
 * EditableCounterImpl.java
 * 
 * Created on 31.03.2010
 */

package net.slightlymagic.laterna.magica.counter;


import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.edit.property.EditableProperty;
import net.slightlymagic.laterna.magica.edit.property.EditablePropertyChangeSupport;
import net.slightlymagic.laterna.magica.impl.AbstractGameContent;


/**
 * The class EditableCounterImpl.
 * 
 * @version V0.0 31.03.2010
 * @author Clemens Koza
 */
public class EditableCounterImpl extends AbstractGameContent implements EditableCounter {
    private EditablePropertyChangeSupport s;
    private EditableProperty<Integer>     count;
    
    public EditableCounterImpl(Game game) {
        super(game);
        s = new EditablePropertyChangeSupport(getGame(), this);
        count = new EditableProperty<Integer>(getGame(), s, "count", 0);
    }
    
    public int getCount() {
        return count.getValue();
    }
    
    public void increase() {
        count.setValue(count.getValue() + 1);
    }
    
    public void reset() {
        count.setValue(0);
    }
}
