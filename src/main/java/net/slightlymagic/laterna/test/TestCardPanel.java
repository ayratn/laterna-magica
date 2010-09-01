/**
 * TestTurnStructure.java
 * 
 * Created on 24.03.2010
 */

package net.slightlymagic.laterna.test;


import java.awt.BorderLayout;
import java.awt.Container;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.LaternaMagica;
import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.card.CardObject;
import net.slightlymagic.laterna.magica.card.CardTemplate;
import net.slightlymagic.laterna.magica.card.Printing;
import net.slightlymagic.laterna.magica.deck.Deck;
import net.slightlymagic.laterna.magica.deck.Deck.DeckType;
import net.slightlymagic.laterna.magica.deck.impl.DeckImpl;
import net.slightlymagic.laterna.magica.gui.Gui;
import net.slightlymagic.laterna.magica.gui.TurnProgressUpdater;
import net.slightlymagic.laterna.magica.gui.actor.GuiMagicActor;
import net.slightlymagic.laterna.magica.gui.card.CardDetail;
import net.slightlymagic.laterna.magica.gui.card.CardImage;
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
        

        JXMultiSplitPane overall = new JXMultiSplitPane(new MultiSplitLayout(getOverallLayout()));
        JXMultiSplitPane zones = new JXMultiSplitPane(new MultiSplitLayout(getZonesLayout()));
        
        JLabel turnProgress = new JLabel();
        new TurnProgressUpdater(turnProgress, g);
        
        Container c = gui.getTable().getContentPane();
        c.add(turnProgress, BorderLayout.NORTH);
        c.add(overall);
        c.add(new JButton(gui.getPassPriorityAction()), BorderLayout.SOUTH);
        
        for(int i = 0; i < g.getPlayers().size(); i++) {
            Player pl = g.getPlayers().get(i);
            overall.add(gui.getPlayerPanel(pl), "player" + i);
            
            zones.add(gui.getZonePanel(pl, Zones.HAND), "hand" + i);
            zones.add(gui.getZonePanel(pl, Zones.BATTLEFIELD), "play" + i);
        }
        
        {
            JPanel p = new JPanel(new BorderLayout());
            p.add(zones);
            p.add(gui.getZonePanel(Zones.STACK), BorderLayout.EAST);
            overall.add(p, "center");
        }
        

        CardImage im = new CardImage();
        gui.add(im);
        overall.add(im, "picture");
        
        CardDetail de = new CardDetail(18);
        gui.add(de);
        overall.add(de, "detail");
        

        JFrame jf = new JFrame();
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        jf.add(gui.getTable());
        
        jf.setSize(700, 300);
        jf.setVisible(true);
        
        g.startGame();
        putIntoPlay(g, "Trained Armodon", 2);
        putIntoPlay(g, "Grizzly Bears", 2);
        putIntoPlay(g, "Llanowar Elves", 2);
        
        //run in the main thread
        new GameLoop(g).run();
    }
    
    private static void putIntoPlay(Game game, String name, int num) {
        CardTemplate grizzly = LaternaMagica.CARDS().getCard(name);
        List<MagicObject> grizzlies = new ArrayList<MagicObject>();
        
        for(Player p:game.getPlayers()) {
            int count = 0;
            for(MagicObject o:p.getLibrary().getCards()) {
                if(((CardObject) o).getTemplate().equals(grizzly)) {
                    grizzlies.add(o);
                    if(++count == num) break;
                }
            }
        }
        
        for(MagicObject o:grizzlies) {
            o.setZone(game.getBattlefield());
            o.getCounter("summoningSickness").reset();
        }
    }
    
    private static Node getOverallLayout() {
        StringWriter sw = new StringWriter();
        PrintWriter w = new PrintWriter(sw);
        w.println("(ROW");
        w.println(" (COLUMN");
        w.println("  (LEAF name=player1 weight=0.5)");
        w.println("  (LEAF name=player0 weight=0.5)");
        w.println(" )");
        w.println("  (LEAF name=center  weight=1)");
        w.println(" (COLUMN");
        w.println("  (LEAF name=picture weight=0.5)");
        w.println("  (LEAF name=detail  weight=0.5)");
        w.println(" )");
        w.println(")");
        return MultiSplitLayout.parseModel(sw.toString());
    }
    
    private static Node getZonesLayout() {
        StringWriter sw = new StringWriter();
        PrintWriter w = new PrintWriter(sw);
        w.println("(COLUMN");
        w.println(" (LEAF name=hand1 weight=0.25)");
        w.println(" (LEAF name=play1 weight=0.25)");
        w.println(" (LEAF name=play0 weight=0.25)");
        w.println(" (LEAF name=hand0 weight=0.25)");
        w.println(")");
        return MultiSplitLayout.parseModel(sw.toString());
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
        put(d, "Llanowar Elves", 10);
        put(d, "Grizzly Bears", 5);
        put(d, "Trained Armodon", 5);
        

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
