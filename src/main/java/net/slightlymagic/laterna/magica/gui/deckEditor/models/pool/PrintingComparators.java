/**
 * PrintingComparators.java
 * 
 * Created on 01.09.2010
 */

package net.slightlymagic.laterna.magica.gui.deckEditor.models.pool;


import java.util.Comparator;

import net.slightlymagic.laterna.magica.card.Printing;
import net.slightlymagic.laterna.magica.gui.deckEditor.models.templates.CardTemplateComparators;


public enum PrintingComparators implements Comparator<Printing> {
    MULTIVERSE_INSTANCE {
        public int compare(Printing o1, Printing o2) {
            return o1.getMultiverseID() - o2.getMultiverseID();
        }
    },
    COLOR_NAME_INSTANCE {
        public int compare(Printing o1, Printing o2) {
            int i = CardTemplateComparators.INSTANCE.compare(o1.getTemplate(), o2.getTemplate());
            if(i != 0) return i;
            return MULTIVERSE_INSTANCE.compare(o1, o2);
        }
    };
}
