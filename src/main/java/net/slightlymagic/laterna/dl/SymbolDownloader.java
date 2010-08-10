/**
 * SymbolDownloader.java
 * 
 * Created on 23.07.2009
 */

package net.slightlymagic.laterna.dl;


import static java.lang.Math.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * The class SymbolDownloader.
 * 
 * @version V0.0 23.07.2009
 * @author Clemens Koza
 */
public class SymbolDownloader implements Runnable {
    public static void main(String[] args) {
        File parent = new File("res");
        String[] size = {"small", "medium", "large"};
//        String[] sym = {
//                "W", "U", "B", "R", "G", "W/U", "U/B", "B/R", "R/G", "G/W", "W/B", "U/R", "B/G", "R/W", "G/U",
//                "2/W", "2/U", "2/B", "2/R", "2/G", "X", "S", "T", "Q"};
        int minNumeric = 17, maxNumeric = 30;
        for(int i = 0; i < size.length; i++) {
            File folder = new File(parent, size[i]);
            if(!folder.isDirectory() && !folder.mkdirs()) throw new RuntimeException(
                    "creating output folder failed");
//            for(int j = 0; j < sym.length; j++) {
//                new SymbolDownloader(size[i], sym[j], folder);
//            }
            for(int j = minNumeric; j <= maxNumeric; j++) {
                new SymbolDownloader(size[i], "" + j, folder);
            }
        }
        
        try {
            Thread.sleep(1000);
            for(int i = 0; i < threads.size(); i++) {
                threads.get(i).join();
            }
            for(Exception ex:SymbolDownloader.ex.values()) {
                ex.printStackTrace();
            }
        } catch(InterruptedException ex) {}
    }
    
    private static final String           fmt = "http://gatherer.wizards.com/Handlers/Image.ashx?size=%1$s&name=%2$s&type=symbol";
    private String                        url, sym, size;
    private File                          dst;
    
    private static Map<String, Integer>   stateM;
    private static Map<String, Exception> ex;
    private static List<String>           symL, sizeL;
    private static int                    modCount, printCount;
    
    private static List<Thread>           threads;
    
    static {
        stateM = new HashMap<String, Integer>();
        ex = new HashMap<String, Exception>();
        threads = new ArrayList<Thread>();
        symL = new ArrayList<String>();
        sizeL = new ArrayList<String>();
    }
    
    public static synchronized void put(String sym, String size, Exception ex) {
        SymbolDownloader.ex.put(sym + size, ex);
        put(sym, size, 2);
    }
    
    public static synchronized Exception getEx(String sym, String size) {
        return SymbolDownloader.ex.get(sym + size);
    }
    
    public static synchronized void put(String sym, String size) {
        if(!symL.contains(sym)) symL.add(sym);
        if(!sizeL.contains(size)) sizeL.add(size);
    }
    
    public static synchronized void put(String sym, String size, int state) {
        put(sym, size);
        stateM.put(sym + size, state);
        modCount++;
        print();
    }
    
    public static synchronized String get(String sym, String size) {
        if(!stateM.containsKey(sym + size)) return " ";
        else switch(stateM.get(sym + size)) {
            case 0: //started
                return "X";
            case 1: //finished
                return "-";
            case 2: //ended with error
                return "!";
            default:
                return " ";
        }
    }
    
    public static void print() {
        synchronized(SymbolDownloader.class) {
            if(printCount == modCount) return;
            for(int i = 0; i < 7; i++)
                System.out.println();
            
            int len = 0;
            for(String s:sizeL)
                len = max(len, s.length());
            len++;
            
            for(int i = 0; i < len; i++)
                System.out.print(' ');
            for(String s:symL) {
                System.out.print(' ');
                if(s.length() == 1) System.out.print(' ');
                else System.out.print(s.charAt(0));
            }
            System.out.println();
            
            for(int i = 0; i < len; i++)
                System.out.print(' ');
            for(String s:symL) {
                System.out.print(' ');
                if(s.length() == 1) System.out.print(s.charAt(0));
                else System.out.print(s.charAt(1));
            }
            System.out.println();
            
            //Before printing the output, it can still be guaranteed that the data is the most recent
            printCount = modCount;
            
            for(String size:sizeL) {
                for(int i = size.length(); i < len; i++)
                    System.out.print(' ');
                System.out.print(size);
                for(String sym:symL) {
                    System.out.print(' ');
                    System.out.print(get(sym, size));
                }
                System.out.println();
            }
        }
        try {
            Thread.sleep(10);
        } catch(InterruptedException ex) {}
    }
    
    public SymbolDownloader(String size, String symbol, File folder) {
        Thread t = new Thread(this, symbol + " " + size);
        symbol = symbol.replaceAll("/", "").toUpperCase();
        put(symbol, size);
        this.size = size;
        sym = symbol;
        dst = new File(folder, symbol + ".gif");
        if(symbol.equals("T")) symbol = "tap";
        else if(symbol.equals("Q")) symbol = "untap";
        else if(symbol.equals("S")) symbol = "snow";
        
        url = String.format(fmt, size, symbol);
        t.start();
        threads.add(t);
    }
    
    public void run() {
        InputStream is = null;
        OutputStream os = null;
        try {
            put(sym, size, 0);
            URLConnection c = new URL(url).openConnection();
            is = c.getInputStream();
            os = new FileOutputStream(dst);
            byte[] buf = new byte[512];
            int len;
            while((len = is.read(buf)) != -1)
                os.write(buf, 0, len);
            put(sym, size, 1);
        } catch(Exception ex) {
            put(sym, size, ex);
        } finally {
            if(is != null) try {
                is.close();
            } catch(IOException ex) {}
            if(os != null) try {
                os.close();
            } catch(IOException ex) {}
        }
    }
}
