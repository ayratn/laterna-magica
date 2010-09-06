/**
 * LaternaMagica.java
 * 
 * Created on 28.10.2009
 */

package net.slightlymagic.laterna.magica;


import java.awt.Dimension;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JFrame;

import net.slightlymagic.laterna.magica.cards.AllCards;
import net.slightlymagic.laterna.magica.gui.main.MainPane;
import net.slightlymagic.laterna.magica.util.DownloadLibs;
import net.slightlymagic.treeProperties.PropertyTree;
import net.slightlymagic.utils.Configurator;
import net.slightlymagic.utils.PropertyTreeConfigurator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The class LaternaMagica.
 * 
 * @version V0.0 28.10.2009
 * @author Clemens Koza
 */
public class LaternaMagica {
    private static final Logger log = LoggerFactory.getLogger(LaternaMagica.class);
    
    private static PropertyTree PROPS;
    private static AllCards     CARDS;
    
    public static void main(String[] args) throws IOException {
        LaternaMagica.init();
        
        JFrame jf = new JFrame();
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setTitle("Laterna Magica");
        
        jf.add(new MainPane());
        
        jf.pack();
        Dimension screen = jf.getToolkit().getScreenSize();
        Dimension window = jf.getSize();
        jf.setLocation((screen.width - window.width) / 2, (screen.height - window.height) / 2);
        
        jf.setVisible(true);
    }
    
    public static void init() throws IOException {
        preInit();
        
        URL url = LaternaMagica.class.getResource("/config.properties");
        System.out.println("Configuring from Resource /config.properties");
        System.out.println("Resolved to " + url);
        Configurator c = new Configurator().configure(url).execute();
        
        PROPS = c.getConfigurator(PropertyTreeConfigurator.class).getTree();
//        System.out.println(PROPS.getAllProperty(Predicates.alwaysTrue()).toString().replaceAll(",", "\n"));
        CARDS = new AllCards();
        
        boolean compile = PROPS().getBoolean("/laterna/res/cards/compileOnStart", false);
        if(!compile) {
            try {
                log.info("Loading...");
                CARDS.load();
                log.info("ok");
            } catch(Exception ex) {
                log.error("Error loading compiled cards; cards were not completely loaded.\n"
                        + "LaternaMagica will now try to recreate the cards.\n"
                        + "Below is the reason why loading failed", ex);
                compile = true;
            }
        }
        if(compile) {
            log.info("Compiling...");
            CARDS.compile();
            log.info("ok");
        }
    }
    
    public static PropertyTree PROPS() {
        return PROPS;
    }
    
    public static AllCards CARDS() {
        return CARDS;
    }
    
    private static void preInit() throws IOException {
        try {
            URL url = LaternaMagica.class.getProtectionDomain().getCodeSource().getLocation();
            File base = new File(url.toURI()).getAbsoluteFile();
            
            //only do this if running from a jar file, aka the deployed environment
            if(!base.isFile()) return;
            //running from jar file
            base = base.getParentFile();
            
            DownloadLibs.downloadLibs(new File(base, "../lib"), LaternaMagica.class.getResource("/libs.txt"));
            
            File res = new File(base, "res");
            File sharedRes = new File(base, "../res");
            File usr = new File(System.getProperty("user.home"), ".slightlymagic.net/laterna");
            unpack(res, "/res.zip");
            unpack(sharedRes, "/sharedRes.zip");
            unpack(usr, "/usr.zip");
        } catch(URISyntaxException ex) {
            throw new IOException(ex);
        }
    }
    
    private static void unpack(File dest, String resource) throws IOException {
        if(dest.exists()) return;
        if(!dest.mkdirs()) throw new IOException("Directory could not be created: " + dest);
        
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(
                LaternaMagica.class.getResourceAsStream(resource)));
        try {
            byte[] buf = new byte[8 * 1024];
            
            for(ZipEntry e; (e = zis.getNextEntry()) != null;) {
                File f = new File(dest, e.getName());
                if(e.isDirectory()) {
                    if(!f.mkdirs() && !f.exists()) throw new IOException("Directory could not be created: " + dest);
                } else {
                    if(!f.getParentFile().mkdirs() && !f.getParentFile().exists()) throw new IOException(
                            "Directory could not be created: " + dest);
                    
                    OutputStream os = new BufferedOutputStream(new FileOutputStream(f));
                    try {
                        for(int len; (len = zis.read(buf)) != -1;)
                            os.write(buf, 0, len);
                    } finally {
                        try {
                            os.close();
                        } catch(IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        } finally {
            try {
                zis.close();
            } catch(IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
