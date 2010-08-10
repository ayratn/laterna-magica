/**
 * AbstractContinuousEffect.java
 * 
 * Created on 07.09.2009
 */

package net.slightlymagic.laterna.magica.effect.impl;


import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.effect.ContinuousEffect;
import net.slightlymagic.laterna.magica.timestamp.impl.AbstractTimestamped;


/**
 * The class AbstractContinuousEffect.
 * 
 * @version V0.0 07.09.2009
 * @author Clemens Koza
 */
public class AbstractContinuousEffect extends AbstractTimestamped implements ContinuousEffect {
    private Layer l;
    
    public AbstractContinuousEffect(Game game, Layer l) {
        super(game);
        this.l = l;
    }
    
    public Layer getLayer() {
        return l;
    }
}
