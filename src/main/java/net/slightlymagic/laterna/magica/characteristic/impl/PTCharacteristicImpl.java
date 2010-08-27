/**
 * PTCharacteristicImpl.java
 * 
 * Created on 02.09.2009
 */

package net.slightlymagic.laterna.magica.characteristic.impl;


import static net.slightlymagic.laterna.magica.characteristics.Characteristics.*;
import net.slightlymagic.laterna.magica.characteristic.PTCharacteristic;
import net.slightlymagic.laterna.magica.characteristics.CardType;
import net.slightlymagic.laterna.magica.edit.Edit;
import net.slightlymagic.laterna.magica.effect.characteristic.PTChangingEffect;
import net.slightlymagic.laterna.magica.effect.characteristic.PTEffect;
import net.slightlymagic.laterna.magica.effect.characteristic.PTSettingEffect;
import net.slightlymagic.laterna.magica.effect.characteristic.PTSwitchingEffect;


/**
 * The class PTCharacteristicImpl.
 * 
 * @version V0.0 02.09.2009
 * @author Clemens Koza
 */
public class PTCharacteristicImpl extends AbstractCharacteristic<PTEffect> implements PTCharacteristic {
    private String        name;
    protected RefreshEdit edit;
    protected int         p, t;
    
    public PTCharacteristicImpl(ObjectCharacteristicsImpl characteristics, String name) {
        super(characteristics, POWER_TOUGHNESS);
        this.name = name;
    }
    
    @Override
    public ObjectCharacteristicsImpl getCharacteristics() {
        return (ObjectCharacteristicsImpl) super.getCharacteristics();
    }
    
    @Override
    protected void reset() {
        edit = new RefreshEdit();
        if(getFirst() != null) applyEffect(getFirst());
    }
    
    @Override
    protected void applyEffect(PTEffect ef) {
        edit.applyEffect(ef);
    }
    
    @Override
    protected void end() {
        edit.execute();
        edit = null;
    }
    
    public int getPower() {
        int p;
        if(edit != null) {
            p = edit.newP;
        } else {
            getCharacteristics().refresh();
            p = this.p;
        }
        if(!getCharacteristics().hasType(CardType.CREATURE)) p = 0;
        return p;
    }
    
    public int getToughness() {
        int t;
        if(edit != null) {
            t = edit.newT;
        } else {
            getCharacteristics().refresh();
            t = this.t;
        }
        if(!getCharacteristics().hasType(CardType.CREATURE)) t = 0;
        return t;
    }
    
    private class RefreshEdit extends Edit {
        private static final long serialVersionUID = 5056088603352168050L;
        
        private int               oldP, newP;
        private int               oldT, newT;
        
        public RefreshEdit() {
            super(PTCharacteristicImpl.this.getGame());
        }
        
        public void applyEffect(PTEffect ef) {
            assert ef.getCharacteristic() == getCharacteristic();
            log.trace("Applying " + ef);
            if(ef instanceof PTSettingEffect) {
                PTSettingEffect st = (PTSettingEffect) ef;
                if(st.affectsPower()) newP = st.getPower();
                if(st.affectsToughness()) newT = st.getToughness();
            } else if(ef instanceof PTChangingEffect) {
                PTChangingEffect ch = (PTChangingEffect) ef;
                newP += ch.getPower();
                newT += ch.getToughness();
            } else if(ef instanceof PTSwitchingEffect) {
                //PTSwitchingEffect sw = (PTSwitchingEffect) ef;
                int i = newP;
                newP = newT;
                newT = i;
            } else throw new AssertionError(ef);
        }
        
        @Override
        protected void execute() {
            oldP = p;
            oldT = t;
            p = newP;
            t = newT;
            s.firePropertyChange(getCharacteristic().name() + ".p", oldP, newP);
            s.firePropertyChange(getCharacteristic().name() + ".t", oldT, newT);
        }
        
        @Override
        protected void rollback() {
            p = oldP;
            t = oldT;
            s.firePropertyChange(getCharacteristic().name() + ".p", newP, oldP);
            s.firePropertyChange(getCharacteristic().name() + ".t", newT, oldT);
        }
        
        @Override
        public String toString() {
            return "Refresh " + name;
        }
    }
}
