/**
 * CharacteristicSnapshot.java
 * 
 * Created on 01.04.2010
 */

package net.slightlymagic.laterna.magica.characteristic;


import java.util.HashSet;
import java.util.Observable;
import java.util.Set;

import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.ability.Ability;
import net.slightlymagic.laterna.magica.card.CardObject;
import net.slightlymagic.laterna.magica.card.CardParts;
import net.slightlymagic.laterna.magica.card.CardTemplate;
import net.slightlymagic.laterna.magica.card.Printing;
import net.slightlymagic.laterna.magica.characteristics.CardType;
import net.slightlymagic.laterna.magica.characteristics.MagicColor;
import net.slightlymagic.laterna.magica.characteristics.SubType;
import net.slightlymagic.laterna.magica.characteristics.SuperType;
import net.slightlymagic.laterna.magica.mana.ManaSequence;


/**
 * The class CharacteristicSnapshot. Characteristic snapshots are meant to store a {@link ObjectCharacteristics}'s
 * values at a single point in time, without updates afterwards. This is often better in performance than getting
 * the values directly from the characteristics, because the characteristics implementation can optimize on the
 * fact that the state won't change between reading individual characteristic values.
 * 
 * @version V1.0 01.04.2010
 * @author Clemens Koza
 */
public class CharacteristicSnapshot extends Observable {
    //MagicObject
    private ObjectCharacteristics                 characteristics;
    //CardTemplate
    private CardParts                             parts;
    private Printing                              printing;
    
    private String                                name;
    private ManaSequence                          manaCost;
    private int                                   power, toughness, loyalty;
    
    private SetCharacteristicSnapshot<MagicColor> colors     = new SetCharacteristicSnapshot<MagicColor>();
    private SetCharacteristicSnapshot<SuperType>  superTypes = new SetCharacteristicSnapshot<SuperType>();
    private SetCharacteristicSnapshot<CardType>   types      = new SetCharacteristicSnapshot<CardType>();
    private SetCharacteristicSnapshot<SubType>    subTypes   = new SetCharacteristicSnapshot<SubType>();
    private SetCharacteristicSnapshot<Ability>    abilities  = new SetCharacteristicSnapshot<Ability>();
    
    public void setCharacteristics(ObjectCharacteristics characteristics) {
        if(!characteristics.equals(this.characteristics)) setChanged();
        this.characteristics = characteristics;
        parts = null;
        printing = null;
    }
    
    public void setParts(CardParts parts, Printing printing) {
        if(!parts.equals(this.parts) || !printing.equals(this.printing)) setChanged();
        this.parts = parts;
        this.printing = printing;
        characteristics = null;
    }
    
    public CardTemplate getTemplate() {
        Printing p = getPrinting();
        if(p != null) return p.getTemplate();
        else return null;
    }
    
    public CardParts getParts() {
        if(characteristics instanceof CardCharacteristics) {
            return ((CardCharacteristics) characteristics).getParts();
        } else return parts;
    }
    
    public ObjectCharacteristics getCharacteristics() {
        return characteristics;
    }
    
    public MagicObject getCard() {
        if(characteristics != null) return characteristics.getCard();
        else return null;
    }
    
    public Printing getPrinting() {
        MagicObject o = getCard();
        if(o instanceof CardObject) return ((CardObject) o).getPrinting();
        else return printing;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        if(name != this.name && (name == null || !name.equals(this.name))) setChanged();
        this.name = name;
    }
    
    
    public ManaSequence getManaCost() {
        return manaCost;
    }
    
    public void setManaCost(ManaSequence manaCost) {
        if(manaCost != this.manaCost && (manaCost == null || !manaCost.equals(this.manaCost))) setChanged();
        this.manaCost = manaCost;
    }
    
    
    public int getPower() {
        return power;
    }
    
    public void setPower(int power) {
        if(power != this.power) setChanged();
        this.power = power;
    }
    
    public int getToughness() {
        return toughness;
    }
    
    public void setToughness(int toughness) {
        if(toughness != this.toughness) setChanged();
        this.toughness = toughness;
    }
    
    public void setLoyalty(int loyalty) {
        this.loyalty = loyalty;
    }
    
    public int getLoyalty() {
        return loyalty;
    }
    
    
    public SetCharacteristicSnapshot<MagicColor> getColors() {
        return colors;
    }
    
    public SetCharacteristicSnapshot<SuperType> getSuperTypes() {
        return superTypes;
    }
    
    public SetCharacteristicSnapshot<CardType> getTypes() {
        return types;
    }
    
    public SetCharacteristicSnapshot<SubType> getSubTypes() {
        return subTypes;
    }
    
    public SetCharacteristicSnapshot<Ability> getAbilities() {
        return abilities;
    }
    
    
    public class SetCharacteristicSnapshot<E> {
        private Set<E>  values;
        private boolean adding;
        
        private SetCharacteristicSnapshot() {
            values = new HashSet<E>();
        }
        
        public Set<E> getValues() {
            return values;
        }
        
        public boolean isAdding() {
            return adding;
        }
        
        public void setAdding(boolean adding) {
            if(adding != this.adding) setChanged();
            this.adding = adding;
        }
        
        public boolean hasValue(E value) {
            return getValues().contains(value) == isAdding();
        }
        
        public String valueString() {
            if(getValues().isEmpty()) return "";
            return (isAdding()? "+":"-") + getValues().toString();
        }
        
        public void setChanged() {
            CharacteristicSnapshot.this.setChanged();
        }
    }
}
