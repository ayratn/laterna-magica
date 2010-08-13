/**
 * SuperType.java
 * 
 * Created on 17.07.2009
 */

package net.slightlymagic.laterna.magica.characteristics;


import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * The class SuperType.
 * 
 * @version V0.0 17.07.2009
 * @author Clemens Koza
 */
public enum SuperType {
    /**
     * {@magic.ruleRef 20100716/R2044c}
     */
    BASIC,
    /**
     * {@magic.ruleRef 20100716/R2044d}, {@magic.ruleRef 20100716/R7045k}
     */
    LEGENDARY,
    /**
     * {@magic.ruleRef 20100716/R2044g}, {@magic.ruleRef 20100716/R7045v}
     */
    ONGOING,
    /**
     * {@magic.ruleRef 20100716/R2044f}
     */
    SNOW,
    /**
     * {@magic.ruleRef 20100716/R2044e}, {@magic.ruleRef 20100716/R7045m}
     */
    WORLD;
    
    private final String name;
    
    private SuperType() {
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
