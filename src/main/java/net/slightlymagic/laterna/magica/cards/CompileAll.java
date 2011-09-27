/**
 * CompileAll.java
 * 
 * Created on 23.07.2010
 */

package net.slightlymagic.laterna.magica.cards;


import static java.util.Collections.*;
import static net.slightlymagic.laterna.magica.LaternaMagica.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.slightlymagic.laterna.magica.card.CardTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The class CompileAll.
 * 
 * @version V0.0 23.07.2010
 * @author Clemens Koza
 */
public class CompileAll {
    static final Logger                log      = LoggerFactory.getLogger(CompileAll.class);
    
    private final List<CompileHandler> handlers = new ArrayList<CompileHandler>();
    private final List<CompileHandler> view     = unmodifiableList(handlers);
    
    public void add(CompileHandler h) {
        if(h != null) handlers.add(h);
    }
    
    public void remove(CompileHandler h) {
        if(h != null) handlers.remove(h);
    }
    
    public void compile() throws IOException {
        for(CardCompiler compiler:PROPS().getCards().getCompilers()) {
            File f = compiler.getPath();
            if(!f.exists()) log.warn("Card directory does not exist: " + f);
            else compileAll(compiler, f);
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
