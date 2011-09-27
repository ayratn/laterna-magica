/**
 * PicsConfig.java
 * 
 * Created on 16.09.2011
 */

package net.slightlymagic.laterna.magica.config;


import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;


/**
 * <p>
 * The class PicsConfig.
 * </p>
 * 
 * @version V0.0 16.09.2011
 * @author Clemens Koza
 */
public class PicsConfig {
    private File symbols;
    private File cards;
    private File cardsHQ;
    
    public File getSymbols() {
        return symbols;
    }
    
    public void setSymbols(URL symbols) {
        try {
            this.symbols = new File(symbols.toURI());
        } catch(URISyntaxException ex) {
            throw new IllegalArgumentException("Supplied URL must belong to a file: " + symbols, ex);
        }
    }
    
    public File getCards() {
        return cards;
    }
    
    public void setCards(URL cards) {
        try {
            this.cards = new File(cards.toURI());
        } catch(URISyntaxException ex) {
            throw new IllegalArgumentException("Supplied URL must belong to a file: " + cards, ex);
        }
    }
    
    public File getCardsHQ() {
        return cardsHQ;
    }
    
    public void setCardsHQ(URL cardsHQ) {
        try {
            this.cardsHQ = new File(cardsHQ.toURI());
        } catch(URISyntaxException ex) {
            throw new IllegalArgumentException("Supplied URL must belong to a file: " + cardsHQ, ex);
        }
    }
}
