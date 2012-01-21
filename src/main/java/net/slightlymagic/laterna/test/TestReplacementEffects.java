/**
 * TestReplacementEffects.java
 * 
 * Created on 24.03.2010
 */

package net.slightlymagic.laterna.test;


import java.util.UUID;

import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.effect.replacement.ReplaceableEvent;
import net.slightlymagic.laterna.magica.effect.replacement.ReplacementEffect.ReplacementType;
import net.slightlymagic.laterna.magica.effect.replacement.impl.AbstractReplacementEffect;
import net.slightlymagic.laterna.magica.impl.GameImpl;
import net.slightlymagic.laterna.magica.player.Player;
import net.slightlymagic.laterna.magica.player.impl.LifeEvent;
import net.slightlymagic.laterna.magica.player.impl.PlayerImpl;
import net.slightlymagic.objectTransactions.History;
import net.slightlymagic.objectTransactions.modifications.Creation;


/**
 * The class TestReplacementEffects.
 * 
 * @version V0.0 24.03.2010
 * @author Clemens Koza
 */
public class TestReplacementEffects {
    public static void main(String[] args) {
        History h = History.createHistory(UUID.randomUUID());
        h.pushHistoryForThread();
        try {
            final Game g = Creation.createObject(new GameImpl()).init();
            Player p = new PlayerImpl(g, "Clemens");
            p.getLifeTotal().setLifeTotal(20);
            
            g.getReplacementEngine().add(
                    new AbstractReplacementEffect<LifeEvent>(LifeEvent.class, ReplacementType.OTHER) {
                        @Override
                        protected ReplaceableEvent replace0(LifeEvent e) {
                            return new LifeEvent(e.getPlayer(), e.getAmount(), !e.isGained());
                        }
                    });
            
            p.getLifeTotal().loseLife(5);
            
//            System.out.println(g.getGameState());
            System.out.println(p.getLifeTotal().getLifeTotal());
        } finally {
            h.popHistoryForThread();
        }
    }
}
