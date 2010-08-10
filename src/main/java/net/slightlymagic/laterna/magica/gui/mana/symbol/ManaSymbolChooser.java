/**
 * ManaSymbolChooser.java
 * 
 * Created on 24.04.2010
 */

package net.slightlymagic.laterna.magica.gui.mana.symbol;


import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import java.io.Serializable;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.slightlymagic.laterna.magica.LaternaMagica;
import net.slightlymagic.laterna.magica.mana.ManaSymbol;
import net.slightlymagic.laterna.magica.mana.impl.ManaFactoryImpl;
import net.slightlymagic.laterna.magica.mana.impl.NumeralManaSymbol;



/**
 * The class ManaSymbolChooser.
 * 
 * @version V0.0 24.04.2010
 * @author Clemens Koza
 */
public class ManaSymbolChooser extends JPanel {
    private static final long serialVersionUID = -8874747444802143417L;
    
    public static void main(String[] args) throws IOException {
        LaternaMagica.init();
        
        JFrame jf = new JFrame();
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        jf.add(new ManaSymbolChooser(ManaFactoryImpl.INSTANCE.parseSymbol("{X}")));
        
        jf.pack();
        jf.setVisible(true);
    }
    
    private ManaSymbol      original;
    private Chooser         chooser;
    private ManaSymbolLabel current;
    
    public ManaSymbolChooser(ManaSymbol original) {
        super(new GridLayout(0, 1));
        this.original = original;
        switch(original.getType()) {
            case NUMERAL:
            case COLORED:
            case SNOW:
                chooser = new NoopChooser();
            break;
            case HYBRID:
                chooser = new HybridChooser();
            break;
            case VARIABLE:
                chooser = new VariableChooser();
            break;
            default:
                throw new AssertionError();
        }
        
        boolean noop = chooser instanceof NoopChooser;
        
        add(new ManaSymbolLabel(original));
        add(noop? new JLabel():new JButton(new AbstractAction("\u25B2") {
            private static final long serialVersionUID = -1970695649496929138L;
            
            public void actionPerformed(ActionEvent e) {
                next();
            }
        }));
        add(current = new ManaSymbolLabel(chooser.getCurrent()));
        add(noop? new JLabel():new JButton(new AbstractAction("\u25BC") {
            private static final long serialVersionUID = -778322351023512968L;
            
            public void actionPerformed(ActionEvent e) {
                previous();
            }
        }));
        
        if(!noop) current.addMouseWheelListener(new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent e) {
                int i = e.getWheelRotation();
                if(i < 0) next();
                if(i > 0) previous();
            }
        });
    }
    
    public ManaSymbol getOriginal() {
        return original;
    }
    
    public ManaSymbol getCurrent() {
        return chooser.getCurrent();
    }
    
    public void next() {
        chooser.next();
        current.setSymbol(chooser.getCurrent());
    }
    
    public void previous() {
        chooser.previous();
        current.setSymbol(chooser.getCurrent());
    }
    
    private interface Chooser extends Serializable {
        public ManaSymbol getCurrent();
        
        public void previous();
        
        public void next();
    }
    
    private class NoopChooser implements Chooser {
        private static final long serialVersionUID = 7178019253409482695L;
        
        public ManaSymbol getCurrent() {
            return original;
        }
        
        public void previous() {}
        
        public void next() {}
    }
    
    private class HybridChooser implements Chooser {
        private static final long serialVersionUID = -1117724452356904000L;
        
        private int               index, size = original.getParts().size();
        
        public ManaSymbol getCurrent() {
            return original.getParts().get(index);
        }
        
        public void previous() {
            index += size - 1;
            index %= size;
        }
        
        public void next() {
            index += 1;
            index %= size;
        }
    }
    
    private static class VariableChooser implements Chooser {
        private static final long serialVersionUID = 8208487009326567310L;
        
        private int               amount;
        private ManaSymbol        current;
        
        public ManaSymbol getCurrent() {
            if(current == null) current = new NumeralManaSymbol(amount);
            return current;
        }
        
        public void previous() {
            if(amount != 0) {
                amount--;
                current = null;
            }
        }
        
        public void next() {
            amount++;
            current = null;
        }
    }
}
