/**
 * ConcessionException.java
 * 
 * Created on 03.09.2010
 */

package net.slightlymagic.laterna.magica.player;


/**
 * The class ConcessionException.
 * 
 * @version V0.0 03.09.2010
 * @author Clemens Koza
 */
public class ConcessionException extends IrregularActionException {
    private static final long serialVersionUID = 8106926386954118122L;
    
    public ConcessionException() {}
    
    public ConcessionException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ConcessionException(String message) {
        super(message);
    }
    
    public ConcessionException(Throwable cause) {
        super(cause);
    }
}
