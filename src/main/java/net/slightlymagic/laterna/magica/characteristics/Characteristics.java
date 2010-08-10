/**
 * Characteristics.java
 * 
 * Created on 07.09.2009
 */

package net.slightlymagic.laterna.magica.characteristics;


import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * <p>
 * The class Characteristics. {@magic.ruleRef 109.3 CR 109.3}
 * </p>
 * 
 * @version V0.0 07.09.2009
 * @author Clemens Koza
 */
public enum Characteristics {
    NAME, MANA_COST, COLOR, SUBTYPE, CARD_TYPE, SUPERTYPE, EXPANSION_SYMBOL, RULES_TEXT, ABILITIES,
    //these are technically distinct, but power and toughness are handled together.
    POWER_TOUGHNESS,
    LOYALTY;
    
    private final String name;
    
    private Characteristics() {
        String s = super.toString().toLowerCase();
        Matcher m = Pattern.compile("(^|_)(.)").matcher(s);
        StringBuffer result = new StringBuffer();
        while(m.find()) {
            boolean b = m.group(1).length() != 0;
            m.appendReplacement(result, Matcher.quoteReplacement((b? " ":"") + m.group(2).toUpperCase()));
        }
        m.appendTail(result);
        name = result.toString();
    }
    
    @Override
    public String toString() {
        return name;
    }
}
