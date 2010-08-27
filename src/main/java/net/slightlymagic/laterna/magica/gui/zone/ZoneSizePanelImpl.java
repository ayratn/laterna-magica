/**
 * ZoneSizePanelImpl.java
 * 
 * Created on 27.08.2010
 */

package net.slightlymagic.laterna.magica.gui.zone;


import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import net.slightlymagic.laterna.magica.gui.Gui;
import net.slightlymagic.laterna.magica.zone.Zone;


/**
 * The class ZoneSizePanelImpl.
 * 
 * @version V0.0 27.08.2010
 * @author Clemens Koza
 */
public class ZoneSizePanelImpl extends ZonePanel {
    private static final long serialVersionUID = -9106502580826879806L;
    
    public ZoneSizePanelImpl(Gui gui, Zone zone) {
        super(gui, zone);
        setLayout(new BorderLayout());
        JLabel size = new JLabel("", SwingConstants.TRAILING);
        new ZoneSizeUpdater(size, zone);
        
        add(size);
    }
}
