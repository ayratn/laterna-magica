/**
 * DownloadLibs.java
 * 
 * Created on 06.09.2010
 */

package net.slightlymagic.laterna.magica.util;


import static javax.swing.JOptionPane.*;
import static net.slightlymagic.laterna.dl.DownloadJob.*;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JDialog;

import net.slightlymagic.laterna.dl.DownloadGui;
import net.slightlymagic.laterna.dl.DownloadJob;
import net.slightlymagic.laterna.dl.Downloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The class DownloadLibs.
 * 
 * @version V0.0 06.09.2010
 * @author Clemens Koza
 */
public class DownloadLibs {
    private static final Logger log = LoggerFactory.getLogger(DownloadLibs.class);
    
    public static void downloadLibs(File libDir, URL libs) throws IOException {
        if(!libDir.exists()) {
            showMessageDialog(
                    null,
                    "The \"lib\" directory is missing. Please make sure to download the libraries and place them outside this version's directory",
                    "Libraries Missing", ERROR_MESSAGE);
            System.exit(1);
        }
        
        BufferedReader r = new BufferedReader(new InputStreamReader(libs.openStream()));
        
        Map<String, URL> dl = new HashMap<String, URL>();
        
        for(String line; (line = r.readLine()) != null;) {
            line = line.trim();
            if(line.isEmpty() || line.startsWith("#")) continue;
            String[] parts = line.split("\\s+");
            assert parts.length == 2;
            if(new File(libDir, parts[0]).exists()) continue;
            dl.put(parts[0], new URL(parts[1]));
        }
        
        if(dl.isEmpty()) return;
        
        int choice = showConfirmDialog(null, "<html>"
                + "Some libraries are missing. Do you wand to automatically download them from:<br/>"
                + "http://laterna-magica.googlecode.com/<br/>"
                + "Laterna Magica can't run without those files.<br/>"
                + "Select \"No\" for a detailed list of the required files</html>", "Confirm download",
                YES_NO_OPTION);
        
        if(choice != YES_OPTION) {
            StringWriter sw = new StringWriter();
            PrintWriter w = new PrintWriter(sw);
            w.println("The following files are missing. Please download and copy them to Laterna Magica's \"lib\" directory:");
            w.println();
            for(URL url:dl.values())
                w.println(url);
            log.error(sw.toString());
            System.exit(1);
        }
        
        final DownloadGui g = new DownloadGui(new Downloader());
        
        for(Entry<String, URL> job:dl.entrySet())
            g.getDownloader().add(
                    new DownloadJob(job.getKey(), fromURL(job.getValue()), toFile(new File(libDir, job.getKey()))));
        
        JDialog d = new JDialog((Frame) null, "Download libraries", true);
        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        d.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                g.getDownloader().dispose();
            }
        });
        d.setLayout(new BorderLayout());
        d.add(g);
        d.pack();
        d.setVisible(true);
        
        showMessageDialog(null, "Please restart Laterna Magica");
    }
}
