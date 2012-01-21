/**
 * TestZone.java
 * 
 * Created on 30.03.2010
 */

package net.slightlymagic.laterna.test;


import java.util.UUID;

import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.LaternaMagica;
import net.slightlymagic.laterna.magica.card.Card;
import net.slightlymagic.laterna.magica.card.CardObject;
import net.slightlymagic.laterna.magica.card.impl.CardObjectImpl;
import net.slightlymagic.laterna.magica.impl.GameImpl;
import net.slightlymagic.laterna.magica.player.Player;
import net.slightlymagic.laterna.magica.player.impl.PlayerImpl;
import net.slightlymagic.objectTransactions.History;
import net.slightlymagic.objectTransactions.modifications.Creation;


/**
 * The class TestZone.
 * 
 * @version V0.0 30.03.2010
 * @author Clemens Koza
 */
public class TestZone {
    public static void main(String[] args) throws Exception {
        LaternaMagica.init();
        
        History h = History.createHistory(UUID.randomUUID());
        h.pushHistoryForThread();
        try {
            final Game g = Creation.createObject(new GameImpl()).init();
            Player p = new PlayerImpl(g, "Clemens");
            g.getPlayers().add(p);
            CardObject c = new CardObjectImpl(new Card());
            c.setOwner(p);
            
            System.out.println(p.getLibrary().getCards());
            System.out.println(g.getBattlefield().getCards());
            c.setZone(p.getLibrary());
            System.out.println(p.getLibrary().getCards());
            System.out.println(g.getBattlefield().getCards());
            c.setZone(g.getBattlefield());
            System.out.println(p.getLibrary().getCards());
            System.out.println(g.getBattlefield().getCards());
            
//            System.out.println(g.getGameState());
        } finally {
            h.popHistoryForThread();
        }
    }
}
