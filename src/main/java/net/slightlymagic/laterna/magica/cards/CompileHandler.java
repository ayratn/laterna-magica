/**
 * CompileHandler.java
 * 
 * Created on 23.07.2010
 */

package net.slightlymagic.laterna.magica.cards;


import static java.lang.String.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


import net.slightlymagic.laterna.magica.card.CardTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;


/**
 * The class CompileHandler. A CompileHandler can handle the output from compiling cards from a
 * {@link CardCompiler}, for example by serializing the compiled instances to a zip file.
 * 
 * @version V0.0 23.07.2010
 * @author Clemens Koza
 */
public interface CompileHandler {
    /**
     * Indicates that a new file is now compiled
     */
    public void start(File f);
    
    /**
     * Handles an exception thrown by the {@link CardCompiler}.
     * 
     * @param f The file being parsed
     * @param detail A message object characterizing the warning
     */
    public void handleWarn(Object detail);
    
    /**
     * Handles an annotation reported by the {@link CardCompiler}.
     * 
     * @param f The file being parsed
     * @param detail A message object characterizing the warning
     */
    public void handleAnnotation(String key, String value);
    
    /**
     * Handles a successfully parsed card
     * 
     * @param f The file being parsed
     */
    public void handleSuccess(CardTemplate card);
    
    /**
     * Handles an exception thrown by the {@link CardCompiler}.
     * 
     * @param f The file being parsed
     */
    public void handleFail(Exception ex);
    
    /**
     * The class LogHandler. Logs information to a log4j logger.
     */
    public static class LogHandler implements CompileHandler {
        static final Logger log = LoggerFactory.getLogger(LogHandler.class);
        
        public void start(File f) {
            log.info("Compiling " + f);
        }
        
        public void handleWarn(Object detail) {
            log.warn(valueOf(detail));
        }
        
        public void handleAnnotation(String key, String value) {
            log.info(format("@%s %s", key, value));
        }
        
        public void handleSuccess(CardTemplate card) {
            log.info("Compiling succeeded: " + card);
        }
        
        public void handleFail(Exception ex) {
            log.warn("Compiling failed", ex);
        }
    }
    
    /**
     * The class ZipHandler. Writes the compiled cards to a zip file
     */
    public static class ZipHandler implements CompileHandler {
        static final Logger     log         = LoggerFactory.getLogger(ZipHandler.class);
        
        private ZipOutputStream zos;
        /**
         * A stream that overwrites close() to be a noop. Stands between the {@link ObjectOutputStream} and the
         * {@link ZipOutputStream}.
         */
        private OutputStream    os;
        
        private List<Object>    warnings    = new ArrayList<Object>();
        private List<Object>    annotations = new ArrayList<Object>();
        
        public ZipHandler(File out) throws IOException {
            zos = new ZipOutputStream(new FileOutputStream(out));
            os = new FilterOutputStream(zos) {
                @Override
                public void close() throws IOException {}
            };
            zos.setComment("Compiled net.slightlymagic.laterna magica card templates");
        }
        
        public void start(File f) {
            warnings.clear();
            annotations.clear();
        }
        
        public void handleWarn(Object detail) {
            warnings.add(detail);
        }
        
        public void handleAnnotation(String key, String value) {
            annotations.add(format("@%s %s", key, value));
        }
        
        public void handleSuccess(CardTemplate card) {
            try {
                ZipEntry e = new ZipEntry(toFileName(card.toString()));
                {
                    List<Object> comments = new ArrayList<Object>();
                    comments.add("Warnings:");
                    comments.addAll(warnings);
                    comments.add("Annotations:");
                    comments.addAll(annotations);
                    e.setComment(Joiner.on(System.getProperty("line.separator")).join(comments));
                }
                zos.putNextEntry(e);
                
                ObjectOutputStream os = new ObjectOutputStream(this.os);
                os.writeObject(card);
                os.close();
                
            } catch(IOException ex) {
                log.warn(card + ": Entry was not written to the zip file.", ex);
            } finally {
                try {
                    zos.closeEntry();
                } catch(IOException ex) {
                    log.error("", ex);
                }
            }
        }
        
        public void handleFail(Exception ex) {
            //failed cards are simply not parsed
        }
        
        public void close() throws IOException {
            zos.close();
        }
        
        private static String toFileName(String name) {
            return name.toLowerCase().replaceAll("\\W", "_");
        }
    }
}
