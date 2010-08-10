/**
 * CardCompiler.java
 * 
 * Created on 03.04.2010
 */

package net.slightlymagic.laterna.magica.cards;


import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import net.slightlymagic.laterna.magica.card.CardTemplate;


/**
 * The class CardCompiler. A card compiler transforms data from an input stream into a {@link CardTemplate} object
 * to be used by Laterna Magica.
 * 
 * @version V0.0 03.04.2010
 * @author Clemens Koza
 */
public interface CardCompiler {
    /**
     * Creates a card template from the given input stream. The returned template must not be null.
     * 
     * @param handlers A list of handlers the compiler should post warnings to. guaranteed to be non-null.
     * 
     * @throws IOException If an error occurred reading the stream
     * @throws InvalidCardException If the stream does not specify a parseable card template.
     */
    public CardTemplate compile(InputStream is, List<? extends CompileHandler> handlers) throws IOException, InvalidCardException;
}
