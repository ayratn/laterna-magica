/**
 * GuiUtil.java
 * 
 * Created on 10.04.2010
 */

package net.slightlymagic.laterna.magica.gui.util;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;

import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.ability.Ability;
import net.slightlymagic.laterna.magica.ability.ActivatedAbility;
import net.slightlymagic.laterna.magica.action.GameAction;
import net.slightlymagic.laterna.magica.action.play.ActivateAction;
import net.slightlymagic.laterna.magica.action.play.CastAction;
import net.slightlymagic.laterna.magica.action.play.PlayAction;
import net.slightlymagic.laterna.magica.action.special.LandDropAction;
import net.slightlymagic.laterna.magica.card.CardObject;
import net.slightlymagic.laterna.magica.characteristic.CharacteristicSnapshot;
import net.slightlymagic.laterna.magica.characteristics.CardType;
import net.slightlymagic.laterna.magica.gui.actor.GuiMagicActor;
import net.slightlymagic.laterna.magica.gui.card.CardDisplay;
import net.slightlymagic.laterna.magica.gui.card.CardTextButton;
import net.slightlymagic.laterna.magica.player.Player;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The class GuiUtil. This class provides utilities for the gui.
 * 
 * @version V0.0 10.04.2010
 * @author Clemens Koza
 */
public class GuiUtil {
    private static final Logger log = LoggerFactory.getLogger(GuiUtil.class);
    
    ////
    // Actions
    ////
    
    /**
     * Returns a list of actions that can be started from the specified card. For example, a GUI element
     * representing a card could use this to determine what to do when clicking it. Common examples would be
     * playing the card (as a land or spell), activating one of its abilities, etc. Choosing a card as a target or
     * similar things don't fall in this category, as these are not {@link GameAction}s.
     */
    public static List<PlayAction> getActions(final Player player, final MagicObject card) {
        List<PlayAction> list = new ArrayList<PlayAction>();
        
        if(card instanceof CardObject) {
            if(card.getCharacteristics().get(0).hasType(CardType.LAND)) {
                list.add(new LandDropAction((CardObject) card));
            } else {
                list.add(new CastAction(player, (CardObject) card));
            }
        }
        
        Set<Ability> abilities = new HashSet<Ability>();
        boolean b = card.getCharacteristics().get(0).getAbilityCharacteristic().isAdding(abilities);
        //abilities can't be "all but"
        assert b;
        for(Ability ability:abilities) {
            if(ability instanceof ActivatedAbility) {
                list.add(new ActivateAction(player, card, (ActivatedAbility) ability));
            }
        }
        
        for(Iterator<PlayAction> it = list.iterator(); it.hasNext();)
            if(!it.next().isLegal()) it.remove();
        
        return list;
    }
    
    /**
     * Returns one of the actions from {@link #getActions(Player, MagicObject)}, chosen by a graphical dialog.
     * Returns null immediately if there are no actions avaliable, and the first if there's only one. Otherwise,
     * will never return null.
     */
    public static PlayAction getAction(Player player, MagicObject card) {
        List<PlayAction> actions = getActions(player, card);
        switch(actions.size()) {
            case 0:
                return null;
            case 1:
                return actions.get(0);
            default:
                ListChooser<PlayAction> chooser = new ListChooser<PlayAction>("Choose one", "Choose an action",
                        actions);
                chooser.show();
                return chooser.getSelectedValue();
        }
    }
    
    /**
     * Returns one of the actions from {@link #getActions(Player, MagicObject)}, chosen by a graphical dialog, or
     * null. Returns null immediately if there are no actions avaliable, and the first if there's only one.
     */
    public static PlayAction getActionOptional(Player player, MagicObject card) {
        List<PlayAction> actions = getActions(player, card);
        switch(actions.size()) {
            case 0:
                return null;
            case 1:
                return actions.get(0);
            default:
                ListChooser<PlayAction> chooser = new ListChooser<PlayAction>("Choose one", "Choose an action", 0,
                        1, actions);
                chooser.show();
                return chooser.getSelectedValue();
        }
    }
    
    ////
    // Cards
    ////
    

    //TODO this will cause a memory leak when playing multiple games. add some abstraction for a GUI instance
    private static final List<GuiMagicActor> actors = new ArrayList<GuiMagicActor>();
    
    public static void add(GuiMagicActor actor) {
        actors.add(actor);
    }
    
    public static void remove(GuiMagicActor actor) {
        actors.remove(actor);
    }
    
    private static final class CardListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            CardTextButton p = (CardTextButton) e.getSource();
            MagicObject c = p.getCard().getCard();
            
            log.debug("Card clicked: " + c);
            log.trace("Publishing to " + actors);
            for(GuiMagicActor actor:actors)
                actor.channels.objects.publish(c);
        }
    }
    
    private static final List<CardDisplay> cards = new ArrayList<CardDisplay>();
    
    public static void add(CardDisplay card) {
        cards.add(card);
    }
    
    public static void remove(CardDisplay card) {
        cards.remove(card);
    }
    
    private static final class CardMouseListener extends MouseAdapter {
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
            for(CardDisplay c:cards)
                c.setCard(null);
        }
    }
    
    private static final ActionListener    cardListener      = new CardListener();
    private static final CardMouseListener cardMouseListener = new CardMouseListener();
    
    /**
     * Returns a component for displaying the specified card. The returned component will also implement
     * {@link CardDisplay}.
     */
    public static JComponent createComponent(MagicObject card) {
        CharacteristicSnapshot s = card.getCharacteristics().get(0).getCharacteristics(
                new CharacteristicSnapshot());
        CardTextButton p = new CardTextButton(s);
        p.addActionListener(cardListener);
        p.addMouseListener(cardMouseListener);
        return p;
    }
}
