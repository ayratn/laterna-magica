/**
 * TestTurnStructure.java
 * 
 * Created on 24.03.2010
 */

package net.slightlymagic.laterna.test;


import java.awt.BorderLayout;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.LaternaMagica;
import net.slightlymagic.laterna.magica.card.Printing;
import net.slightlymagic.laterna.magica.deck.Deck;
import net.slightlymagic.laterna.magica.deck.Deck.DeckType;
import net.slightlymagic.laterna.magica.deck.impl.DeckImpl;
import net.slightlymagic.laterna.magica.gui.Gui;
import net.slightlymagic.laterna.magica.gui.TurnProgressUpdater;
import net.slightlymagic.laterna.magica.gui.actor.GuiMagicActor;
import net.slightlymagic.laterna.magica.gui.card.CardDetail;
import net.slightlymagic.laterna.magica.gui.card.CardImage;
import net.slightlymagic.laterna.magica.gui.zone.ZonePanel;
import net.slightlymagic.laterna.magica.impl.GameImpl;
import net.slightlymagic.laterna.magica.impl.GameLoop;
import net.slightlymagic.laterna.magica.player.Player;
import net.slightlymagic.laterna.magica.player.impl.PlayerImpl;
import net.slightlymagic.laterna.magica.zone.Zone.Zones;

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
        final Gui gui = new Gui(g);
        
        Player me = g.getPlayers().get(0);
        me.setActor(new GuiMagicActor(gui, me));
        Player her = g.getPlayers().get(1);
        her.setActor(new GuiMagicActor(gui, her));
        
        JFrame jf = new JFrame();
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JXMultiSplitPane p = new JXMultiSplitPane(new MultiSplitLayout(createLayout()));
        for(int i = 0; i < g.getPlayers().size(); i++) {
            Player pl = g.getPlayers().get(i);
            p.add(gui.getPlayerPanel(pl), "player" + i);
            
            p.add(gui.getZonePanel(pl, Zones.HAND), "hand" + i);
            p.add(gui.getZonePanel(pl, Zones.BATTLEFIELD), "play" + i);
        }
        
        ZonePanel z = gui.getZonePanel(Zones.STACK);
        p.add(z, "stack");
        
        CardImage im = new CardImage();
        gui.add(im);
        p.add(im, "picture");
        
        CardDetail de = new CardDetail(18);
        gui.add(de);
        p.add(de, "detail");
        
        JLabel turnProgress = new JLabel();
        new TurnProgressUpdater(turnProgress, g);
        
        jf.add(turnProgress, BorderLayout.NORTH);
        jf.add(p);
        jf.add(new JButton(gui.getPassPriorityAction()), BorderLayout.SOUTH);
        
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
        
//        put(d, "Island", 20);
//        put(d, "Courier's Capsule", 12);
//        put(d, "Arcanis the Omnipotent", 8);
        put(d, "Forest", 20);
        put(d, "Llanowar Elves", 12);
        put(d, "Grizzly Bears", 8);
        

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
