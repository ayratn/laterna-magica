/**
 * DisposeSupport.java
 * 
 * Created on 31.08.2010
 */

package net.slightlymagic.laterna.magica.gui;


import static java.util.Collections.*;

import java.util.ArrayList;
import java.util.List;

import org.jetlang.core.Disposable;


/**
 * The class DisposeSupport.
 * 
 * @version V0.0 31.08.2010
 * @author Clemens Koza
 */
public class DisposeSupport implements Disposable {
    private final List<Disposable> disposables = new ArrayList<Disposable>();
    
    public void add(Disposable d) {
        if(d != null) disposables.add(d);
    }
    
    /**
     * Removes the disposable from the list, and disposes it if successful
     */
    public void remove(Disposable d) {
        if(disposables.remove(d)) d.dispose();
    }
    
    /**
     * Disposes all disposables in reverse order
     */
    public void dispose() {
        reverse(disposables);
        for(Disposable d:disposables)
            d.dispose();
        disposables.clear();
    }
    
}
