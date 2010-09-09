/**
 * ReplacementEngine.java
 * 
 * Created on 22.03.2010
 */

package net.slightlymagic.laterna.magica.effect.replacement;


import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.ability.Ability;
import net.slightlymagic.laterna.magica.ability.TriggeredAbility;
import net.slightlymagic.laterna.magica.action.play.TriggerAction;
import net.slightlymagic.laterna.magica.characteristic.ObjectCharacteristics;
import net.slightlymagic.laterna.magica.effect.replacement.ReplacementEffect.ReplacementType;
import net.slightlymagic.laterna.magica.impl.AbstractGameContent;
import net.slightlymagic.laterna.magica.player.Player;
import net.slightlymagic.laterna.magica.util.MagicaCollections;
import net.slightlymagic.laterna.magica.util.MagicaUtils;


/**
 * The class ReplacementEngine. The replacement engine makes sure that a replaceable event is executed in the right
 * manner, i.e. replaced if necessary.
 * 
 * A {@link ReplaceableEvent} executes itself by calling {@link #execute(ReplaceableEvent)} with itself as
 * parameter, which in turn calls {@link ReplaceableEvent#execute0()} on the resulting event, whatever event that
 * may be.
 * 
 * @version V0.0 22.03.2010
 * @author Clemens Koza
 */
public class ReplacementEngine extends AbstractGameContent {
    private Set<ReplacementEffect> effects;
    
    /**
     * Creates a replacement engine for the given game.
     */
    public ReplacementEngine(Game game) {
        super(game);
        effects = MagicaCollections.editableSet(getGame(), new HashSet<ReplacementEffect>());
    }
    
    /**
     * Adds a {@link ReplacementEffect} to the engine. This must be called when the ability starts to apply, e.g.
     * the card its printed on enters the battlefield.
     */
    public void add(ReplacementEffect effect) {
        effects.add(effect);
    }
    
    /**
     * Removes a {@link ReplacementEffect} from the engine. This must be called when the ability ends to apply,
     * e.g. the card its printed on leaves the battlefield.
     */
    public void remove(ReplacementEffect effect) {
        effects.remove(effect);
    }
    
    /**
     * Replaces the replacement effect by applying all applicable replacement effects in the right order. If this
     * results in an actual event, it is executed and abilities trigger from it.
     * 
     * @see {@magic.ruleRef 20100716/R616}
     */
    public boolean execute(ReplaceableEvent event) {
        event = replace(event);
        if(event == null) return false;
        
        trigger(event);
        return event.execute0();
    }
    
    private ReplaceableEvent replace(ReplaceableEvent event) {
        Set<ReplacementEffect> effects = new HashSet<ReplacementEffect>(this.effects);
        Set<ReplacementEffect> choices = new HashSet<ReplacementEffect>();
        ReplacementType choicesType;
        while(true) {
            choices.clear();
            choicesType = ReplacementType.OTHER;
            for(ReplacementEffect ef:effects) {
                //only consider choices that could be applied to the current event
                if(!ef.apply(event)) continue;
                if(choicesType.ordinal() > ef.getType().ordinal()) {
                    //the current type of choices is less specific than the new effect's type
                    //only use effects that are at least as specific.
                    choicesType = ef.getType();
                    choices.clear();
                }
                //if the choice is more specific, don't add it
                //can't be less specific because of the preceding if
                if(choicesType == ef.getType()) choices.add(ef);
            }
            //break when all effects were applied
            if(choices.isEmpty()) break;
            
            //Let the player choose an effect (if there is more than one)
            ReplacementEffect ef;
            if(choices.size() == 1) ef = choices.iterator().next();
            else ef = event.getAffectedPlayer().getActor().getReplacementEffect(event, choices);
            
            assert ef.apply(event);
            
            //remove the effect from the list of effects to consider
            effects.remove(ef);
            event = ef.replace(event);
        }
        return event;
    }
    
    private void trigger(ReplaceableEvent event) {
        Set<Ability> ab = new HashSet<Ability>();
        Collection<TriggerAction> triggeredAbilities = getGame().getTriggeredAbilities();
        for(MagicObject o:MagicaUtils.getAllCards(getGame())) {
            Player p = MagicaUtils.you(o);
            
            for(ObjectCharacteristics c:o.getCharacteristics()) {
                boolean b = c.getAbilityCharacteristic().isAdding(ab);
                assert b;
                for(Ability a:ab)
                    if(a instanceof TriggeredAbility) {
                        TriggeredAbility tr = ((TriggeredAbility) a);
                        TriggerAction ac = new TriggerAction(p, o, tr, event);
                        if(tr.triggersFrom(ac)) triggeredAbilities.add(ac);
                    }
            }
        }
    }
}
