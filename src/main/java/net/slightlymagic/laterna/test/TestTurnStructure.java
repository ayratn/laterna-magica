/**
 * TestTurnStructure.java
 * 
 * Created on 24.03.2010
 */

package net.slightlymagic.laterna.test;


import static net.slightlymagic.laterna.magica.deck.DeckType.*;

import java.util.UUID;

import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.LaternaMagica;
import net.slightlymagic.laterna.magica.cards.AllCards;
import net.slightlymagic.laterna.magica.deck.Deck;
import net.slightlymagic.laterna.magica.impl.GameImpl;
import net.slightlymagic.laterna.magica.player.Player;
import net.slightlymagic.laterna.magica.player.impl.PlayerImpl;
import net.slightlymagic.objectTransactions.History;
import net.slightlymagic.objectTransactions.modifications.Creation;


/**
 * The class TestTurnStructure.
 * 
 * @version V0.0 24.03.2010
 * @author Clemens Koza
 */
public class TestTurnStructure {
    public static void main(String[] args) throws Exception {
        LaternaMagica.init();
        
        AllCards c = new AllCards();
        c.load();
        
        Deck d = new Deck();
        d.addPool(MAIN_DECK);
        
        d.getPool(MAIN_DECK).put(c.getCard("Plains").getPrintings().get(0), 9);
        
        History h = History.createHistory(UUID.randomUUID());
        h.pushHistoryForThread();
        try {
            final Game g = Creation.createObject(new GameImpl()).init();
            
            String[] names = {"Clemens", "Berni", "Roman"};
            for(String name:names) {
                Player p = new PlayerImpl(g, name);
                g.getPlayers().add(p);
                p.setDeck(d);
            }
            
            g.getTurnStructure().takeExtraTurn(g.getPlayers().get(2));
            
            g.startGame();
            
            while(g.getTurnStructure().getTurnNumber() < 3) {
                System.out.printf("%2d: %-10s %-15s %-20s %-10s%n", g.getTurnStructure().getTurnNumber(),
                        g.getTurnStructure().getActivePlayer(), g.getPhaseStructure().getPhase(),
                        g.getPhaseStructure().getStep(), g.getPhaseStructure().getPriorPlayer());
                g.getPhaseStructure().takeAction(false);
            }
            
//            System.out.println(g.getGameState());
        } finally {
            h.popHistoryForThread();
        }
    }
}
