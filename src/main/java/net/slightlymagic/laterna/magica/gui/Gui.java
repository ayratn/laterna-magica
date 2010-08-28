/**
 * Gui.java
 * 
 * Created on 27.08.2010
 */

package net.slightlymagic.laterna.magica.gui;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.characteristic.CharacteristicSnapshot;
import net.slightlymagic.laterna.magica.gui.actor.GuiMagicActor;
import net.slightlymagic.laterna.magica.gui.card.CardDisplay;
import net.slightlymagic.laterna.magica.gui.card.CardPanel;
import net.slightlymagic.laterna.magica.gui.card.CardTextButton;
import net.slightlymagic.laterna.magica.gui.combat.LegalCombatantUpdater;
import net.slightlymagic.laterna.magica.gui.player.PlayerPanel;
import net.slightlymagic.laterna.magica.gui.player.PlayerPanelImpl;
import net.slightlymagic.laterna.magica.gui.zone.ZonePanel;
import net.slightlymagic.laterna.magica.gui.zone.ZonePanelImpl;
import net.slightlymagic.laterna.magica.gui.zone.ZoneSizePanelImpl;
import net.slightlymagic.laterna.magica.player.Player;
import net.slightlymagic.laterna.magica.zone.Zone;
import net.slightlymagic.laterna.magica.zone.Zone.Zones;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;


/**
 * The class Gui. The Gui class saves all the state of a game related to its user interface.
 * 
 * @version V0.0 27.08.2010
 * @author Clemens Koza
 */
public class Gui {
    private final Game                        game;
    private final List<GuiMagicActor>         actors  = new ArrayList<GuiMagicActor>();
    private final List<CardDisplay>           cards   = new ArrayList<CardDisplay>();
    private final Map<Player, PlayerPanel>    players = new MapMaker().makeComputingMap(new Function<Player, PlayerPanel>() {
                                                          @Override
                                                          public PlayerPanel apply(Player from) {
                                                              return new PlayerPanelImpl(Gui.this, from);
                                                          }
                                                      });
    private final Map<PlayerZones, ZonePanel> zones   = new MapMaker().makeComputingMap(new Function<PlayerZones, ZonePanel>() {
                                                          public ZonePanel apply(PlayerZones from) {
                                                              switch(from.zones) {
                                                                  case ANTE:
                                                                  case EXILE:
                                                                  case GRAVEYARD:
                                                                  case LIBRARY:
                                                                      return new ZoneSizePanelImpl(Gui.this,
                                                                              from.zone);
                                                                  case HAND:
                                                                  case STACK:
                                                                      return new ZonePanelImpl(Gui.this, from.zone);
                                                                  case COMMAND:
                                                                  case BATTLEFIELD:
                                                                      return new ZonePanelImpl(Gui.this,
                                                                              from.zone, from.player);
                                                                  default:
                                                                      throw new AssertionError(from.zones);
                                                              }
                                                          }
                                                      });
    
    public Gui(Game game) {
        this.game = game;
        new LegalCombatantUpdater(this);
    }
    
    public Game getGame() {
        return game;
    }
    
    public PlayerPanel getPlayerPanel(Player player) {
        return players.get(player);
    }
    
    public ZonePanel getZonePanel(Player player, Zones zone) {
        return zones.get(new PlayerZones(player, zone));
    }
    
    public ZonePanel getZonePanel(Zones zone) {
        return zones.get(new PlayerZones(game, zone));
    }
    
    /**
     * Adds a GuiMagicActor that wants to be notified on events happening in this Gui.
     * 
     * @param actor
     */
    public void add(GuiMagicActor actor) {
        actors.add(actor);
    }
    
    public void remove(GuiMagicActor actor) {
        actors.remove(actor);
    }
    
    /**
     * Adds a CardDisplay that wants to be notified whenever a card is selected in this Gui.
     */
    public void add(CardDisplay card) {
        cards.add(card);
    }
    
    public void remove(CardDisplay card) {
        cards.remove(card);
    }
    
    public void publishPassPriority() {
        for(GuiMagicActor actor:actors)
            actor.channels.passPriority.publish(null);
    }
    
    /**
     * Returns a component for displaying the specified card.
     */
    public CardPanel createCardPanel(MagicObject card) {
        CharacteristicSnapshot s = card.getCharacteristics().get(0).getCharacteristics(
                new CharacteristicSnapshot());
        CardTextButton p = new CardTextButton(s);
        p.addActionListener(cardListener);
        p.addMouseListener(cardMouseListener);
        return p;
    }
    
    private final ActionListener cardListener      = new CardListener();
    private final MouseListener  cardMouseListener = new CardMouseListener();
    
    private final class CardListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            CardTextButton p = (CardTextButton) e.getSource();
            MagicObject c = p.getCard().getCard();
            
            for(GuiMagicActor actor:actors)
                actor.channels.objects.publish(c);
        }
    }
    
    private final class CardMouseListener extends MouseAdapter {
        @Override
        public void mouseEntered(MouseEvent e) {
            if(!(e.getSource() instanceof CardDisplay)) return;
            CharacteristicSnapshot sn = ((CardDisplay) e.getSource()).getCard();
            for(CardDisplay c:cards)
                c.setCard(sn);
        }
        
        @Override
        public void mouseExited(MouseEvent e) {
            if(!(e.getSource() instanceof CardDisplay)) return;
//            for(CardDisplay c:cards)
//                c.setCard(null);
        }
    }
    
    private static class PlayerZones {
        private final Player player;
        private final Zones  zones;
        private final Zone   zone;
        
        public PlayerZones(Player player, Zones zones) {
            this.player = player;
            this.zones = zones;
            zone = player.getZone(zones);
        }
        
        public PlayerZones(Game game, Zones zones) {
            this.player = null;
            this.zones = zones;
            zone = game.getZone(zones);
        }
        
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((player == null)? 0:player.hashCode());
            result = prime * result + ((zones == null)? 0:zones.hashCode());
            return result;
        }
        
        @Override
        public boolean equals(Object obj) {
            if(this == obj) return true;
            if(obj == null) return false;
            if(getClass() != obj.getClass()) return false;
            PlayerZones other = (PlayerZones) obj;
            if(player == null) {
                if(other.player != null) return false;
            } else if(!player.equals(other.player)) return false;
            if(zones != other.zones) return false;
            return true;
        }
    }
}