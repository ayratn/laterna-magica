/**
 * Printing.java
 * 
 * Created on 29.07.2010
 */

package net.slightlymagic.laterna.magica.card;


import java.io.Serializable;

import net.slightlymagic.laterna.magica.characteristics.Expansion;
import net.slightlymagic.laterna.magica.characteristics.Rarity;


/**
 * The class Printing.
 * 
 * @version V0.0 29.07.2010
 * @author Clemens Koza
 */
public class Printing implements Serializable {
    private static final long serialVersionUID = -4098276769529206054L;
    
    private CardTemplate      template;
    private Expansion         expansion;
    private Rarity            rarity;
    private int               multiverseID;
    
    public Printing(CardTemplate t, Expansion e, Rarity r, int multiverseID) {
        setTemplate(t);
        setExpansion(e);
        setRarity(r);
        setMultiverseID(multiverseID);
    }
    
    public CardTemplate getTemplate() {
        return template;
    }
    
    private void setTemplate(CardTemplate template) {
        this.template = template;
    }
    
    public Expansion getExpansion() {
        return expansion;
    }
    
    private void setExpansion(Expansion expansion) {
        this.expansion = expansion;
    }
    
    public Rarity getRarity() {
        return rarity;
    }
    
    private void setRarity(Rarity rarity) {
        this.rarity = rarity;
    }
    
    public int getMultiverseID() {
        return multiverseID;
    }
    
    private void setMultiverseID(int multiverseID) {
        this.multiverseID = multiverseID;
    }
    
    @Override
    public String toString() {
        return template + " - " + expansion + " " + rarity;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((expansion == null)? 0:expansion.hashCode());
        result = prime * result + multiverseID;
        result = prime * result + ((rarity == null)? 0:rarity.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null) return false;
        if(getClass() != obj.getClass()) return false;
        Printing other = (Printing) obj;
        if(expansion != other.expansion) return false;
        if(multiverseID != other.multiverseID) return false;
        if(rarity != other.rarity) return false;
        return true;
    }
}
