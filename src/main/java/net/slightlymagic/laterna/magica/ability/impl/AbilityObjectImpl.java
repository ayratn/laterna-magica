/**
 * AbilityObjectImpl.java
 * 
 * Created on 21.04.2010
 */

package net.slightlymagic.laterna.magica.ability.impl;


import static java.util.Collections.*;

import java.util.List;

import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.ability.AbilityObject;
import net.slightlymagic.laterna.magica.ability.ActivatedAbility;
import net.slightlymagic.laterna.magica.ability.NonStaticAbility;
import net.slightlymagic.laterna.magica.ability.TriggeredAbility;
import net.slightlymagic.laterna.magica.action.play.ActivateAction;
import net.slightlymagic.laterna.magica.action.play.PlayAction;
import net.slightlymagic.laterna.magica.action.play.PlayInformation;
import net.slightlymagic.laterna.magica.characteristic.AbilityCharacteristics;
import net.slightlymagic.laterna.magica.characteristic.impl.AbilityCharacteristicsImpl;
import net.slightlymagic.laterna.magica.edit.property.EditableProperty;
import net.slightlymagic.laterna.magica.edit.property.EditablePropertyChangeSupport;
import net.slightlymagic.laterna.magica.impl.MagicObjectImpl;


/**
 * The class AbilityObjectImpl.
 * 
 * @version V0.0 21.04.2010
 * @author Clemens Koza
 */
public class AbilityObjectImpl extends MagicObjectImpl implements AbilityObject {
    //the object the ability is on
    private MagicObject                            object;
    //the ability this object represents
    private NonStaticAbility                       ability;
    private List<? extends AbilityCharacteristics> characteristics;
    
    private final EditablePropertyChangeSupport    s;
    private EditableProperty<PlayInformation>      info;
    
    public AbilityObjectImpl(Game game, MagicObject object, NonStaticAbility ability) {
        super(game);
        this.object = object;
        this.ability = ability;
        
        s = new EditablePropertyChangeSupport(getGame(), this);
        info = new EditableProperty<PlayInformation>(getGame(), s, "info");
        
        setOwner(object.getOwner());
        characteristics = singletonList(new AbilityCharacteristicsImpl(object, ability));
    }
    
    
    public MagicObject getObject() {
        return object;
    }
    
    
    public NonStaticAbility getAbility() {
        return ability;
    }
    
    
    public List<? extends AbilityCharacteristics> getCharacteristics() {
        return characteristics;
    }
    
    
    public boolean isLegal(PlayAction a) {
        NonStaticAbility ab = getCharacteristics().get(0).getAbility();
        //triggered abilities can't be played, so return false here
        if(ab instanceof TriggeredAbility) return false;
        //other wise must be an activated ability
        if(!(a instanceof ActivateAction)) {
            throw new IllegalArgumentException("An ability can only be activated using an ActivateAction");
        }
        return ((ActivatedAbility) ab).isLegal((ActivateAction) a);
    }
    
    
    public void play(PlayAction a) {
        NonStaticAbility ab = getCharacteristics().get(0).getAbility();
        
        if((ab instanceof ActivatedAbility) && !(a instanceof ActivateAction)) {
            throw new IllegalArgumentException("An ability can only be activated using an ActivateAction");
        }
        //TODO implement triggered abilities
//        if((ab instanceof TriggeredAbility) && !(a instanceof TriggerAction)) {
//            throw new IllegalArgumentException("An ability can only be triggered using a TriggerAction");
//        }
        
        //Create the info before changing the zones so that listeners see the PlayInformation
        PlayInformation info;
        if(ab instanceof ActivatedAbility) info = ((ActivatedAbility) ab).getPlayInformation((ActivateAction) a);
//        else ((TriggeredAbility) ab).getPlayInformation((TriggerAction) a);
        else throw new UnsupportedOperationException("triggered not implemented");
        this.info.setValue(info);
        
        if(!ab.isManaAbility()) setZone(getGame().getStack());
    }
    
    
    public PlayInformation getPlayInformation() {
        PlayInformation info = this.info.getValue();
        if(info == null) throw new IllegalStateException("Ability is not on the played");
        return info;
    }
    
    
    public void resetPlayInformation() {
        info.setValue(null);
    }
    
    
    @Override
    public String toString() {
        return getAbility().toString();
    }
}
