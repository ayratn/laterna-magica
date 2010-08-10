/**
 * LifeTotalUpdater.java
 * 
 * Created on 08.04.2010
 */

package net.slightlymagic.laterna.magica.gui.player;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JLabel;

import net.slightlymagic.laterna.magica.player.LifeTotal;


/**
 * The class LifeTotalUpdater.
 * 
 * @version V0.0 08.04.2010
 * @author Clemens Koza
 */
public class LifeTotalUpdater implements PropertyChangeListener {
    private JLabel    label;
    private LifeTotal life;
    
    public LifeTotalUpdater(JLabel label, LifeTotal life) {
        this.label = label;
        this.life = life;
        lifeUpdated();
    }
    
    private void lifeUpdated() {
        label.setText("" + life.getLifeTotal());
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getSource() == life && LifeTotal.LIFE_TOTAL.equals(evt.getPropertyName())) lifeUpdated();
    }
}
