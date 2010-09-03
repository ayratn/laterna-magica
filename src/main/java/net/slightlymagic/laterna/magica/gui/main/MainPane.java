/**
 * MainPane.java
 * 
 * Created on 03.09.2010
 */

package net.slightlymagic.laterna.magica.gui.main;


import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.Executors;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.LaternaMagica;
import net.slightlymagic.laterna.magica.deck.Deck;
import net.slightlymagic.laterna.magica.gui.DisposeSupport;
import net.slightlymagic.laterna.magica.gui.Gui;
import net.slightlymagic.laterna.magica.gui.TurnProgressUpdater;
import net.slightlymagic.laterna.magica.gui.actor.GuiMagicActor;
import net.slightlymagic.laterna.magica.gui.card.CardDetail;
import net.slightlymagic.laterna.magica.gui.card.CardImage;
import net.slightlymagic.laterna.magica.gui.deckEditor.DeckEditorPane;
import net.slightlymagic.laterna.magica.gui.deckEditor.DeckIO;
import net.slightlymagic.laterna.magica.impl.GameImpl;
import net.slightlymagic.laterna.magica.impl.GameLoop;
import net.slightlymagic.laterna.magica.player.Player;
import net.slightlymagic.laterna.magica.player.impl.PlayerImpl;
import net.slightlymagic.laterna.magica.zone.Zone.Zones;

import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.MultiSplitLayout;
import org.jdesktop.swingx.MultiSplitLayout.Node;
import org.jetlang.channels.Channel;
import org.jetlang.channels.MemoryChannel;
import org.jetlang.core.Callback;
import org.jetlang.core.Disposable;
import org.jetlang.fibers.Fiber;
import org.jetlang.fibers.PoolFiberFactory;


/**
 * The class MainPane.
 * 
 * @version V0.0 03.09.2010
 * @author Clemens Koza
 */
public class MainPane extends JRootPane implements Disposable {
    private static final long serialVersionUID = 1118746971534003442L;
    
    public static void main(String[] args) throws IOException {
        LaternaMagica.init();
        
        JFrame jf = new JFrame();
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setTitle("Laterna Magica");
        
        jf.add(new MainPane());
        
        jf.pack();
        Dimension screen = jf.getToolkit().getScreenSize();
        Dimension window = jf.getSize();
        jf.setLocation((screen.width - window.width) / 2, (screen.height - window.height) / 2);
        
        jf.setVisible(true);
    }
    
    private JTextField             name1, name2;
    private DeckAction             deck1, deck2;
    
    private Channel<Runnable>      ch = new MemoryChannel<Runnable>();
    
    protected final DisposeSupport d  = new DisposeSupport();
    
    public MainPane() {
        PoolFiberFactory f = new PoolFiberFactory(Executors.newCachedThreadPool());
        Fiber fiber = f.create();
        fiber.start();
        d.add(ch.subscribe(fiber, new RunCallback()));
        d.add(fiber);
        d.add(f);
        
        setupComponents();
    }
    
    @Override
    public void dispose() {
        d.dispose();
    }
    
    private void setupComponents() {
        Container p = getContentPane();
        p.setLayout(new GridLayout(1, 0));
        
        JPanel editor = new JPanel(new BorderLayout());
        editor.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
        editor.add(new JButton(new EditorAction()));
        p.add(editor);
        
        JPanel names = new JPanel(new GridLayout(0, 1));
        names.add(name1 = new JTextField("You"));
        names.add(name2 = new JTextField("Opponent"));
        p.add(names);
        
        JPanel decks = new JPanel(new GridLayout(0, 1));
        DeckIO io = new DeckIO();
        decks.add(new JButton(deck1 = new DeckAction(io)));
        decks.add(new JButton(deck2 = new DeckAction(io)));
        p.add(decks);
        
        JPanel start = new JPanel(new BorderLayout());
        start.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
        start.add(new JButton(new StartGameAction()));
        p.add(start);
        
        add(p);
    }
    
    public static void setupGui(Gui gui) {
        JXMultiSplitPane overall = new JXMultiSplitPane(new MultiSplitLayout(getOverallLayout()));
        JXMultiSplitPane zones = new JXMultiSplitPane(new MultiSplitLayout(getZonesLayout()));
        
        JLabel turnProgress = new JLabel();
        new TurnProgressUpdater(turnProgress, gui.getGame());
        
        Container c = gui.getTable().getContentPane();
        c.add(turnProgress, BorderLayout.NORTH);
        c.add(overall);
        c.add(new JButton(gui.getPassPriorityAction()), BorderLayout.SOUTH);
        
        for(int i = 0; i < gui.getGame().getPlayers().size(); i++) {
            Player pl = gui.getGame().getPlayers().get(i);
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
    
    private class RunCallback implements Callback<Runnable> {
        public void onMessage(Runnable message) {
            Window w = SwingUtilities.getWindowAncestor(MainPane.this);
            if(w != null) w.setVisible(false);
            message.run();
            if(w != null) w.setVisible(true);
        }
    }
    
    private class DeckAction extends AbstractAction {
        private static final long serialVersionUID = 2658481541415357720L;
        
        private DeckIO            io;
        private Deck              deck;
        
        public DeckAction(DeckIO io) {
            super("Select Deck");
            this.io = io;
        }
        
        public Deck getDeck() {
            return deck;
        }
        
        public void actionPerformed(ActionEvent e) {
            File f = io.open();
            deck = io.open(f);
            putValue(NAME, deck == null? "Select Deck":f.getName());
        }
    }
    
    private class EditorAction extends AbstractAction {
        private static final long serialVersionUID = -4461470152106197557L;
        
        public EditorAction() {
            super("Deck Editor");
        }
        
        public void actionPerformed(ActionEvent e) {
            ch.publish(new Runnable() {
                public void run() {
                    JDialog jf = new JDialog((Frame) null, "Deck Editor", true);
                    jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    
                    jf.add(new DeckEditorPane());
                    
                    jf.setSize(1000, 800);
                    Dimension screen = jf.getToolkit().getScreenSize();
                    Dimension window = jf.getSize();
                    jf.setLocation((screen.width - window.width) / 2, (screen.height - window.height) / 2);
                    
                    jf.setVisible(true);
                }
            });
        }
    }
    
    private class StartGameAction extends AbstractAction {
        private static final long serialVersionUID = 94519598316379563L;
        
        public StartGameAction() {
            super("Start Game");
        }
        
        public void actionPerformed(ActionEvent e) {
            if(deck1.getDeck() == null || deck2.getDeck() == null) return;
            
            ch.publish(new Runnable() {
                public void run() {
                    final Game g = new GameImpl();
                    final Gui gui = new Gui(g);
                    
                    String[] names = {name1.getText(), name2.getText()};
                    Deck[] decks = {deck1.getDeck(), deck2.getDeck()};
                    
                    for(int i = 0; i < names.length; i++) {
                        Player p = new PlayerImpl(g, names[i]);
                        p.setActor(new GuiMagicActor(gui, p));
                        p.setDeck(decks[i]);
                        g.getPlayers().add(p);
                    }
                    
                    setupGui(gui);
                    
                    JFrame jf = new JFrame("Game");
                    jf.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                    jf.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                            gui.publishConcede();
                        }
                    });
                    
                    jf.add(gui.getTable());
                    
                    jf.setSize(1000, 800);
                    Dimension screen = jf.getToolkit().getScreenSize();
                    Dimension window = jf.getSize();
                    jf.setLocation((screen.width - window.width) / 2, (screen.height - window.height) / 2);
                    
                    jf.setVisible(true);
                    
                    g.startGame();
                    new GameLoop(g).run();
                    
                    jf.dispose();
                }
            });
        }
    }
}
