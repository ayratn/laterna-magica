/**
 * IrregularActionException.java
 * 
 * Created on 03.09.2010
 */

package net.slightlymagic.laterna.magica.player;


/**
 * The class IrregularActionException. An IrregularActionException is thrown by {@linkplain MagicActor actors} when
 * a player performs an irregular action while the actor is blocking, e.g. waiting for user input. Currently, the
 * only subclass is {@link ConcessionException}.
 * 
 * @version V0.0 03.09.2010
 * @author Clemens Koza
 */
public abstract class IrregularActionException extends RuntimeException {
    private static final long serialVersionUID = -5029787538550254955L;
    
    public IrregularActionException() {
        super();
    }
    
    public IrregularActionException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public IrregularActionException(String message) {
        super(message);
    }
    
    public IrregularActionException(Throwable cause) {
        super(cause);
    }
}
