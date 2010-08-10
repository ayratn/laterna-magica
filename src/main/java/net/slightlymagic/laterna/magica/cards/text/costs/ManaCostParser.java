/**
 * ManaCostParser.java
 * 
 * Created on 24.07.2010
 */

package net.slightlymagic.laterna.magica.cards.text.costs;


import net.slightlymagic.laterna.magica.action.play.ActivateAction;
import net.slightlymagic.laterna.magica.action.play.PlayAction;
import net.slightlymagic.laterna.magica.action.play.PlayInformation;
import net.slightlymagic.laterna.magica.cards.text.CostParsers.CostParser;
import net.slightlymagic.laterna.magica.cost.impl.ManaCostInformation;
import net.slightlymagic.laterna.magica.mana.ManaSequence;
import net.slightlymagic.laterna.magica.mana.impl.ManaFactoryImpl;
import net.slightlymagic.laterna.magica.util.FactoryFunction;

import com.google.common.base.Function;


public class ManaCostParser implements CostParser {
    public Function<? super ActivateAction, ? extends PlayInformation> parseCost(String text) {
        try {
            ManaSequence cost = ManaFactoryImpl.INSTANCE.parseSequence(text);
            return FactoryFunction.getInstance(ManaSequence.class, cost, PlayAction.class,
                    ManaCostInformation.class);
        } catch(IllegalArgumentException ex) {
            return null;
        }
    }
}
