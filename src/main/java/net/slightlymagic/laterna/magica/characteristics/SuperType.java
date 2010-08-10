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
     * {@magic.ruleRef 204.4c CR 204.4c}
     */
    BASIC,
    /**
     * {@magic.ruleRef 204.4d CR 204.4d}, {@magic.ruleRef 704.5k CR 704.5k}
     */
    LEGENDARY,
    /**
     * {@magic.ruleRef 204.4e CR 204.4e}
     */
    SNOW,
    /**
     * {@magic.ruleRef 204.4f CR 204.4f}, {@magic.ruleRef 704.5m CR 704.5m}
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
