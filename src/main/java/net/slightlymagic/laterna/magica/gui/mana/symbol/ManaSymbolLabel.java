/**
 * ManaSymbolLabel.java
 * 
 * Created on 24.04.2010
 */

package net.slightlymagic.laterna.magica.gui.mana.symbol;


import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import net.slightlymagic.laterna.magica.characteristics.MagicColor;
import net.slightlymagic.laterna.magica.gui.util.ImageCache;
import net.slightlymagic.laterna.magica.mana.ManaSymbol;
import net.slightlymagic.laterna.magica.mana.impl.ColoredManaSymbol;
import net.slightlymagic.laterna.magica.mana.impl.VariableManaSymbol;


/**
 * The class ManaSymbolLabel.
 * 
 * @version V0.0 24.04.2010
 * @author Clemens Koza
 */
public class ManaSymbolLabel extends JLabel {
    private static final long serialVersionUID = -8416370078811886479L;
    
    private ManaSymbol        symbol;
    
    public ManaSymbolLabel(MagicColor color) {
        this(color == null? new VariableManaSymbol('X'):new ColoredManaSymbol(color));
    }
    
    public ManaSymbolLabel(ManaSymbol symbol) {
        setSymbol(symbol);
    }
    
    public void setSymbol(ManaSymbol symbol) {
        this.symbol = symbol;
        Image im = ImageCache.getInstance().getSymbol(symbol);
        if(im == null) {
            setIcon(null);
            setText(symbol.toString());
        } else {
            setIcon(new ImageIcon(im));
            setText("");
        }
    }
    
    public ManaSymbol getSymbol() {
        return symbol;
    }
}
