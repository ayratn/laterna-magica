/**
 * AbstractPlayInformation.java
 * 
 * Created on 22.04.2010
 */

package net.slightlymagic.laterna.magica.action.play.impl;


import java.util.ArrayList;
import java.util.List;

import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.ability.AbilityObject;
import net.slightlymagic.laterna.magica.action.AbstractGameAction;
import net.slightlymagic.laterna.magica.action.CompoundActionImpl;
import net.slightlymagic.laterna.magica.action.GameAction;
import net.slightlymagic.laterna.magica.action.play.PlayAction;
import net.slightlymagic.laterna.magica.action.play.PlayInformation;
import net.slightlymagic.laterna.magica.characteristic.ObjectCharacteristics;
import net.slightlymagic.laterna.magica.characteristics.CardType;
import net.slightlymagic.laterna.magica.impl.AbstractGameContent;
import net.slightlymagic.laterna.magica.zone.Zone;



/**
 * The class AbstractPlayInformation. This class implements a play information that works for spells/abilities
 * without any choices or targets but with effect and cost.
 * 
 * @version V0.0 22.04.2010
 * @author Clemens Koza
 */
public class AbstractPlayInformation extends AbstractGameContent implements PlayInformation {
    private PlayAction            action;
    //the play information is composed of delegate informations, which are all notified of the various calls to
    //this play information. They may return null or non-null values for cost and effect. All non-null values are
    //appended to create this information's effect
    private List<PlayInformation> delegates;
    //is true if this information is not a delegate of another information. This determines if the card-moving
    //effect is added
    private boolean               root = true;
    
    public AbstractPlayInformation(PlayAction action) {
        this(null, action);
    }
    
    public AbstractPlayInformation(List<PlayInformation> delegates, PlayAction action) {
        super(action.getGame());
        this.delegates = delegates;
        this.action = action;
    }
    
    protected List<PlayInformation> getDelegates() {
        return delegates;
    }
    
    public PlayAction getAction() {
        return action;
    }
    
    public MagicObject getObject() {
        return action.getObject();
    }
    
    public void makeChoices() {
        if(delegates != null) for(PlayInformation delegate:delegates) {
            delegate.makeChoices();
        }
    }
    
    public void chooseTargets() {
        if(delegates != null) for(PlayInformation delegate:delegates) {
            delegate.chooseTargets();
        }
    }
    
    public void distributeEffect() {
        if(delegates != null) for(PlayInformation delegate:delegates) {
            delegate.distributeEffect();
        }
    }
    
    public GameAction getCost() {
        List<GameAction> actions = new ArrayList<GameAction>();
        if(delegates != null) for(PlayInformation delegate:delegates) {
            GameAction cost = delegate.getCost();
            if(cost != null) actions.add(cost);
        }
        return new CompoundActionImpl(actions);
    }
    
    public GameAction getEffect() {
        List<GameAction> actions = new ArrayList<GameAction>();
        if(delegates != null) for(PlayInformation delegate:delegates) {
            GameAction cost = delegate.getEffect();
            if(cost != null) actions.add(cost);
        }
        
        if(root) actions.add(new AbstractGameAction(getGame()) {
            @Override
            public boolean execute() {
                MagicObject o = getObject();
                ObjectCharacteristics c = o.getCharacteristics().get(0);
                Zone z;
                if(o instanceof AbilityObject) z = null;
                else if(c.hasType(CardType.INSTANT) || c.hasType(CardType.SORCERY)) z = o.getOwner().getGraveyard();
                else z = o.getGame().getBattlefield();
                o.setZone(z);
                return true;
            }
        });
        
        return new CompoundActionImpl(actions);
    }
}
