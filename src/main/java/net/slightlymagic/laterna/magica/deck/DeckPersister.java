/**
 * DeckPersister.java
 * 
 * Created on 29.07.2010
 */

package net.slightlymagic.laterna.magica.deck;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * The class DeckPersister.
 * 
 * @version V0.0 29.07.2010
 * @author Clemens Koza
 */
public interface DeckPersister {
    public Deck readDeck(InputStream is) throws IOException;
    
    public void writeDeck(Deck d, OutputStream os) throws IOException;
}
