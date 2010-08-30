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
import net.slightlymagic.laterna.magica.action.play.PlayAction;
import net.slightlymagic.laterna.magica.characteristics.MagicColor;
import net.slightlymagic.laterna.magica.characteristics.SuperType;
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
import net.slightlymagic.laterna.magica.gui.mana.symbol.ManaSymbolChooser;
import net.slightlymagic.laterna.magica.gui.util.ListChooser;
import net.slightlymagic.laterna.magica.mana.Mana;
import net.slightlymagic.laterna.magica.mana.ManaSequence;
import net.slightlymagic.laterna.magica.mana.ManaSymbol;
import net.slightlymagic.laterna.magica.mana.impl.ManaSequenceImpl;
import net.slightlymagic.laterna.magica.player.Player;
import net.slightlymagic.laterna.magica.player.impl.AbstractMagicActor;
import net.slightlymagic.laterna.magica.turnStructure.PhaseStructure.Step;

import org.jetlang.channels.Channel;
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
public class GuiMagicActor extends AbstractMagicActor {
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
    
    private static <T> T getValue(Fiber f, Channel<T> ch, GuiActor a) {
        a.start();
        log.trace("Waiting for result...");
        T result = Parallel.getValue(f, ch);
        log.trace("received!");
        a.dispose();
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
        
        return new ManaSequenceImpl(symbols);
    }
    
    private static final Predicate<MagicObject> isSnow = card(has(SuperType.SNOW));
    
    public Set<Mana> getManaToPay(ManaCost cost) {
        //transform the mana sequence into amounts of different mana qualities
        //TODO Make more general mana payment code
        //this approach is easy but does not account for special restrictions
        
        //WUBRGSX
        int[] amounts = new int[7];
        for(ManaSymbol sym:cost.getCost().getSymbols()) {
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
        
        //make a source and destination set
        Set<Mana> pool = new HashSet<Mana>(getPlayer().getManaPool().getPool()), result = new HashSet<Mana>();
        
        //fill the destination set from the source. if no appropriate mana is found, return null immediately
        main: while(amounts[5] > 0) {
            for(Mana mana:pool) {
                if(isSnow.apply(mana.getSource())) {
                    pool.remove(mana);
                    result.add(mana);
                    amounts[5]--;
                    break main;
                }
                continue main;
            }
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
    
    @Override
    public void setDefendingPlayers() {
        //TODO implement
        //this method has to make a default choice
    }
    
    @Override
    public void declareAttackers() {
        //use getValue to block until assignments are finished
        getValue(channels.fiber, channels.actions, new AttackerActor(this));
    }
    
    @Override
    public void declareBlockers() {
        //use getValue to block until assignments are finished
        getValue(channels.fiber, channels.actions, new BlockerActor(this));
    }
    
    @Override
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
    
    @Override
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
    
    @Override
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
    
    @Override
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
    
    @Override
    public Blocker getBlockerToAssignDamage(Collection<? extends Blocker> blockers) {
        if(blockers.size() == 1) return blockers.iterator().next();
        for(Blocker b:blockers) {
            //assigning damage for only one attacker is trivial
            if(b.getAttackers().size() == 1) return b;
        }
        
        return getValue(channels.fiber, channels.blockers, new ChooseBlockerActor(this, blockers));
    }
    
    @Override
    public void assignDamage(Blocker blocker) {
        if(blocker.getAttackers().size() == 1) {
            blocker.getAttackers().values().iterator().next().setBlockerAssignedDamage(getAmmount(blocker));
        } else {
            //use getValue to block until assignments are finished
            getValue(channels.fiber, channels.actions, new AssignBlockerDamageActor(this, blocker));
        }
    }
}
