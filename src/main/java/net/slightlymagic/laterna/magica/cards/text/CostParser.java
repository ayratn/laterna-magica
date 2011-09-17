/**
 * CostParser.java
 * 
 * Created on 28.04.2010
 */

package net.slightlymagic.laterna.magica.cards.text;


import net.slightlymagic.laterna.magica.action.play.ActivateAction;
import net.slightlymagic.laterna.magica.action.play.PlayInformation;

import com.google.common.base.Function;


/**
 * The class CostParser.
 * 
 * @version V0.0 28.04.2010
 * @author Clemens Koza
 */
public interface CostParser {
    public Function<? super ActivateAction, ? extends PlayInformation> parseCost(String text);
}
