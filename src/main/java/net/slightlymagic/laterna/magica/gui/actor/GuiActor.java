/**
 * GuiActor.java
 * 
 * Created on 23.08.2010
 */

package net.slightlymagic.laterna.magica.gui.actor;


import static java.util.Collections.*;

import java.util.ArrayList;
import java.util.List;

import org.jetlang.core.Disposable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The class GuiActor.
 * 
 * @version V0.0 23.08.2010
 * @author Clemens Koza
 */
public abstract class GuiActor implements Disposable {
    protected final Logger           log         = LoggerFactory.getLogger(getClass());
    
    protected final GuiMagicActor    actor;
    protected final List<Disposable> disposables = new ArrayList<Disposable>();
    
    
    public GuiActor(GuiMagicActor actor) {
        this.actor = actor;
    }
    
    /**
     * This method should store all generated {@link Disposable}s in {@link #disposables}, so that they can
     * automatically handled in {@link #dispose()}.
     */
    public abstract void start();
    
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
