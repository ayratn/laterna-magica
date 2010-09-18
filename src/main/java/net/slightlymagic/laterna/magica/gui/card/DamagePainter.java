/**
 * DamagePainter.java
 * 
 * Created on 11.09.2010
 */

package net.slightlymagic.laterna.magica.gui.card;


import static com.google.common.base.Suppliers.*;
import static net.slightlymagic.laterna.magica.util.MagicaPredicates.*;
import static net.slightlymagic.laterna.magica.zone.Zone.Zones.*;

import java.awt.Graphics2D;

import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.characteristic.CharacteristicSnapshot;
import net.slightlymagic.laterna.magica.characteristics.CardType;

import org.jdesktop.swingx.painter.Painter;

import com.google.common.base.Predicate;


/**
 * The class DamagePainter.
 * 
 * @version V0.0 11.09.2010
 * @author Clemens Koza
 */
public class DamagePainter implements Painter<CharacteristicSnapshot> {
    private static final Predicate<MagicObject> isOnBattlefield = isIn(ofInstance(BATTLEFIELD));
    
    @Override
    public void paint(Graphics2D g, CharacteristicSnapshot object, int width, int height) {
        if(object == null) return;
        MagicObject c = object.getCard();
        if(c == null) return;
        if(!isOnBattlefield.apply(c)) return;
        if(!object.getTypes().hasValue(CardType.CREATURE)) return;
        
        //TODO what to draw? assigned damage (during combat), marked damage, ...?
    }
}
