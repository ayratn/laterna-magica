/**
 * AbstractLaternaBean.java
 * 
 * Created on 25.08.2010
 */

package net.slightlymagic.laterna;


import net.slightlymagic.beans.AbstractBoundBean;
import net.slightlymagic.beans.EventListenerList;
import net.slightlymagic.beans.properties.Properties;
import net.slightlymagic.beans.properties.bound.BoundProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The class AbstractLaternaBean.
 * 
 * @version V0.0 25.08.2010
 * @author Clemens Koza
 */
public class AbstractLaternaBean extends AbstractBoundBean {
    protected final Logger      log        = LoggerFactory.getLogger(getClass());
    
    protected Properties        properties = new BoundProperties(s);
    protected EventListenerList listeners  = new EventListenerList();
}
