/**
 * UntapCostParser.java
 * 
 * Created on 24.07.2010
 */

package net.slightlymagic.laterna.magica.cards.text.costs;


import net.slightlymagic.laterna.magica.action.play.ActivateAction;
import net.slightlymagic.laterna.magica.action.play.PlayAction;
import net.slightlymagic.laterna.magica.action.play.PlayInformation;
import net.slightlymagic.laterna.magica.cards.text.CostParser;
import net.slightlymagic.laterna.magica.cost.impl.UntapSymbolInformation;
import net.slightlymagic.laterna.magica.util.FactoryFunction;

import com.google.common.base.Function;


public class UntapCostParser implements CostParser {
    public Function<? super ActivateAction, ? extends PlayInformation> parseCost(String text) {
        if("{Q}".equals(text)) return FactoryFunction.getInstance(PlayAction.class, UntapSymbolInformation.class);
        return null;
    }
}
