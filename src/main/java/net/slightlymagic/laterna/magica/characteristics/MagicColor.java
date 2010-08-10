/**
 * MagicColor.java
 * 
 * Created on 13.07.2009
 */

package net.slightlymagic.laterna.magica.characteristics;


import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * <p>
 * The class MagicColor.
 * </p>
 * 
 * @version V0.0 13.07.2009
 * @author Clemens Koza
 */
public enum MagicColor {
    WHITE('W'), BLUE('U'), BLACK('B'), RED('R'), GREEN('G');
    
    private final char   shortChar;
    private final String name;
    
    private MagicColor(char shortChar) {
        this.shortChar = Character.toUpperCase(shortChar);
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
     * Returns the uppercase character corresponding to the color
     * </p>
     */
    public char getShortChar() {
        return this.shortChar;
    }
    
    /**
     * <p>
     * Returns the color corresponding to the character, that is
     * <ul>
     * <li>{@link #WHITE} for 'w' or 'W'</li>
     * <li>{@link #BLUE} for 'u' or 'U'</li>
     * <li>{@link #BLACK} for 'b' or 'B'</li>
     * <li>{@link #RED} for 'r' or 'R'</li>
     * <li>{@link #GREEN} for 'g' or 'G'</li>
     * <li>{@code null} for any other</li>
     * </ul>
     * </p>
     */
    public static MagicColor getColorByChar(char c) {
        c = Character.toUpperCase(c);
        for(MagicColor color:MagicColor.values()) {
            char c2 = color.getShortChar();
            if(c == c2) return color;
        }
        return null;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
