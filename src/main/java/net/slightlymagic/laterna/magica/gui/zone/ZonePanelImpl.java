/**
 * ZonePanelImpl.java
 * 
 * Created on 27.08.2010
 */

package net.slightlymagic.laterna.magica.gui.zone;


import static java.lang.String.*;
import static java.util.Collections.*;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.gui.DisposeSupport;
import net.slightlymagic.laterna.magica.gui.Gui;
import net.slightlymagic.laterna.magica.gui.card.CardPanel;
import net.slightlymagic.laterna.magica.player.Player;
import net.slightlymagic.laterna.magica.util.MagicaUtils;
import net.slightlymagic.laterna.magica.zone.Zone;
import net.slightlymagic.laterna.magica.zone.Zone.Zones;

import org.jdesktop.swingx.JXCollapsiblePane;
import org.jdesktop.swingx.JXCollapsiblePane.Direction;
import org.jetlang.core.Disposable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The class ZonePanelImpl.
 * 
 * @version V0.0 27.08.2010
 * @author Clemens Koza
 */
public class ZonePanelImpl extends ZonePanel implements ZoneCardsPanel {
    private static final long      serialVersionUID = -7801923077230254195L;
    
    private static final Logger    log              = LoggerFactory.getLogger(ZonePanelImpl.class);
    
    protected final DisposeSupport d                = new DisposeSupport();
    
    //if not null, only consider cards for which the player is "you"
    private Player                 you;
    private Map<MagicObject, CardPanel> cards, cardsView;
    
    protected Container                 pane;
    
    private MoveCard                    l;
    
    public ZonePanelImpl(Gui gui, Zone zone) {
        this(gui, zone, null);
    }
    
    public ZonePanelImpl(Gui gui, Zone zone, Player you) {
        super(gui, zone);
        setLayout(new BorderLayout());
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
            d.add(new CollapseListener(pane));
        }
        

        cards = new HashMap<MagicObject, CardPanel>();
        cardsView = unmodifiableMap(cards);
        d.add(l = new MoveCard());
        for(MagicObject o:zone.getCards()) {
            addCard(o);
        }
    }
    
    public void dispose() {
        d.dispose();
    }
    
    public Map<MagicObject, CardPanel> getShownCards() {
        return cardsView;
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
            l.add(card);
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
            l.remove(card);
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
        CardPanel c = getGui().createCardPanel(card);
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
    
    private final class CollapseListener implements PropertyChangeListener, Disposable {
        private final JXCollapsiblePane pane;
        
        private CollapseListener(JXCollapsiblePane pane) {
            this.pane = pane;
            getZone().addPropertyChangeListener("cards", this);
        }
        
        @Override
        public void dispose() {
            getZone().removePropertyChangeListener("cards", this);
        }
        
        public void propertyChange(PropertyChangeEvent evt) {
            pane.setCollapsed(getZone().isEmpty());
        }
    }
    
    private final class MoveCard implements PropertyChangeListener, Disposable {
        private List<MagicObject> l = new ArrayList<MagicObject>();
        
        public MoveCard() {
            getZone().addPropertyChangeListener("cards", this);
        }
        
        public void add(MagicObject card) {
            l.add(card);
            card.addPropertyChangeListener(MagicObject.CONTROLLER, this);
        }
        
        public void remove(MagicObject card) {
            l.remove(card);
            card.removePropertyChangeListener(MagicObject.CONTROLLER, this);
        }
        
        public void dispose() {
            getZone().removePropertyChangeListener("cards", this);
            for(MagicObject card:l)
                card.removePropertyChangeListener(MagicObject.CONTROLLER, this);
            l.clear();
        }
        
        public void propertyChange(PropertyChangeEvent evt) {
            if(evt.getSource() == getZone() && Zone.CARDS.equals(evt.getPropertyName())) {
                log.debug(getZone()
                        + " Panel: "
                        + format("[source=%s, old=%s, new=%s]", evt.getSource(), evt.getOldValue(),
                                evt.getNewValue()));
                log.trace(null, new Exception());
                if("cards".equals(evt.getPropertyName())) {
                    if(evt.getSource() != getZone()) throw new AssertionError();
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
                        for(MagicObject c:getZone().getCards()) {
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
                    for(MagicObject c:getZone().getCards()) {
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
