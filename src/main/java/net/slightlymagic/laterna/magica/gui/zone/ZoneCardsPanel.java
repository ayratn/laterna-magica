/**
 * ZoneCardsPanel.java
 * 
 * Created on 31.08.2010
 */

package net.slightlymagic.laterna.magica.gui.zone;


import java.util.Map;

import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.gui.card.CardPanel;

import org.jetlang.core.Disposable;


/**
 * The class ZoneCardsPanel.
 * 
 * @version V0.0 31.08.2010
 * @author Clemens Koza
 */
public interface ZoneCardsPanel extends Disposable {
    
    public Map<MagicObject, CardPanel> getShownCards();
    
}
