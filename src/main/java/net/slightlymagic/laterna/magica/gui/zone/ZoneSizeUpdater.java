/**
 * ZoneSizeUpdater.java
 * 
 * Created on 08.04.2010
 */

package net.slightlymagic.laterna.magica.gui.zone;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JLabel;

import net.slightlymagic.laterna.magica.zone.Zone;


/**
 * The class ZoneSizeUpdater.
 * 
 * @version V0.0 08.04.2010
 * @author Clemens Koza
 */
public class ZoneSizeUpdater implements PropertyChangeListener {
    private JLabel l;
    private Zone   z;
    
    public ZoneSizeUpdater(JLabel l, Zone z) {
        this.l = l;
        this.z = z;
        l.setText("" + z.size());
        z.addPropertyChangeListener(Zone.CARDS, this);
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getSource() != z) return;
        l.setText("" + z.size());
        l.repaint();
    }
}
