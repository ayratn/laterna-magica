/**
 * GathererSymbols.java
 * 
 * Created on 25.08.2010
 */

package net.slightlymagic.laterna.dl.sources;


import static java.lang.String.*;
import static net.slightlymagic.laterna.magica.LaternaMagica.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import net.slightlymagic.laterna.dl.DownloadJob;

import com.google.common.collect.AbstractIterator;


/**
 * The class GathererSymbols.
 * 
 * @version V0.0 25.08.2010
 * @author Clemens Koza
 */
public class GathererSymbols implements Iterable<DownloadJob> {
    private static final File     outDir  = PROPS().getFile("/laterna/res/pics/symbols");
    private static final String   urlFmt  = "http://gatherer.wizards.com/Handlers/Image.ashx?sizes=%1$s&name=%2$s&type=symbol";
    
    private static final String[] sizes   = {"small", "medium", "large"};
    
    private static final String[] symbols = {"W", "U", "B", "R", "G",

                                          "W/U", "U/B", "B/R", "R/G", "G/W", "W/B", "U/R", "B/G", "R/W", "G/U",

                                          "2/W", "2/U", "2/B", "2/R", "2/G",

                                          "X", "S", "T", "Q"};
    private static final int      minNumeric = 0, maxNumeric = 16;
    
    @Override
    public Iterator<DownloadJob> iterator() {
        return new AbstractIterator<DownloadJob>() {
            private int  sizeIndex, symIndex, numeric = minNumeric;
            private File dir = new File(outDir, sizes[sizeIndex]);
            
            @Override
            protected DownloadJob computeNext() {
                String sym;
                if(symIndex < symbols.length) {
                    sym = symbols[symIndex++];
                } else if(numeric <= maxNumeric) {
                    sym = "" + (numeric++);
                } else {
                    sizeIndex++;
                    if(sizeIndex == sizes.length) return endOfData();
                    
                    symIndex = 0;
                    numeric = 0;
                    dir = new File(outDir, sizes[sizeIndex]);
                    return computeNext();
                }
                String symbol = sym.replaceAll("/", "");
                File dst = new File(dir, symbol + ".gif");
                
                if(symbol.equals("T")) symbol = "tap";
                else if(symbol.equals("Q")) symbol = "untap";
                else if(symbol.equals("S")) symbol = "snow";
                
                URL url;
                try {
                    url = new URL(format(urlFmt, sizes[sizeIndex], symbol));
                } catch(MalformedURLException ex) {
                    throw new AssertionError(ex);
                }
                
                return new DownloadJob(sym + " " + sizes[sizeIndex], url, dst);
            }
        };
    }
}
