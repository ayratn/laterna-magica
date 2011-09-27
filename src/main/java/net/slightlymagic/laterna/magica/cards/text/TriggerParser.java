/**
 * TriggerParser.java
 * 
 * Created on 09.09.2010
 */

package net.slightlymagic.laterna.magica.cards.text;


import net.slightlymagic.laterna.magica.action.play.TriggerAction;

import com.google.common.base.Predicate;


/**
 * The class TriggerParser.
 * 
 * @version V0.0 09.09.2010
 * @author Clemens Koza
 */
public interface TriggerParser {
    public Predicate<? super TriggerAction> parseTrigger(String text);
}
