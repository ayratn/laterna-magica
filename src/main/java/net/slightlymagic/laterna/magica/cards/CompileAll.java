/**
 * CompileAll.java
 * 
 * Created on 23.07.2010
 */

package net.slightlymagic.laterna.magica.cards;


import static java.lang.String.*;
import static java.util.Collections.*;
import static net.slightlymagic.laterna.magica.LaternaMagica.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.slightlymagic.laterna.magica.card.CardTemplate;
import net.slightlymagic.treeProperties.TreeProperty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The class CompileAll.
 * 
 * @version V0.0 23.07.2010
 * @author Clemens Koza
 */
public class CompileAll {
    static final Logger                    log             = LoggerFactory.getLogger(CompileAll.class);
    
    private static final String            COMPILER_CLASS  = "/laterna/res/cards/uncompiled/([^/]+)/class";
    private static final String            UNCOMPILED_PATH = "/laterna/res/cards/uncompiled/%s/path";
    
    static final Map<String, CardCompiler> compilers;
    
    static {
        compilers = new HashMap<String, CardCompiler>();
        
        List<TreeProperty> classes = PROPS().getAllProperty(COMPILER_CLASS);
        
        Pattern p = Pattern.compile(COMPILER_CLASS);
        //loop through each class
        for(TreeProperty pr:classes) {
            //get an object of the compiler class
            String clazz = (String) pr.getValue();
            CardCompiler compiler;
            try {
                compiler = (CardCompiler) Class.forName(clazz).newInstance();
            } catch(Exception ex) {
                log.warn(clazz + " couldn't be instantiated", ex);
                continue;
            }
            
            //get a list of paths for the specified class
            //there may be more than one property with the same name. In this case, all of them are read
            Matcher m = p.matcher(pr.getName());
            //must be true because the same pattern was found by getProperties()
            if(!m.matches()) throw new AssertionError();
            String name = m.group(1);
            
            //put the compiler into the map
            compilers.put(name, compiler);
        }
    }
    
    private final List<CompileHandler>     handlers        = new ArrayList<CompileHandler>();
    private final List<CompileHandler>     view            = unmodifiableList(handlers);
    
    public void add(CompileHandler h) {
        if(h != null) handlers.add(h);
    }
    
    public void remove(CompileHandler h) {
        if(h != null) handlers.remove(h);
    }
    
    public void compile() throws IOException {
        for(Entry<String, CardCompiler> compiler:compilers.entrySet()) {
            List<File> files = PROPS().getValues(format(UNCOMPILED_PATH, compiler.getKey()), File.class);
            for(File f:files) {
                if(!f.exists()) log.warn("Card directory does not exist: " + f);
                else compileAll(compiler.getValue(), f);
            }
        }
    }
    
    private void compileAll(CardCompiler compiler, File path) {
        //skip hidden files, especially ".svn"
        if(path.isHidden()) return;
        
        if(path.isDirectory()) {
            for(File f:path.listFiles())
                compileAll(compiler, f);
        } else if(path.isFile()) {
            try {
                for(CompileHandler h:handlers)
                    h.start(path);
                InputStream is = new FileInputStream(path);
                try {
                    CardTemplate t = compiler.compile(is, view);
                    for(CompileHandler h:handlers)
                        h.handleSuccess(t);
                } finally {
                    try {
                        is.close();
                    } catch(IOException ex) {
                        log.warn("Exception while closing: ", ex);
                    }
                }
            } catch(Exception ex) {
                for(CompileHandler h:handlers)
                    h.handleFail(ex);
            }
        } else {
            //must be either a file or directory
            //i don't assert false here because there's still pipes and devices...
            //since the method is private, this shouldn't be a wrong parameter
            log.trace("neither file nor directory: " + path);
        }
    }
}
