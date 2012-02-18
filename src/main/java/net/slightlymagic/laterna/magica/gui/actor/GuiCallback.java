/**
 * GuiCallback.java
 * 
 * Created on 16.02.2012
 */

package net.slightlymagic.laterna.magica.gui.actor;


import org.jetlang.core.Callback;


/**
 * <p>
 * The class GuiCallback.
 * </p>
 * 
 * @version V0.0 16.02.2012
 * @author Clemens Koza
 */
public abstract class GuiCallback<T> implements Callback<GameMessage<T>> {
    public void onMessage(GameMessage<T> m) {
        m.pushHistory();
        try {
            onMessage0(m.getValue());
        } finally {
            m.popHistory();
        }
    }
    
    public abstract void onMessage0(T m);
}
