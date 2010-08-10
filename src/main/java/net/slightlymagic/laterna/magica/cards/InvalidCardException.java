/**
 * InvalidCardException.java
 * 
 * Created on 03.04.2010
 */

package net.slightlymagic.laterna.magica.cards;


import net.slightlymagic.laterna.magica.card.CardTemplate;


/**
 * The class InvalidCardException. This exception is thrown if a {@link CardCompiler} can't create a
 * {@link CardTemplate} from an input stream because the input stream's data does not conform to the format or does
 * not specify a possible card.
 * 
 * @version V0.0 03.04.2010
 * @author Clemens Koza
 */
public class InvalidCardException extends Exception {
    private static final long serialVersionUID = 5095231651822353969L;
    
    public InvalidCardException() {
        super();
    }
    
    public InvalidCardException(String message, String line, Throwable cause) {
        this(message + "\n" + line, cause);
    }
    
    public InvalidCardException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public InvalidCardException(String message, String line) {
        this(message + "\n" + line);
    }
    
    public InvalidCardException(String message) {
        super(message);
    }
    
    public InvalidCardException(Throwable cause) {
        super(cause);
    }
}
