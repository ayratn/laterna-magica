/**
 * CardPartsImpl.java
 * 
 * Created on 03.09.2009
 */

package net.slightlymagic.laterna.magica.card.impl;


import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import net.slightlymagic.laterna.magica.ability.Ability;
import net.slightlymagic.laterna.magica.action.play.CastAction;
import net.slightlymagic.laterna.magica.action.play.PlayInformation;
import net.slightlymagic.laterna.magica.card.CardParts;
import net.slightlymagic.laterna.magica.characteristic.CharacteristicSnapshot;
import net.slightlymagic.laterna.magica.characteristic.CharacteristicSnapshot.SetCharacteristicSnapshot;
import net.slightlymagic.laterna.magica.characteristics.CardType;
import net.slightlymagic.laterna.magica.characteristics.MagicColor;
import net.slightlymagic.laterna.magica.characteristics.SubType;
import net.slightlymagic.laterna.magica.characteristics.SuperType;
import net.slightlymagic.laterna.magica.mana.ManaSequence;
import net.slightlymagic.laterna.magica.mana.impl.ManaSequenceImpl;

import com.google.common.base.Function;
import com.google.common.base.Predicate;


/**
 * The class CardPartsImpl.
 * 
 * @version V0.0 03.09.2009
 * @author Clemens Koza
 */
public class CardPartsImpl implements CardParts {
    private static final long                                       serialVersionUID = -2759043004643600286L;
    
    private String                                                  name;
    private ManaSequence                                            manaCost;
    private Set<MagicColor>                                         colors;
    private Set<SuperType>                                          superTypes;
    private Set<CardType>                                           cardTypes;
    private Set<SubType>                                            subTypes;
    private Set<Ability>                                            abilities;
    private int                                                     p, t, l;
    
    private Predicate<? super CastAction>                           legal;
    private Function<? super CastAction, ? extends PlayInformation> info;
    
    public CharacteristicSnapshot getCharacteristics(CharacteristicSnapshot c) {
        if(c == null) return null;
        c.setName(getName());
        c.setManaCost(getManaCost());
        c.setPower(getPower());
        c.setToughness(getToughness());
        c.setLoyalty(getLoyalty());
        
        getSetCharacteristic(c.getColors(), getColors());
        getSetCharacteristic(c.getSuperTypes(), getSuperTypes());
        getSetCharacteristic(c.getTypes(), getTypes());
        getSetCharacteristic(c.getSubTypes(), getSubTypes());
        getSetCharacteristic(c.getAbilities(), getAbilities());
        
        c.notifyObservers();
        
        return c;
    }
    
    private <E> void getSetCharacteristic(SetCharacteristicSnapshot<E> c, Set<E> values) {
        c.setAdding(true);
        if(!c.getValues().equals(values)) {
            c.setChanged();
            c.getValues().clear();
            c.getValues().addAll(values);
        }
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        if(name == null) name = "";
        return name;
    }
    
    
    public void setManaCost(ManaSequence manaCost) {
        this.manaCost = manaCost;
    }
    
    public ManaSequence getManaCost() {
        if(manaCost == null) manaCost = new ManaSequenceImpl();
        return manaCost;
    }
    
    
    public void takeColors() {
        setColors(getManaCost().getColors());
    }
    
    public void setColors(MagicColor... colors) {
        setColors(Arrays.asList(colors));
    }
    
    public void setColors(Collection<MagicColor> colors) {
        getColors().clear();
        getColors().addAll(colors);
    }
    
    public Set<MagicColor> getColors() {
        if(colors == null) colors = EnumSet.noneOf(MagicColor.class);
        return colors;
    }
    
    
    public void setSuperTypes(SuperType... types) {
        setSuperTypes(Arrays.asList(types));
    }
    
    public void setSuperTypes(Collection<SuperType> types) {
        getSuperTypes().clear();
        getSuperTypes().addAll(types);
    }
    
    public Set<SuperType> getSuperTypes() {
        if(superTypes == null) superTypes = EnumSet.noneOf(SuperType.class);
        return superTypes;
    }
    
    
    public void setTypes(CardType... types) {
        setTypes(Arrays.asList(types));
    }
    
    public void setTypes(Collection<CardType> types) {
        getTypes().clear();
        getTypes().addAll(types);
    }
    
    public Set<CardType> getTypes() {
        if(cardTypes == null) cardTypes = EnumSet.noneOf(CardType.class);
        return cardTypes;
    }
    
    
    public void setSubTypes(SubType... types) {
        setSubTypes(Arrays.asList(types));
    }
    
    public void setSubTypes(Collection<SubType> types) {
        getSubTypes().clear();
        getSubTypes().addAll(types);
    }
    
    public Set<SubType> getSubTypes() {
        if(subTypes == null) subTypes = new HashSet<SubType>();
        return subTypes;
    }
    
    public void setAbilities(Ability... abilities) {
        setAbilities(Arrays.asList(abilities));
    }
    
    public void setAbilities(Collection<Ability> abilities) {
        getAbilities().clear();
        getAbilities().addAll(abilities);
    }
    
    public Set<Ability> getAbilities() {
        if(abilities == null) abilities = new HashSet<Ability>();
        return abilities;
    }
    
    
    public void setPower(int p) {
        this.p = p;
    }
    
    public int getPower() {
        return p;
    }
    
    
    public void setToughness(int t) {
        this.t = t;
    }
    
    public int getToughness() {
        return t;
    }
    
    
    public void setLoyalty(int l) {
        this.l = l;
    }
    
    public int getLoyalty() {
        return l;
    }
    
    
    public void setLegal(Predicate<? super CastAction> legal) {
        this.legal = legal;
    }
    
    public Predicate<? super CastAction> getLegal() {
        return legal;
    }
    
    public boolean isLegal(CastAction a) {
        return legal.apply(a);
    }
    
    public void setPlayInformation(Function<? super CastAction, ? extends PlayInformation> info) {
        this.info = info;
    }
    
    public Function<? super CastAction, ? extends PlayInformation> getPlayInformation() {
        return info;
    }
    
    public PlayInformation getPlayInformation(CastAction a) {
        return getPlayInformation().apply(a);
    }
    
    @Override
    public String toString() {
        return getName();
    }
    
    //this class doesn't need undo s since it's not GameContent. CardParts are not changed during a game;
    //they are the representation of immutable, printed cards
}
