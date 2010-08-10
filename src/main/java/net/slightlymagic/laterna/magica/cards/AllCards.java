/**
 * AllCards.java
 * 
 * Created on 03.04.2010
 */

package net.slightlymagic.laterna.magica.cards;


import static net.slightlymagic.laterna.magica.LaternaMagica.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.slightlymagic.laterna.magica.card.CardParts;
import net.slightlymagic.laterna.magica.card.CardTemplate;
import net.slightlymagic.laterna.magica.card.Printing;
import net.slightlymagic.laterna.magica.cards.CompileHandler.LogHandler;
import net.slightlymagic.laterna.magica.cards.CompileHandler.ZipHandler;


/**
 * The class AllCards is responsible for getting card templates for individual card names.
 * 
 * @version V0.0 03.04.2010
 * @author Clemens Koza
 */
public class AllCards {
    private static final String             COMPILED        = "/laterna/res/cards/compiled";
    
    private final Map<String, CardTemplate> templates       = new HashMap<String, CardTemplate>();
    private final Map<Integer, Printing>    printings       = new HashMap<Integer, Printing>();
    private final Set<CardTemplate>         uniqueTemplates = new HashSet<CardTemplate>();
    private final Set<Printing>             uniquePrintings = new HashSet<Printing>();
    
    public AllCards() {}
    
    public Set<Printing> getPrintings() {
        return uniquePrintings;
    }
    
    public Set<CardTemplate> getTemplates() {
        return uniqueTemplates;
    }
    
    /**
     * Compiles all cards to a zip file. Every entry in the zip is a serialized CardTemplate. The templates are
     * stored in memory at the same time.
     */
    public void compile() throws IOException {
        templates.clear();
        
        ZipHandler h = new ZipHandler(PROPS().getFile(COMPILED));
        CompileAll ca = new CompileAll();
        ca.add(new LogHandler());
        ca.add(h);
        ca.compile();
        h.close();
        load();
    }
    
    /**
     * Stores the serialized templates in memory.
     */
    public void load() throws IOException {
        templates.clear();
        
        ZipFile f = new ZipFile(PROPS().getFile(COMPILED));
        try {
            Enumeration<? extends ZipEntry> entries = f.entries();
            while(entries.hasMoreElements()) {
                ZipEntry result = entries.nextElement();
                ObjectInputStream is = new ObjectInputStream(f.getInputStream(result));
                try {
                    CardTemplate t = (CardTemplate) is.readObject();
                    put(t);
                } catch(ClassNotFoundException ex) {
                    throw new IOException(ex);
                } finally {
                    is.close();
                }
            }
        } finally {
            f.close();
        }
    }
    
    void put(CardTemplate t) {
        templates.put(t.toString().toLowerCase(), t);
        for(CardParts p:t.getCardParts())
            templates.put(p.toString().toLowerCase(), t);
        for(Printing p:t.getPrintings())
            printings.put(p.getMultiverseID(), p);
        
        uniqueTemplates.add(t);
        uniquePrintings.addAll(t.getPrintings());
    }
    
    /**
     * Returns the template for the card by either giving the full name or only one of the names. For most cards,
     * both are equal. However, split and flip cards have multiple names, Which are written as "A//B". This method
     * will return a card if only "A" or "B" is given.
     * 
     * Card names given to this method are not case-sensitive.
     */
    public CardTemplate getCard(String name) {
        return templates.get(name.toLowerCase());
    }
    
    public Printing getCard(int multiverseID) {
        return printings.get(multiverseID);
    }
    
    
    static String toFileName(String name) {
        return name.toLowerCase().replaceAll("[^a-z0-9-]", "_");
    }
    
//    private class CompileAll {
//        private File            out;
//        private ZipOutputStream zos;
//        /**
//         * A stream that overwrites close() to be a noop. Stands between the {@link ObjectOutputStream} and the
//         * {@link ZipOutputStream}.
//         */
//        private OutputStream    os;
//        
//        public CompileAll() throws FileNotFoundException {
//            out = PROPS().getPath(COMPILED);
//            zos = new ZipOutputStream(new FileOutputStream(out));
//            os = new FilterOutputStream(zos) {
//                @Override
//                public void close() throws IOException {}
//            };
//            zos.setComment("Compiled net.slightlymagic.laterna magica card templates");
//        }
//        
//        public void compile() throws IOException {
//            for(Entry<String, CardCompiler> compiler:compilers.entrySet()) {
//                List<Path> paths = PROPS().getValues(format(UNCOMPILED_PATH, compiler.getKey()), Path.class);
//                for(Path path:paths) {
//                    compileAll(compiler.getValue(), path.getFile());
//                }
//            }
//            
//            zos.close();
//        }
//        
//        private void compileAll(CardCompiler compiler, File path) {
//            //skip hidden files, especially ".svn"
//            if(path.isHidden()) return;
//            
//            if(path.isDirectory()) {
//                for(File f:path.listFiles())
//                    compileAll(compiler, f);
//            } else if(path.isFile()) {
//                CardTemplate t;
//                try {
//                    InputStream is = new FileInputStream(path);
//                    t = compiler.compile(is);
//                } catch(Exception ex) {
//                    log.warn(path + " could not be read", ex);
//                    return;
//                }
//                
//                put(t);
//                
//                try {
//                    zos.putNextEntry(new ZipEntry(toFileName(t.toString())));
//                    
//                    ObjectOutputStream os = new ObjectOutputStream(this.os);
//                    os.writeObject(t);
//                    os.close();
//                    
//                } catch(IOException ex) {
//                    log.warn(t + ": Entry was not written to the zip file.", ex);
//                } finally {
//                    try {
//                        zos.closeEntry();
//                    } catch(IOException ex) {
//                        log.error(ex);
//                    }
//                }
//            } else log.warn("File not existant: " + path);
//        }
//    }
}
