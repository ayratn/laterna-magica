/**
 * AbstractEditableBean.java
 * 
 * Created on 24.08.2010
 */

package net.slightlymagic.laterna.magica.edit.property;


import net.slightlymagic.beans.properties.CompoundProperties;
import net.slightlymagic.beans.properties.bound.BoundProperties;
import net.slightlymagic.laterna.AbstractLaternaBean;
import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.edit.impl.EditableListenerList;


/**
 * The class AbstractEditableBean.
 * 
 * @version V0.0 24.08.2010
 * @author Clemens Koza
 */
public class AbstractEditableBean extends AbstractLaternaBean {
    protected void init(Game game) {
        s = new EditablePropertyChangeSupport(game, this);
        properties = new CompoundProperties(new EditableProperties(game), new BoundProperties(s));
        listeners = new EditableListenerList(game);
    }
}
