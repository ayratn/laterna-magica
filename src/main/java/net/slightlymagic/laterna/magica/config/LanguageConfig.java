/**
 * LanguageConfig.java
 * 
 * Created on 16.09.2011
 */

package net.slightlymagic.laterna.magica.config;


import java.util.HashMap;
import java.util.Map;


/**
 * <p>
 * The class LanguageConfig.
 * </p>
 * 
 * @version V0.0 16.09.2011
 * @author Clemens Koza
 */
public class LanguageConfig {
    private Map<String, Language> languages = new HashMap<String, Language>();
    private String                preferred = "en";
    
    public void setLanguage(String key, Language language) {
        this.languages.put(key, language);
    }
    
    public Language getLanguage(String key) {
        return languages.get(key);
    }
    
    public void setPreferred(String preferred) {
        this.preferred = preferred;
    }
    
    public String getPreferred() {
        return preferred;
    }
    
    public Language getPreferredLanguage() {
        return getLanguage(getPreferred());
    }
    
    public String getString(String lang, String key) {
        return getLanguage(lang).getString(key);
    }
    
    public String getPreferredString(String key) {
        return getPreferredLanguage().getString(key);
    }
    
    public static class Language {
        private Map<String, String> strings = new HashMap<String, String>();
        
        public void setString(String key, String string) {
            this.strings.put(key, string);
        }
        
        public String getString(String key) {
            return strings.get(key);
        }
    }
}
