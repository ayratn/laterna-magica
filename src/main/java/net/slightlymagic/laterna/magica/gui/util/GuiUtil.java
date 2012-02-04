/**
 * GuiUtil.java
 * 
 * Created on 10.04.2010
 */

package net.slightlymagic.laterna.magica.gui.util;


import static net.slightlymagic.laterna.magica.action.play.ActivateAction.*;
import static net.slightlymagic.laterna.magica.action.play.CastAction.*;
import static net.slightlymagic.laterna.magica.action.special.LandDropAction.*;
import static net.slightlymagic.laterna.magica.characteristic.CardType.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.ability.ActivatedAbility;
import net.slightlymagic.laterna.magica.action.GameAction;
import net.slightlymagic.laterna.magica.action.play.PlayAction;
import net.slightlymagic.laterna.magica.card.CardObject;
import net.slightlymagic.laterna.magica.characteristic.AbilityCharacteristic;
import net.slightlymagic.laterna.magica.characteristic.ObjectCharacteristics;
import net.slightlymagic.laterna.magica.player.Player;


/**
 * The class GuiUtil. This class provides utilities for the gui.
 * 
 * @version V0.0 10.04.2010
 * @author Clemens Koza
 */
public class GuiUtil {
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
            //TODO be multipart compatible
            if(card.getCharacteristics().get(0).hasType(LAND)) {
                list.add(newLandDropAction((CardObject) card));
            } else {
                list.add(newCastAction(player, (CardObject) card));
            }
        }
        
        for(ObjectCharacteristics c:card.getCharacteristics()) {
            Set<AbilityCharacteristic> abilities = new HashSet<AbilityCharacteristic>();
            boolean b = c.getAbilityCharacteristic().isAdding(abilities);
            //abilities can't be "all but"
            assert b;
            for(AbilityCharacteristic ability:abilities)
                if(ability.getAbility() instanceof ActivatedAbility) {
                    list.add(newActivateAction(player, card, (ActivatedAbility) ability.getAbility()));
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
}
