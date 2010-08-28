/**
 * SymbolTextCreator.java
 * 
 * Created on 28.10.2009
 */

package net.slightlymagic.laterna.magica.gui.util;


import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;


/**
 * The class SymbolTextCreator.
 * 
 * @version V0.0 28.10.2009
 * @author Clemens Koza
 */
public class SymbolTextCreator {
    public static JTextPane formatRulesText(String text, int size) {
        return formatRulesText(text, new JTextPane(), size);
    }
    
    public static JTextPane formatRulesText(String text, JTextPane p, int size) {
        try {
            ImageCache i = ImageCache.getInstance();
            
            StyledDocument d = p.getStyledDocument();
            addStyles(d, size);
            
            boolean reminder = false;
            Matcher m = Pattern.compile("\\(|\\)|(\\{.*?\\})").matcher(text);
            StringBuffer sb = new StringBuffer();
            while(m.find()) {
                //store the part before the match
                m.appendReplacement(sb, "");
                //append the string as rules text
                d.insertString(d.getLength(), sb.toString(), d.getStyle(reminder? "reminder":"rules"));
                //clear the string buffer content
                sb.setLength(0);
                
                if(m.group().equals("(")) {
                    reminder = true;
                    d.insertString(d.getLength(), "(", d.getStyle("reminder"));
                } else if(m.group().equals(")")) {
                    d.insertString(d.getLength(), ")", d.getStyle("reminder"));
                    reminder = false;
                } else {
                    //append symbol
                    String symbol = m.group(1);
                    URI uri;
                    if(size < 15) uri = i.getSymbolURI(symbol, "small");
                    else if(size < 25) uri = i.getSymbolURI(symbol, "medium");
                    else uri = i.getSymbolURI(symbol, "large");
                    BufferedImage im = i.getImage(i.setSize(uri, size, size));
                    if(im != null) {
                        p.setCaretPosition(d.getLength());
                        p.insertIcon(new ImageIcon(im, symbol));
                    } else d.insertString(d.getLength(), symbol, d.getStyle(reminder? "reminder":"rules"));
                }
            }
            //store the tail
            m.appendTail(sb);
            //append the tail as rules text
            d.insertString(d.getLength(), sb.toString(), d.getStyle("rules"));
        } catch(BadLocationException ex) {
            ex.printStackTrace();
        }
        
        p.setOpaque(false);
        
        return p;
    }
    
    public static JTextPane formatFlavorText(String text, int size) {
        return formatFlavorText(text, new JTextPane(), size);
    }
    
    public static JTextPane formatFlavorText(String text, JTextPane p, int size) {
        StyledDocument d = p.getStyledDocument();
        addStyles(d, size);
        
        try {
            d.insertString(d.getLength(), text, d.getStyle("flavor"));
        } catch(BadLocationException ex) {
            ex.printStackTrace();
        }
        
        p.setOpaque(false);
        
        return p;
    }
    
    private static void addStyles(StyledDocument doc, int size) {
        Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        StyleConstants.setFontSize(def, size);
        
        Style rules = doc.addStyle("rules", def);
        
        Style reminder = doc.addStyle("reminder", rules);
        StyleConstants.setItalic(reminder, true);
        
        Style flavor = doc.addStyle("flavor", rules);
        StyleConstants.setItalic(flavor, true);
    }
}
