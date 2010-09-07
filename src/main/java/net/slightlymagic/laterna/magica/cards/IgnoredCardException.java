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
public class IgnoredCardException extends InvalidCardException {
    private static final long serialVersionUID = -2222834427827344976L;
    
    public IgnoredCardException() {
        super();
    }
    
    public IgnoredCardException(String message, String line, Throwable cause) {
        super(message, line, cause);
    }
    
    public IgnoredCardException(String message, String line) {
        super(message, line);
    }
    
    public IgnoredCardException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public IgnoredCardException(String message) {
        super(message);
    }
    
    public IgnoredCardException(Throwable cause) {
        super(cause);
    }
}
