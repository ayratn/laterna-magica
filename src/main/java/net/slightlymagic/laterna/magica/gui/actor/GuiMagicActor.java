/**
 * GuiMagicActor.java
 * 
 * Created on 10.04.2010
 */

package net.slightlymagic.laterna.magica.gui.actor;


import static javax.swing.JOptionPane.*;
import static net.slightlymagic.laterna.magica.impl.CombatUtil.*;
import static net.slightlymagic.laterna.magica.util.MagicaPredicates.*;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.slightlymagic.concurrent.Parallel;
import net.slightlymagic.laterna.magica.Combat.Attacker;
import net.slightlymagic.laterna.magica.Combat.Blocker;
import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.action.CompoundActionImpl;
import net.slightlymagic.laterna.magica.action.GameAction;
import net.slightlymagic.laterna.magica.action.play.ActivateAction;
import net.slightlymagic.laterna.magica.action.play.PlayAction;
import net.slightlymagic.laterna.magica.characteristic.MagicColor;
import net.slightlymagic.laterna.magica.characteristic.SuperType;
import net.slightlymagic.laterna.magica.cost.ManaCost;
import net.slightlymagic.laterna.magica.effect.replacement.ReplaceableEvent;
import net.slightlymagic.laterna.magica.effect.replacement.ReplacementEffect;
import net.slightlymagic.laterna.magica.gui.Gui;
import net.slightlymagic.laterna.magica.gui.actor.actors.ActionActor;
import net.slightlymagic.laterna.magica.gui.actor.actors.AssignAttackerDamageActor;
import net.slightlymagic.laterna.magica.gui.actor.actors.AssignBlockerDamageActor;
import net.slightlymagic.laterna.magica.gui.actor.actors.AttackerActor;
import net.slightlymagic.laterna.magica.gui.actor.actors.BlockerActor;
import net.slightlymagic.laterna.magica.gui.actor.actors.ChooseAttackerActor;
import net.slightlymagic.laterna.magica.gui.actor.actors.ChooseBlockerActor;
import net.slightlymagic.laterna.magica.gui.actor.actors.ManaActor;
import net.slightlymagic.laterna.magica.gui.mana.symbol.ManaSymbolChooser;
import net.slightlymagic.laterna.magica.gui.util.ListChooser;
import net.slightlymagic.laterna.magica.mana.Mana;
import net.slightlymagic.laterna.magica.mana.ManaSequence;
import net.slightlymagic.laterna.magica.mana.ManaSymbol;
import net.slightlymagic.laterna.magica.player.ConcessionException;
import net.slightlymagic.laterna.magica.player.Player;
import net.slightlymagic.laterna.magica.player.impl.AbstractMagicActor;
import net.slightlymagic.laterna.magica.turnStructure.PhaseStructure.Step;

import org.jetlang.channels.Channel;
import org.jetlang.core.Disposable;
import org.jetlang.fibers.Fiber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;


/**
 * The class GuiMagicActor.
 * 
 * @version V0.0 10.04.2010
 * @author Clemens Koza
 */
public class GuiMagicActor extends AbstractMagicActor implements Disposable {
    private static final Logger log      = LoggerFactory.getLogger(GuiMagicActor.class);
    
    public final GuiChannels    channels = new GuiChannels();
    
    private final Gui           gui;
    
    public GuiMagicActor(Gui gui, Player player) {
        super(player);
        this.gui = gui;
        getGui().add(this);
    }
    
    public Gui getGui() {
        return gui;
    }
    
    public void dispose() {
        channels.dispose();
    }
    
    private boolean conceded;
    
    void setConceded() {
        this.conceded = true;
        getPlayer().loseGame();
    }
    
    private <T> T getValue(Fiber f, Channel<T> ch, GuiActor a) {
        a.start();
        log.trace("Waiting for result...");
        T result = Parallel.getValue(f, ch);
        log.trace("received!");
        a.dispose();
        if(conceded) {
            conceded = false;
            log.debug("throw concede");
            throw new ConcessionException();
        }
        return result;
    }
    
    public PlayAction getAction() {
        if(getGame().getStack().isEmpty()) {
            //skip a few steps to be quicker
            Step step = getGame().getPhaseStructure().getStep();
            if(step == Step.BEGINNING_DRAW) return null;
            if(step == Step.COMBAT_BEGINNING) return null;
            if(step == Step.COMBAT_DAMAGE) return null;
            if(step == Step.COMBAT_END) return null;
            if(step == Step.ENDING_CLEANUP) return null;
        }
        return getValue(channels.fiber, channels.actions, new ActionActor(this));
    }
    
    public ActivateAction activateManaAbility(GameAction cost) {
        int[] amounts = getManaCost(new int[7], cost);
        //enough mana to pay the cost
        if(getManaToPay(amounts) != null) return null;
        return (ActivateAction) getValue(channels.fiber, channels.actions, new ManaActor(this, amounts));
    }
    
    public ReplacementEffect getReplacementEffect(ReplaceableEvent event, Set<ReplacementEffect> effects) {
        ListChooser<ReplacementEffect> chooser = new ListChooser<ReplacementEffect>("Choose one",
                "Choose a replacement effect to apply", new ArrayList<ReplacementEffect>(effects));
        chooser.show();
        return chooser.getSelectedValue();
    }
    
    public ManaSequence resolveCost(ManaSequence cost) {
        if(cost.isResolved()) return cost;
        List<ManaSymbol> symbols = cost.getSymbols();
        List<ManaSymbolChooser> choosers = new ArrayList<ManaSymbolChooser>(symbols.size());
        
        //TODO equivalent variable costs must have equal values
        //Actually, variables are chosen before resolving, thus should already be fixed here
        JPanel center = new JPanel(new GridLayout(1, 0));
        for(ManaSymbol symbol:symbols) {
            ManaSymbolChooser c = new ManaSymbolChooser(symbol);
            choosers.add(c);
            center.add(c);
        }
        JPanel p = new JPanel(new BorderLayout());
        //TODO localize
        p.add(new JLabel("Choose how to pay the mana cost:"), BorderLayout.NORTH);
        p.add(center, BorderLayout.WEST);
        
        if(showConfirmDialog(null, p, "", OK_CANCEL_OPTION) != OK_OPTION) return null;
        
        symbols = new ArrayList<ManaSymbol>();
        for(ManaSymbolChooser chooser:choosers) {
            symbols.add(chooser.getCurrent());
        }
        
        return new ManaSequence(symbols);
    }
    
    private static int[] getManaCost(int[] amounts, GameAction cost) {
        if(cost instanceof ManaCost) {
            //is the cost a mana cost?
            for(ManaSymbol sym:((ManaCost) cost).getCost().getSymbols()) {
                switch(sym.getType()) {
                    case COLORED:
                        amounts[sym.getColor().ordinal()]++;
                    break;
                    case SNOW:
                        amounts[5]++;
                    break;
                    case NUMERAL:
                        amounts[6] += sym.getAmount();
                    break;
                    case HYBRID:
                    case VARIABLE:
                        throw new IllegalArgumentException("Cost contains hybrid or variable symbols");
                    default:
                        throw new AssertionError(sym.getType());
                }
            }
        } else if(cost instanceof CompoundActionImpl) {
            //does the cost contain a mana cost?
            List<? extends GameAction> actions = ((CompoundActionImpl) cost).getActions();
            for(GameAction action:actions)
                getManaCost(amounts, action);
            //otherwise...
        }
        
        return amounts;
    }
    
    private static final Predicate<MagicObject> isSnow = card(has(SuperType.SNOW));
    
    public Set<Mana> getManaToPay(ManaCost cost) {
        //transform the mana sequence into amounts of different mana qualities
        //TODO Make more general mana payment code
        //this approach is easy but does not account for special restrictions
        
        //WUBRGSX
        int[] amounts = getManaCost(new int[7], cost);
        
        return getManaToPay(amounts);
    }
    
    public Set<Mana> getManaToPay(int[] amounts) {
        //make a source and destination set
        Set<Mana> pool = new HashSet<Mana>(getPlayer().getManaPool().getPool()), result = new HashSet<Mana>();
        
        //fill the destination set from the source. if no appropriate mana is found, return null immediately
        main: while(amounts[5] > 0) {
            for(Mana mana:pool) {
                if(isSnow.apply(mana.getSource())) {
                    pool.remove(mana);
                    result.add(mana);
                    amounts[5]--;
                    continue main;
                }
            }
            return null;
        }
        for(int i = 0; i < 5; i++) {
            main: while(amounts[i] > 0) {
                for(Mana mana:pool) {
                    if(mana.getColor() == MagicColor.values()[i]) {
                        pool.remove(mana);
                        result.add(mana);
                        amounts[i]--;
                        continue main;
                    }
                }
                return null;
            }
        }
        main: while(amounts[6] > 0) {
            for(Mana mana:pool) {
                if(mana.getColor() == null) {
                    pool.remove(mana);
                    result.add(mana);
                    amounts[6]--;
                    continue main;
                }
            }
            if(!pool.isEmpty()) {
                Mana mana = pool.iterator().next();
                pool.remove(mana);
                result.add(mana);
                amounts[6]--;
                continue main;
            }
            return null;
        }
        
        //if gone until here, return the result set
        return result;
    }
    
    //TODO implement combat
    
    public void setDefendingPlayers() {
        //TODO implement
        //this method has to make a default choice
    }
    
    public void declareAttackers() {
        //use getValue to block until assignments are finished
        getValue(channels.fiber, channels.actions, new AttackerActor(this));
    }
    
    public void declareBlockers() {
        //use getValue to block until assignments are finished
        getValue(channels.fiber, channels.actions, new BlockerActor(this));
    }
    
    public void orderAttackers() {
        for(Blocker b:getGame().getCombat().getBlockers()) {
            if(b.getBlocker().getController() != getPlayer()) continue;
            
            List<Attacker> attackers = new ArrayList<Attacker>(b.getAttackers().keySet());
            if(attackers.size() > 1) {
                //TODO show a sort dialog
            }
            b.setDamageAssignmentOrder(attackers);
        }
    }
    
    public void orderBlockers() {
        for(Attacker a:getGame().getCombat().getAttackers()) {
            if(a.getAttacker().getController() != getPlayer()) continue;
            
            List<Blocker> blockers = new ArrayList<Blocker>(a.getBlockers().keySet());
            if(blockers.size() > 1) {
                //TODO show a sort dialog
            }
            a.setDamageAssignmentOrder(blockers);
        }
    }
    
    public Attacker getAttackerToAssignDamage(Collection<? extends Attacker> attackers) {
        if(attackers.size() == 1) return attackers.iterator().next();
        for(Attacker a:attackers) {
            //assigning damage for unblocked creatures is trivial
            if(a.getBlockers().isEmpty()) return a;
            //assigning damage for only one blocker without trample
            if(a.getBlockers().size() == 1 && !hasTrample(a)) return a;
        }
        
        return getValue(channels.fiber, channels.attackers, new ChooseAttackerActor(this, attackers));
    }
    
    public void assignDamage(Attacker attacker) {
        if(attacker.getBlockers().isEmpty()) {
            attacker.getAttackerAssignment().setAttackerAssignedDamage(getAmmount(attacker));
        } else if(attacker.getBlockers().size() == 1 && !hasTrample(attacker)) {
            attacker.getBlockers().values().iterator().next().setAttackerAssignedDamage(getAmmount(attacker));
        } else {
            //use getValue to block until assignments are finished
            getValue(channels.fiber, channels.actions, new AssignAttackerDamageActor(this, attacker));
        }
    }
    
    public Blocker getBlockerToAssignDamage(Collection<? extends Blocker> blockers) {
        if(blockers.size() == 1) return blockers.iterator().next();
        for(Blocker b:blockers) {
            //assigning damage for only one attacker is trivial
            if(b.getAttackers().size() == 1) return b;
        }
        
        return getValue(channels.fiber, channels.blockers, new ChooseBlockerActor(this, blockers));
    }
    
    public void assignDamage(Blocker blocker) {
        if(blocker.getAttackers().size() == 1) {
            blocker.getAttackers().values().iterator().next().setBlockerAssignedDamage(getAmmount(blocker));
        } else {
            //use getValue to block until assignments are finished
            getValue(channels.fiber, channels.actions, new AssignBlockerDamageActor(this, blocker));
        }
    }
}
