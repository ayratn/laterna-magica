/**
 * CardTemplateImpl.java
 * 
 * Created on 03.09.2009
 */

package net.slightlymagic.laterna.magica.card.impl;


import static net.slightlymagic.laterna.magica.card.CardTemplate.CardLayout.*;

import java.util.ArrayList;
import java.util.List;

import net.slightlymagic.laterna.magica.card.CardParts;
import net.slightlymagic.laterna.magica.card.CardTemplate;
import net.slightlymagic.laterna.magica.card.Printing;

import com.google.common.base.Joiner;


/**
 * The class CardTemplateImpl.
 * 
 * @version V0.0 03.09.2009
 * @author Clemens Koza
 */
public class CardTemplateImpl implements CardTemplate {
    private static final long serialVersionUID = -1749043979051384410L;
    
    private List<CardParts>   parts;
    private List<Printing>    sets;
    
    public CardTemplateImpl() {
        parts = new ArrayList<CardParts>();
        sets = new ArrayList<Printing>();
    }
    
    public CardLayout getType() {
        return NORMAL;
    }
    
    public List<CardParts> getCardParts() {
        return parts;
    }
    
    public List<Printing> getPrintings() {
        return sets;
    }
    
    /**
     * Returns a card part's names separated by "//"
     */
    @Override
    public String toString() {
        return Joiner.on("//").join(getCardParts());
    }
}
