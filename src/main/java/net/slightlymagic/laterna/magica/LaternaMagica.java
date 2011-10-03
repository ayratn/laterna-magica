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
import net.slightlymagic.laterna.magica.config.MagicaConfig;
import net.slightlymagic.laterna.magica.gui.main.MainPane;
import net.slightlymagic.laterna.magica.util.DownloadLibs;


/**
 * The class LaternaMagica.
 * 
 * @version V0.0 28.10.2009
 * @author Clemens Koza
 */
public class LaternaMagica {
    private static MagicaConfig MAGICA_CONFIG;
    private static AllCards     CARDS;
    
    public static void main(String[] args) throws Exception {
        preInit();
        LaternaInit.init();
        
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
    
    
    public static void init() throws Exception {
        MAGICA_CONFIG = LaternaTools.CONFIG().getConfig("magica");
        CARDS = CardFormats.getAllCards();
    }
    
    public static MagicaConfig MAGICA_CONFIG() {
        return MAGICA_CONFIG;
    }
    
    public static AllCards CARDS() {
        return CARDS;
    }
    
    public static void preInit() throws IOException {
        try {
            URL url = LaternaMagica.class.getProtectionDomain().getCodeSource().getLocation();
            File base = new File(url.toURI()).getAbsoluteFile();
            
            //only do this if running from a jar file, aka the deployed environment
            if(!base.isFile()) return;
            //running from jar file
            base = base.getParentFile();
            
            DownloadLibs.downloadLibs(new File(base, "../lib"),
                    Thread.currentThread().getContextClassLoader().getResource("libs.txt"));
            
            File res = new File(base, "res");
            File sharedRes = new File(base, "../res");
            File usr = new File(System.getProperty("user.home"), ".slightlymagic.net/laterna");
            unpack(res, "res.zip");
            unpack(sharedRes, "sharedRes.zip");
            unpack(usr, "usr.zip");
        } catch(URISyntaxException ex) {
            throw new IOException(ex);
        }
    }
    
    private static void unpack(File dest, String resource) throws IOException {
        if(dest.exists()) return;
        if(!dest.mkdirs()) throw new IOException("Directory could not be created: " + dest);
        
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(
                Thread.currentThread().getContextClassLoader().getResourceAsStream(resource)));
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
