/**
 * ManaPoolPanel.java
 * 
 * Created on 14.04.2010
 */

package net.slightlymagic.laterna.magica.gui.mana;


import static net.slightlymagic.laterna.magica.characteristic.MagicColor.*;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.slightlymagic.laterna.magica.characteristic.MagicColor;
import net.slightlymagic.laterna.magica.event.ManaPoolListener;
import net.slightlymagic.laterna.magica.gui.DisposeSupport;
import net.slightlymagic.laterna.magica.gui.mana.symbol.ManaSymbolLabel;
import net.slightlymagic.laterna.magica.mana.Mana;
import net.slightlymagic.laterna.magica.mana.ManaPool;
import net.slightlymagic.laterna.magica.mana.impl.ManaPoolEvent;

import org.jetlang.core.Disposable;


/**
 * The class ManaPoolPanel.
 * 
 * @version V0.0 14.04.2010
 * @author Clemens Koza
 */
public class ManaPoolPanel extends JPanel implements Disposable {
    private static final long      serialVersionUID = -5407083116641530085L;
    
    protected final DisposeSupport d                = new DisposeSupport();
    
    private ManaPool               pool;
    
    private JLabel[]               labels;
    
    public ManaPoolPanel(ManaPool pool) {
        super(null);
        this.pool = pool;
        setupComponents();
        d.add(new Updater());
        update();
    }
    
    public void dispose() {
        d.dispose();
    }
    
    public ManaPool getPool() {
        return pool;
    }
    
    protected void setupComponents() {
        setLayout(new GridLayout(3, 0, 3, 3));
        setBorder(BorderFactory.createEmptyBorder(1, 2, 2, 2));
        
        //these are sorted for layout, the attribute is sorted WUBRGX
//        MagicColor[] c = {WHITE, BLUE, BLACK, RED, GREEN, null}; //1x6
//        MagicColor[] c = {WHITE, BLACK, GREEN, BLUE, RED, null}; //2x3
        MagicColor[] c = {WHITE, RED, BLUE, GREEN, BLACK, null}; //3x2
        
        labels = new JLabel[c.length];
        for(int i = 0; i < labels.length; i++) {
            JLabel l = new ManaSymbolLabel(c[i]);
            labels[c[i] == null? c.length - 1:c[i].ordinal()] = l;
            add(l);
        }
    }
    
    protected void update() {
        //Count the mana
        int[] counts = new int[MagicColor.values().length + 1];
        for(Mana m:getPool().getPool())
            counts[m.getColor() == null? MagicColor.values().length:m.getColor().ordinal()]++;
        
        //Update the labels
        for(int i = 0; i < counts.length; i++)
            labels[i].setText("" + counts[i]);
    }
    
    //TODO change to PropertyChangeListener
    private class Updater implements ManaPoolListener, Disposable {
        public Updater() {
            //TODO use a PCL
            pool.addManaPoolListener(this);
        }
        
        public void dispose() {
            pool.removeManaPoolListener(this);
        }
        
        public void ManaAdded(ManaPoolEvent ev) {
            assert ev.getPool() == getPool();
            update();
        }
        
        public void ManaRemoved(ManaPoolEvent ev) {
            assert ev.getPool() == getPool();
            update();
        }
    }
}
