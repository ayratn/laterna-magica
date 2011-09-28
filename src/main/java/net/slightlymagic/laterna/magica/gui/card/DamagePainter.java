/**
 * DamagePainter.java
 * 
 * Created on 11.09.2010
 */

package net.slightlymagic.laterna.magica.gui.card;


import static com.google.common.base.Suppliers.*;
import static java.lang.String.*;
import static net.slightlymagic.laterna.magica.characteristic.CardType.*;
import static net.slightlymagic.laterna.magica.util.MagicaPredicates.*;
import static net.slightlymagic.laterna.magica.zone.Zone.Zones.*;

import java.awt.FontMetrics;
import java.awt.Graphics2D;

import net.slightlymagic.laterna.magica.Combat;
import net.slightlymagic.laterna.magica.Combat.AttackAssignment;
import net.slightlymagic.laterna.magica.Combat.Attacker;
import net.slightlymagic.laterna.magica.Combat.BlockAssignment;
import net.slightlymagic.laterna.magica.Combat.Blocker;
import net.slightlymagic.laterna.magica.Combat.Defender;
import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.card.CardObject;
import net.slightlymagic.laterna.magica.characteristic.CardSnapshot;
import net.slightlymagic.laterna.magica.characteristic.impl.CardCharacteristicsSnapshot;

import org.jdesktop.swingx.painter.Painter;

import com.google.common.base.Predicate;


/**
 * The class DamagePainter.
 * 
 * @version V0.0 11.09.2010
 * @author Clemens Koza
 */
public class DamagePainter implements Painter<CardSnapshot> {
    private static final Predicate<MagicObject> isOnBattlefield = isIn(ofInstance(BATTLEFIELD));
    
    @Override
    public void paint(Graphics2D g, CardSnapshot object, int width, int height) {
        if(object == null) return;
        if(!(object instanceof CardCharacteristicsSnapshot)) return;
        MagicObject o = ((CardCharacteristicsSnapshot) object).getCardObject();
        if(!(o instanceof CardObject)) return;
        CardObject c = (CardObject) o;
        
        if(!isOnBattlefield.apply(c)) return;
        if(!object.getTypes().hasValue(CREATURE)) return;
        
        if(true) return;
        //TODO this code seems to cause a NullPointerException
        //in later code because of the Edits which are implicitly executed.
        
        int marked = c.getMarkedDamage();
        int assigned = 0;
        if(c.getGame().getCombat() != null) {
            Combat combat = c.getGame().getCombat();
            try {
                Attacker a = combat.getAttacker(c);
                if(a != null) for(BlockAssignment b:a.getBlockers().values())
                    assigned += b.getBlockerAssignedDamage();
            } catch(IllegalArgumentException ex) {}
            try {
                Blocker b = combat.getBlocker(c);
                if(b != null) for(BlockAssignment a:b.getAttackers().values())
                    assigned += a.getAttackerAssignedDamage();
            } catch(IllegalArgumentException ex) {}
            try {
                Defender d = combat.getDefender(c);
                if(d != null) for(AttackAssignment a:d.getAttackers().values())
                    assigned += a.getAttackerAssignedDamage();
            } catch(IllegalArgumentException ex) {}
        }
        
        if(marked == 0 && assigned == 0) return;
        
        System.out.println("repaint");
        String toPaint = format("%d / %d", marked, marked + assigned);
        FontMetrics f = g.getFontMetrics();
        g.drawString(toPaint, width - f.stringWidth(toPaint) - 5, height - f.getDescent() - 5);
    }
}
