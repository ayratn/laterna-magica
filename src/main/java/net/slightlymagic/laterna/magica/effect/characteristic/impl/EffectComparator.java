/**
 * EffectComparator.java
 * 
 * Created on 10.10.2009
 */

package net.slightlymagic.laterna.magica.effect.characteristic.impl;


import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Comparator;

import net.slightlymagic.laterna.magica.effect.ContinuousEffect;


/**
 * A comparator that sorts effects by the layer they are applied in.
 * 
 * @version V0.0 10.10.2009
 * @author Clemens Koza
 */
public class EffectComparator implements Comparator<ContinuousEffect>, Serializable {
    private static final long            serialVersionUID = -5745432958606308785L;
    
    public static final EffectComparator INSTANCE         = new EffectComparator();
    
    private EffectComparator() {}
    
    public int compare(ContinuousEffect o1, ContinuousEffect o2) {
        //TODO dependencies (CR 612.7)
        //Layer compare
        int result = o1.getLayer().compareTo(o2.getLayer());
        //Timestamp compare
        if(result == 0) result = o1.compareTo(o2);
        return result;
    }
    
    private Object readResolve() throws ObjectStreamException {
        return INSTANCE;
    }
}
