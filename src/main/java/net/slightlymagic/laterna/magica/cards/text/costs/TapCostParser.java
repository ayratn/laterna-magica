/**
 * TapCostParser.java
 * 
 * Created on 24.07.2010
 */

package net.slightlymagic.laterna.magica.cards.text.costs;


import net.slightlymagic.laterna.magica.action.play.ActivateAction;
import net.slightlymagic.laterna.magica.action.play.PlayAction;
import net.slightlymagic.laterna.magica.action.play.PlayInformation;
import net.slightlymagic.laterna.magica.cards.text.CostParsers.CostParser;
import net.slightlymagic.laterna.magica.cost.impl.TapSymbolInformation;
import net.slightlymagic.laterna.magica.util.FactoryFunction;

import com.google.common.base.Function;


public class TapCostParser implements CostParser {
    public Function<? super ActivateAction, ? extends PlayInformation> parseCost(String text) {
        if("{T}".equals(text)) return FactoryFunction.getInstance(PlayAction.class, TapSymbolInformation.class);
        return null;
    }
}
