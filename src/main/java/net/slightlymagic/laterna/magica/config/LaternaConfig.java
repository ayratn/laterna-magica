/**
 * LaternaConfig.java
 * 
 * Created on 16.09.2011
 */

package net.slightlymagic.laterna.magica.config;


import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;


/**
 * <p>
 * The class LaternaConfig.
 * </p>
 * 
 * @version V0.0 16.09.2011
 * @author Clemens Koza
 */
public class LaternaConfig {
    private LanguageConfig languages;
    private CardsConfig    cards;
    private PicsConfig     pics;
    private File           decksFolder;
    
    public void setLanguages(LanguageConfig languages) {
        this.languages = languages;
    }
    
    public void setPreferredLanguage(String preferred) {
        languages.setPreferred(preferred);
    }
    
    public LanguageConfig getLanguages() {
        return languages;
    }
    
    public void setCards(CardsConfig cards) {
        this.cards = cards;
    }
    
    public CardsConfig getCards() {
        return cards;
    }
    
    public void setPics(PicsConfig pics) {
        this.pics = pics;
    }
    
    public PicsConfig getPics() {
        return pics;
    }
    
    public void setDecksFolder(URL decksFolder) {
        try {
            this.decksFolder = new File(decksFolder.toURI());
        } catch(URISyntaxException ex) {
            throw new IllegalArgumentException("Supplied URL must belong to a file: " + decksFolder, ex);
        }
    }
    
    public File getDecksFolder() {
        return decksFolder;
    }
}
