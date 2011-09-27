/**
 * CostParsers.java
 * 
 * Created on 28.04.2010
 */

package net.slightlymagic.laterna.magica.cards.text;


import net.slightlymagic.laterna.magica.action.play.PlayAction;
import net.slightlymagic.laterna.magica.action.play.PlayInformation;

import com.google.common.base.Function;


/**
 * The class CostParsers.
 * 
 * @version V0.0 28.04.2010
 * @author Clemens Koza
 */
public interface EffectParser {
    public Function<? super PlayAction, ? extends PlayInformation> parseEffect(String text);
    
    /**
     * The result is undefined if the parser can't parse the text
     */
    public boolean isManaEffect(String text);
}
