/**
 * EditableCounterImpl.java
 * 
 * Created on 31.03.2010
 */

package net.slightlymagic.laterna.magica.counter;


import net.slightlymagic.beans.properties.Property;
import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.impl.AbstractGameContent;


/**
 * The class EditableCounterImpl.
 * 
 * @version V0.0 31.03.2010
 * @author Clemens Koza
 */
public class EditableCounterImpl extends AbstractGameContent implements EditableCounter {
    private Property<Integer> count;
    
    public EditableCounterImpl(Game game) {
        super(game);
        count = properties.property("count", 0);
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
