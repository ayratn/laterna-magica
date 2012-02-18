/**
 * ZonePanel.java
 * 
 * Created on 07.04.2010
 */

package net.slightlymagic.laterna.magica.gui.zone;


import static java.lang.String.*;

import javax.swing.JPanel;

import net.slightlymagic.laterna.magica.gui.Gui;
import net.slightlymagic.laterna.magica.zone.Zone;

import org.jetlang.core.Disposable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The class ZonePanel.
 * 
 * @version V0.0 07.04.2010
 * @author Clemens Koza
 */
public abstract class ZonePanel extends JPanel implements Disposable {
    private static final long   serialVersionUID = 5079429702394765257L;
    
    private static final Logger log              = LoggerFactory.getLogger(ZonePanel.class);
    
    private final Gui           gui;
    private final Zone          zone;
    
    public ZonePanel(Gui gui, Zone zone) {
        this.gui = gui;
        this.zone = zone;
        log.debug(format("%s for %s created", getClass().getSimpleName(), zone));
    }
    
    public Gui getGui() {
        return gui;
    }
    
    public Zone getZone() {
        return zone;
    }
}
