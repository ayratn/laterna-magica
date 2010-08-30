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
         * {@magic.ruleRef 20100716/R407}
         */
        ANTE(false),
        /**
         * {@magic.ruleRef 20100716/R403}
         */
        BATTLEFIELD(false),
        /**
         * {@magic.ruleRef 20100716/R408}
         */
        COMMAND(false),
        /**
         * {@magic.ruleRef 20100716/R406}
         */
        EXILE(false),
        /**
         * {@magic.ruleRef 20100716/R404}
         */
        GRAVEYARD(true),
        /**
         * {@magic.ruleRef 20100716/R402}
         */
        HAND(true),
        /**
         * {@magic.ruleRef 20100716/R401}
         */
        LIBRARY(true),
        /**
         * {@magic.ruleRef 20100716/R405}
         */
        STACK(false);
        
        private final String  name;
        private final boolean owned;
        
        private Zones(boolean owned) {
            this.owned = owned;
            
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
        
        public boolean isOwnedZone() {
            return owned;
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
