/**
 * FastTest.java
 * 
 * Created on 05.02.2012
 */

package net.slightlymagic.laterna.magica;


import static net.slightlymagic.laterna.magica.LaternaMagica.*;
import static net.slightlymagic.laterna.magica.impl.GameImpl.*;
import static net.slightlymagic.laterna.magica.impl.GameLoop.*;
import static net.slightlymagic.laterna.magica.player.impl.PlayerImpl.*;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Map.Entry;
import java.util.UUID;

import javax.swing.JFrame;

import net.slightlymagic.laterna.magica.deck.Deck;
import net.slightlymagic.laterna.magica.gui.Gui;
import net.slightlymagic.laterna.magica.gui.actor.GuiMagicActor;
import net.slightlymagic.laterna.magica.gui.deckEditor.DeckIO;
import net.slightlymagic.laterna.magica.gui.main.MainPane;
import net.slightlymagic.laterna.magica.player.Player;
import net.slightlymagic.objectTransactions.History;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <p>
 * The class FastTest.
 * </p>
 * 
 * @version V0.0 05.02.2012
 * @author Clemens Koza
 */
public class FastTest {
    private static final Logger log = LoggerFactory.getLogger(FastTest.class);
    
    public static void main(String[] args) throws Exception {
        LaternaInit.init();
        
        Deck deck = new DeckIO().open(new File(MAGICA_CONFIG().getDecksFolder(), "green.deck"));
        
        History h = History.createHistory(UUID.randomUUID());
        h.pushHistoryForThread();
        try {
            final Game g = newGameImpl();
            final Gui gui = new Gui(g);
            
            String[] names = {"You", "Opponent"};
            Deck[] decks = {deck, deck};
            
            for(int i = 0; i < names.length; i++) {
                Player p = newPlayerImpl(names[i]);
                p.setActor(new GuiMagicActor(gui, p));
                p.setDeck(decks[i]);
                g.getPlayers().add(p);
            }
            
            MainPane.setupGui(gui);
            
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
            newGameLoop().run();
            
            jf.dispose();
            
            for(Player p:g.getPlayers())
                if(p.getActor() instanceof GuiMagicActor) ((GuiMagicActor) p.getActor()).dispose();
            gui.dispose();
        } finally {
            h.popHistoryForThread();
        }
        
        for(Entry<Thread, StackTraceElement[]> e:Thread.getAllStackTraces().entrySet()) {
            Thread t = e.getKey();
            log.debug(t.getName() + ": " + t.getState());
            for(StackTraceElement el:e.getValue())
                log.debug("\t" + el);
        }
        
        System.exit(0);
    }
}
