/**
 * Zone.java
 * 
 * Created on 05.09.2009
 */

package net.slightlymagic.laterna.magica.zone;


import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.slightlymagic.laterna.magica.GameContent;
import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.card.CardObject;
import net.slightlymagic.laterna.magica.event.MoveCardListener;
import net.slightlymagic.laterna.magica.player.Player;



/**
 * The class Zone.
 * 
 * @version V0.0 05.09.2009
 * @author Clemens Koza
 */
public interface Zone extends GameContent {
    /**
     * The {@code cards} property name
     */
    public static final String CARDS = "cards";
    
    public static enum Zones {
        /**
         * {@magic.ruleRef 407 CR 407}
         */
        ANTE,
        /**
         * {@magic.ruleRef 403 CR 403}
         */
        BATTLEFIELD,
        /**
         * 
         */
        COMMAND,
        /**
         * {@magic.ruleRef 406 CR 406}
         */
        EXILE,
        /**
         * {@magic.ruleRef 404 CR 404}
         */
        GRAVEYARD,
        /**
         * {@magic.ruleRef 402 CR 402}
         */
        HAND,
        /**
         * {@magic.ruleRef 401 CR 401}
         */
        LIBRARY,
        /**
         * {@magic.ruleRef 405 CR 405}
         */
        STACK;
        
        private final String name;
        
        private Zones() {
            String s = super.toString().toLowerCase();
            Matcher m = Pattern.compile("(^|_)(.)").matcher(s);
            StringBuffer result = new StringBuffer();
            while(m.find()) {
                boolean b = m.group(1).length() != 0;
                m.appendReplacement(result, Matcher.quoteReplacement((b? " ":"") + m.group(2).toUpperCase()));
            }
            m.appendTail(result);
            name = result.toString();
        }
        
        @Override
        public String toString() {
            return name;
        }
    }
    
    /**
     * Returns what type of zone this is.
     */
    public Zones getType();
    
    /**
     * Returns the Zone's owner. A shared zone has {@code null} for this property.
     */
    public Player getOwner();
    
    /**
     * Returns the cards in the zone. DO NOT ADD/REMOVE CARDS. The only place from where to modify the list is
     * {@link CardObject}, and only for adding/removing itself. To a sorted zone, the order of cards has rules
     * relevance. For nonsorted zones that use {@link List}s, the order of cards may be modified.
     */
    public Collection<MagicObject> getCards();
    
    /**
     * Convenience method for {@code getCards().size()}.
     */
    public int size();
    
    /**
     * Convenience method for {@code getCards().isEmpty()}.
     */
    public boolean isEmpty();
    
    public void addPropertyChangeListener(PropertyChangeListener listener);
    
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);
    
    public void removePropertyChangeListener(PropertyChangeListener listener);
    
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);
    
    public void addMoveCardListener(MoveCardListener l);
    
    public void removeMoveCardListener(MoveCardListener l);
    
    public Iterator<MoveCardListener> getMoveCardListeners();
}
