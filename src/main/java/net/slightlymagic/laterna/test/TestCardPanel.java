/**
 * TestTurnStructure.java
 * 
 * Created on 24.03.2010
 */

package net.slightlymagic.laterna.test;


import static java.lang.String.*;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.LaternaMagica;
import net.slightlymagic.laterna.magica.card.Printing;
import net.slightlymagic.laterna.magica.deck.Deck;
import net.slightlymagic.laterna.magica.deck.Deck.DeckType;
import net.slightlymagic.laterna.magica.deck.impl.DeckImpl;
import net.slightlymagic.laterna.magica.event.PriorChangedListener;
import net.slightlymagic.laterna.magica.gui.actor.GuiMagicActor;
import net.slightlymagic.laterna.magica.gui.card.CardDetail;
import net.slightlymagic.laterna.magica.gui.card.CardImage;
import net.slightlymagic.laterna.magica.gui.player.PlayerPanel;
import net.slightlymagic.laterna.magica.gui.util.GuiUtil;
import net.slightlymagic.laterna.magica.gui.zone.ZonePanel;
import net.slightlymagic.laterna.magica.impl.GameImpl;
import net.slightlymagic.laterna.magica.impl.GameLoop;
import net.slightlymagic.laterna.magica.player.MagicActor;
import net.slightlymagic.laterna.magica.player.Player;
import net.slightlymagic.laterna.magica.player.impl.PlayerImpl;

import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.MultiSplitLayout;
import org.jdesktop.swingx.MultiSplitLayout.Node;


/**
 * The class TestTurnStructure.
 * 
 * @version V0.0 24.03.2010
 * @author Clemens Koza
 */
public class TestCardPanel {
    public static void main(String[] args) throws Exception {
        LaternaMagica.init();
        
        final Game g = initGame();
        Player me = g.getPlayers().get(0);
        me.setActor(new GuiMagicActor(me));
        Player her = g.getPlayers().get(1);
        her.setActor(new GuiMagicActor(her));
        
        JFrame jf = new JFrame();
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JXMultiSplitPane p = new JXMultiSplitPane(new MultiSplitLayout(createLayout()));
        for(int i = 0; i < g.getPlayers().size(); i++) {
            Player pl = g.getPlayers().get(i);
            p.add(new PlayerPanel(pl), "player" + i);
            
            p.add(new ZonePanel(pl.getHand()), "hand" + i);
            p.add(new ZonePanel(g.getBattlefield(), pl), "play" + i);
        }
        
        ZonePanel z = new ZonePanel(g.getStack());
        p.add(z, "stack");
        
        CardImage im = new CardImage();
        GuiUtil.add(im);
        p.add(im, "picture");
        
        CardDetail de = new CardDetail(18);
        GuiUtil.add(de);
        p.add(de, "detail");
        
        final JLabel l = new JLabel();
        g.getPhaseStructure().addPriorChangedListener(new PriorChangedListener() {
            public void nextPrior(Player oldPrior, Player newPrior) {
                l.setText(format("%s's %s - %s has priority", g.getTurnStructure().getActivePlayer(),
                        g.getPhaseStructure().getStep(), g.getPhaseStructure().getPriorPlayer()));
            }
        });
        
        jf.add(l, BorderLayout.NORTH);
        jf.add(p);
        jf.add(new JButton(new AbstractAction("Pass priority") {
            private static final long serialVersionUID = -8679358973135402669L;
            
            public void actionPerformed(ActionEvent e) {
                MagicActor actor = g.getPhaseStructure().getPriorPlayer().getActor();
                if(!(actor instanceof GuiMagicActor)) return;
                ((GuiMagicActor) actor).channels.passPriority.publish(null);
            }
        }), BorderLayout.SOUTH);
        
//        jf.pack();
        jf.setSize(700, 300);
        jf.setVisible(true);
        
        g.startGame();
        
        //run in the main thread
        new GameLoop(g).run();
    }
    
    private static Node createLayout() {
        return MultiSplitLayout.parseModel("                    "
                + "(ROW                                         "
                + "  (COLUMN (LEAF name=player1 weight=0.5)     "
                + "          (LEAF name=player0 weight=0.5)     "
                + " )(COLUMN (LEAF name=hand1   weight=0.25)    "
                + "          (LEAF name=play1   weight=0.25)    "
                + "          (LEAF name=play0   weight=0.25)    "
                + "          (LEAF name=hand0   weight=0.25)    "
                + "          weight=0.7                         "
                + " )        (LEAF name=stack   weight=0)       "
                + "  (COLUMN (LEAF name=picture weight=0.5)     "
                + "          (LEAF name=detail  weight=0.5)     "
                + "          weight=0.3                         "
                + " )                                           "
                + ")                                            ");
    }
    
    private static Game initGame() throws IOException {
//        AllCards c = LaternaMagica.CARDS();
//        c.compile();
        
        Deck d = new DeckImpl();
        d.addPool(DeckType.MAIN_DECK);
        
        put(d, "Island", 20);
        put(d, "Courier's Capsule", 12);
        put(d, "Arcanis the Omnipotent", 8);
        

        final Game g = new GameImpl();
        
        String[] names = {"Freak", "Foxal"};
        for(String name:names) {
            Player p = new PlayerImpl(g, name);
            p.setDeck(d);
            g.getPlayers().add(p);
        }
        
        return g;
    }
    
    private static void put(Deck d, String name, int num) {
        Map<Printing, Integer> pool = d.getPool(DeckType.MAIN_DECK);
        List<Printing> list = LaternaMagica.CARDS().getCard(name).getPrintings();
        for(int i = 0; i < num; i++)
            put(pool, list);
    }
    
    /**
     * Randomly adds one of the printings to the pool
     */
    private static void put(Map<Printing, Integer> pool, List<Printing> list) {
        Printing p = list.get((int) (Math.random() * list.size()));
        Integer old = pool.put(p, 1);
        //If there already were cards, add 1
        if(old != null) pool.put(p, old + 1);
    }
}
