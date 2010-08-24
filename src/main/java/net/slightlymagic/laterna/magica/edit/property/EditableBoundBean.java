/**
 * EditableBoundBean.java
 * 
 * Created on 24.08.2010
 */

package net.slightlymagic.laterna.magica.edit.property;


import net.slightlymagic.beans.AbstractBoundBean;
import net.slightlymagic.beans.EventListenerList;
import net.slightlymagic.beans.properties.CompoundProperties;
import net.slightlymagic.beans.properties.Properties;
import net.slightlymagic.beans.properties.bound.BoundProperties;
import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.edit.impl.EditableListenerList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The class EditableBoundBean.
 * 
 * @version V0.0 24.08.2010
 * @author Clemens Koza
 */
public class EditableBoundBean extends AbstractBoundBean {
    protected final Logger      log = LoggerFactory.getLogger(getClass());
    
    protected EventListenerList listeners;
    protected Properties        properties;
    
    protected void init(Game game) {
        s = new EditablePropertyChangeSupport(game, this);
        properties = new CompoundProperties(new EditableProperties(game), new BoundProperties(s));
        listeners = new EditableListenerList(game);
    }
}
