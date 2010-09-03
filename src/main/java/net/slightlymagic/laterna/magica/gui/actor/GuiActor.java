/**
 * GuiActor.java
 * 
 * Created on 23.08.2010
 */

package net.slightlymagic.laterna.magica.gui.actor;


import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.border.Border;

import net.slightlymagic.laterna.magica.gui.DisposeSupport;
import net.slightlymagic.laterna.magica.gui.Gui;

import org.jetlang.core.Callback;
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
    protected final Logger         log = LoggerFactory.getLogger(getClass());
    
    protected final GuiMagicActor  actor;
    protected final DisposeSupport d   = new DisposeSupport();
    
    
    public GuiActor(GuiMagicActor actor) {
        this.actor = actor;
    }
    
    public Gui getGui() {
        return actor.getGui();
    }
    
    /**
     * This method should store all generated {@link Disposable}s in {@link #disposables}, so that they can
     * automatically handled in {@link #dispose()}.
     * 
     * subclass implementations MUST call {@code super.start()} to enable concession.
     */
    public void start() {
        d.add(actor.channels.concede.subscribe(actor.channels.fiber, new ConcedeCallback()));
    }
    
    /**
     * Disposes all disposables in reverse order
     */
    public void dispose() {
        d.dispose();
    }
    
    //Utility methods
    
    private class ConcedeCallback implements Callback<Void> {
        @Override
        public void onMessage(Void message) {
            actor.setConceded();
            concede();
        }
    }
    
    /**
     * This method should publish a message that ends the blocking of this actor.
     */
    protected abstract void concede();
    
    protected Disposable setName(final String newName) {
        return new Disposable() {
            private String oldName;
            
            {
                oldName = (String) getGui().getPassPriorityAction().getValue(Action.NAME);
                getGui().getPassPriorityAction().putValue(Action.NAME, newName);
            }
            
            @Override
            public void dispose() {
                getGui().getPassPriorityAction().putValue(Action.NAME, oldName);
            }
        };
    }
    
    protected Disposable setEnabled(final boolean newEnabled) {
        return new Disposable() {
            private boolean oldEnabled;
            
            {
                oldEnabled = getGui().getPassPriorityAction().isEnabled();
                getGui().getPassPriorityAction().setEnabled(newEnabled);
            }
            
            @Override
            public void dispose() {
                getGui().getPassPriorityAction().setEnabled(oldEnabled);
            }
        };
    }
    
    protected Disposable setBorder(final JComponent p, final Border newBorder) {
        return new Disposable() {
            private Border oldBorder;
            
            {
                oldBorder = p.getBorder();
                p.setBorder(newBorder);
                
                log.debug("set border: " + newBorder);
            }
            
            @Override
            public void dispose() {
                p.setBorder(oldBorder);
                log.debug("reset border: " + oldBorder);
            }
        };
    }
}
