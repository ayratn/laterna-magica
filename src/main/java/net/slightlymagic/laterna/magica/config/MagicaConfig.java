/**
 * MagicaConfig.java
 * 
 * Created on 16.09.2011
 */

package net.slightlymagic.laterna.magica.config;


import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import net.slightlymagic.laterna.magica.LaternaInit;
import net.slightlymagic.laterna.magica.LaternaMagica;


/**
 * <p>
 * The class MagicaConfig.
 * </p>
 * 
 * @version V0.0 16.09.2011
 * @author Clemens Koza
 */
public class MagicaConfig extends LaternaInit {
    private LanguageConfig languages;
    private PicsConfig     pics;
    private File           decksFolder;
    
    @Override
    public void initialize() throws Exception {
        LaternaMagica.init();
    }
    
    public void setLanguages(LanguageConfig languages) {
        this.languages = languages;
    }
    
    public LanguageConfig getLanguages() {
        return languages;
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
