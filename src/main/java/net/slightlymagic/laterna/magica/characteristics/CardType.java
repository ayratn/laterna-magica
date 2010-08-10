/**
 * CardType.java
 * 
 * Created on 17.07.2009
 */

package net.slightlymagic.laterna.magica.characteristics;


import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * <p>
 * The class CardType.
 * </p>
 * 
 * @version V0.0 17.07.2009
 * @author Clemens Koza
 */
public enum CardType {
    /**
     * {@magic.ruleRef 301 CR 301}
     */
    ARTIFACT,
    /**
     * {@magic.ruleRef 302 CR 302}
     */
    CREATURE,
    /**
     * {@magic.ruleRef 303 CR 303}
     */
    ENCHANTMENT,
    /**
     * {@magic.ruleRef 304 CR 304}
     */
    INSTANT,
    /**
     * {@magic.ruleRef 305 CR 305}
     */
    LAND,
    /**
     * {@magic.ruleRef 306 CR 306}
     */
    PLANESWALKER,
    /**
     * {@magic.ruleRef 307 CR 307}
     */
    SORCERY,
    /**
     * {@magic.ruleRef 308 CR 308}
     */
    TRIBAL;
    
    private final String name;
    
    private CardType() {
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
    
    /**
     * <p>
     * Returns the normalized type.
     * </p>
     * <ul>
     * <li>For {@link #TRIBAL}, returns {@link #CREATURE}.</li>
     * <li>For {@link #INSTANT}, returns {@link #SORCERY}.</li>
     * <li>Other types are returned without modification.</li>
     * </ul>
     */
    public CardType normalize() {
        if(this == TRIBAL) return CREATURE;
        if(this == INSTANT) return SORCERY;
        return this;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
