/**
 * GameInitializer.java
 * 
 * Created on 04.04.2010
 */

package net.slightlymagic.laterna.magica.impl;


import java.util.Map;
import java.util.Map.Entry;

import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.card.CardObject;
import net.slightlymagic.laterna.magica.card.Printing;
import net.slightlymagic.laterna.magica.card.impl.CardObjectImpl;
import net.slightlymagic.laterna.magica.deck.Deck.DeckType;
import net.slightlymagic.laterna.magica.event.ActiveChangedListener;
import net.slightlymagic.laterna.magica.event.GameStartListener;
import net.slightlymagic.laterna.magica.player.Player;
import net.slightlymagic.laterna.magica.zone.Zone;
import net.slightlymagic.laterna.magica.zone.Zone.Zones;


/**
 * The class GameInitializer.
 * 
 * @version V0.0 04.04.2010
 * @author Clemens Koza
 */
public class GameInitializer implements GameStartListener.Internal {
    public void gameStarted(final Game game) {
        game.getTurnStructure().addActiveChangedListener(new ActiveChangedListener.Internal() {
            public void nextActive(Player oldActive, Player newActive) {
                //reset land drops at the end of each turn
                for(Player p:game.getPlayers())
                    p.getCounter(Player.LAND_DROP_COUNTER).reset();
                //clear summoning sickness
                for(MagicObject c:game.getBattlefield().getCards())
                    c.getCounter("summoningSickness").reset();
            }
        });
        
        for(Player p:game.getPlayers()) {
            //create the library from the deck
            //TODO allow sideboarding
            //TODO move cards for other game types
            move(p, DeckType.MAIN_DECK, Zones.LIBRARY);
            //shuffle the library
            p.getLibrary().shuffle();
            
            //set life points
            //TODO respect vanguards, game variants
            //maybe just do that by afterwards gaining/losing life
            p.getLifeTotal().setLifeTotal(20);
            
            //draw starting hands
            //TODO respect vanguards, game variants
            p.drawCards(7);
        }
        
        //start the first turn
        game.getPhaseStructure().takeAction(false);
    }
    
    /**
     * Creates individual card objects from the templates in the deck and puts them into the specified zone.
     */
    private void move(Player player, DeckType deck, Zones zone) {
        Map<Printing, Integer> src = player.getDeck().getPool(deck);
        if(src == null) return;
        Zone dst = player.getZone(zone);
        for(Entry<Printing, Integer> e:src.entrySet())
            for(int i = 0; i < e.getValue(); i++) {
                CardObject o = new CardObjectImpl(player.getGame(), e.getKey());
                o.setOwner(player);
                o.setZone(dst);
            }
    }
}
