/**
 * CardsConfig.java
 * 
 * Created on 16.09.2011
 */

package net.slightlymagic.laterna.magica.config;


import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.slightlymagic.laterna.magica.cards.CardCompiler;


/**
 * <p>
 * The class CardsConfig.
 * </p>
 * 
 * @version V0.0 16.09.2011
 * @author Clemens Koza
 */
public class CardsConfig {
    private Map<String, CardCompiler> compilers = new HashMap<String, CardCompiler>();
    private boolean                   compileOnStart;
    private File                      compiled;
    private String                    author;
    
    public CardCompiler getCompiler(String key) {
        return compilers.get(key);
    }
    
    public Collection<CardCompiler> getCompilers() {
        return compilers.values();
    }
    
    public void setCompiler(String key, CardCompiler compiler) {
        compilers.put(key, compiler);
    }
    
    public boolean isCompileOnStart() {
        return compileOnStart;
    }
    
    public void setCompileOnStart(boolean compileOnStart) {
        this.compileOnStart = compileOnStart;
    }
    
    public File getCompiled() {
        return compiled;
    }
    
    public void setCompiled(URL compiled) {
        try {
            this.compiled = new File(compiled.toURI());
        } catch(URISyntaxException ex) {
            throw new IllegalArgumentException("Supplied URL must belong to a file: " + compiled, ex);
        }
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
}
