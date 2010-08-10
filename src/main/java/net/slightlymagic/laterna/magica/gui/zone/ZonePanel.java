/**
 * ZonePanel.java
 * 
 * Created on 07.04.2010
 */

package net.slightlymagic.laterna.magica.gui.zone;


import static java.lang.String.*;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.gui.util.GuiUtil;
import net.slightlymagic.laterna.magica.player.Player;
import net.slightlymagic.laterna.magica.util.MagicaUtils;
import net.slightlymagic.laterna.magica.zone.Zone;
import net.slightlymagic.laterna.magica.zone.Zone.Zones;

import org.jdesktop.swingx.JXCollapsiblePane;
import org.jdesktop.swingx.JXCollapsiblePane.Direction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The class ZonePanel.
 * 
 * @version V0.0 07.04.2010
 * @author Clemens Koza
 */
public class ZonePanel extends JPanel {
    private static final Logger          log              = LoggerFactory.getLogger(ZonePanel.class);
    
    private static final long            serialVersionUID = 5079429702394765257L;
    
    private Zone                         zone;
    //if not null, only consider cards for which the player is "you"
    private Player                       you;
    private Map<MagicObject, JComponent> cards;
    
    protected Container                  pane;
    
    private MoveCard                     l;
    
    public ZonePanel(Zone zone) {
        this(zone, null);
    }
    
    public ZonePanel(Zone zone, Player you) {
        super(new BorderLayout());
        this.zone = zone;
        this.you = you;
        
        //specifying a "you" is only meaningful for shared zones; otherwise, only a player's own cards can be in it
        assert you == null || zone.getOwner() == null;
        
        if(zone.getType() != Zones.STACK) {
            pane = new JPanel(new FlowLayout(FlowLayout.LEADING, 5, 5));
            add(pane);
        } else {
            final JXCollapsiblePane pane = new JXCollapsiblePane(Direction.RIGHT);
            pane.setPreferredSize(new Dimension(100, 0));
            pane.setCollapsed(true);
            pane.setBorder(BorderFactory.createTitledBorder("Stack"));
            add(pane);
            this.pane = pane.getContentPane();
            this.pane.setLayout(new BoxLayout(this.pane, BoxLayout.Y_AXIS));
            //automatically expand/collapse the pane
            zone.addPropertyChangeListener("cards", new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    pane.setCollapsed(ZonePanel.this.zone.isEmpty());
                }
            });
        }
        

        cards = new HashMap<MagicObject, JComponent>();
        zone.addPropertyChangeListener("cards", l = new MoveCard());
        for(MagicObject o:zone.getCards()) {
            addCard(o);
        }
    }
    
    protected boolean shouldShowCard(MagicObject card) {
        return you == null || MagicaUtils.you(card) == you;
    }
    
    protected void addCard(MagicObject card) {
        addCard(card, pane.getComponentCount());
    }
    
    /**
     * Called when a card is added to the zone. If you is not null, a controller change listener is added to the
     * card. If the card is controlled by that player, or you is null, the card is
     * {@link #showCard(MagicObject, int) shown}.
     */
    protected void addCard(MagicObject card, int index) {
        if(you != null) {
            card.addPropertyChangeListener(MagicObject.CONTROLLER, l);
        }
        //this means the card should not be visible
        if(!shouldShowCard(card)) return;
        showCard(card, index);
    }
    
    /**
     * Called when a card is removed from the zone. If you is not null, the controller change listener is removed
     * from the card. If the card is controlled by that player, or you is null, the card is
     * {@link #hideCard(MagicObject) hidden}.
     */
    protected void removeCard(MagicObject card) {
        if(you != null) {
            card.removePropertyChangeListener(MagicObject.CONTROLLER, l);
        }
        //this means the card should not be visible
        if(!shouldShowCard(card)) return;
        hideCard(card);
    }
    
    /**
     * Adds the card to the panel. This method is called when the card enters the zone, or when the player gets
     * control over it.
     */
    protected void showCard(MagicObject card, int index) {
        JComponent c = GuiUtil.createComponent(card);
        cards.put(card, c);
        pane.add(c, index);
    }
    
    /**
     * Removes the card from the panel. This method is called when the card leaves the zone, or when the player
     * loses control over it.
     */
    protected void hideCard(MagicObject card) {
        JComponent c = cards.remove(card);
        assert c != null;
        pane.remove(c);
    }
    
    private class MoveCard implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            if(evt.getSource() == zone && Zone.CARDS.equals(evt.getPropertyName())) {
                log.debug(zone
                        + " Panel: "
                        + format("[source=%s, old=%s, new=%s]", evt.getSource(), evt.getOldValue(),
                                evt.getNewValue()));
                log.trace(null, new Exception());
                if("cards".equals(evt.getPropertyName())) {
                    if(evt.getSource() != zone) throw new AssertionError();
                    if(!(evt instanceof IndexedPropertyChangeEvent)) throw new AssertionError();
                    
                    MagicObject oldValue = (MagicObject) evt.getOldValue();
                    MagicObject newValue = (MagicObject) evt.getNewValue();
                    
                    if(oldValue != null) {
                        log.debug("remove");
                        removeCard(oldValue);
                    }
                    if(newValue != null) {
                        log.debug("add");
                        int i = 0;
                        //count the number of cards before the new one
                        for(MagicObject c:zone.getCards()) {
                            if(c == newValue) break;
                            if(shouldShowCard(c)) i++;
                        }
                        //add the card with the actual index
                        addCard(newValue, i);
                    }
                    
                    validate();
                    repaint();
                } else throw new AssertionError();
            } else if(evt.getSource() instanceof MagicObject
                    && MagicObject.CONTROLLER.equals(evt.getPropertyName())) {
                MagicObject card = (MagicObject) evt.getSource();
                if(shouldShowCard(card)) {
                    int i = 0;
                    //count the number of cards before the new one
                    for(MagicObject c:zone.getCards()) {
                        if(c == card) break;
                        if(shouldShowCard(c)) i++;
                    }
                    //add the card with the actual index
                    showCard(card, i);
                } else {
                    hideCard(card);
                }
            } else throw new AssertionError();
        }
    }
}
