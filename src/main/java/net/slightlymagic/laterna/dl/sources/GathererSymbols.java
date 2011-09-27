/**
 * GathererSymbols.java
 * 
 * Created on 25.08.2010
 */

package net.slightlymagic.laterna.dl.sources;


import static java.lang.String.*;
import static net.slightlymagic.laterna.dl.DownloadJob.*;
import static net.slightlymagic.laterna.magica.LaternaMagica.*;

import java.io.File;
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
    //TODO chaos and planeswalker symbol
    //chaos: http://gatherer.wizards.com/Images/Symbols/chaos.gif
    
    private static final File     outDir  = PROPS().getPics().getSymbols();
    private static final String   urlFmt  = "http://gatherer.wizards.com/handlers/image.ashx?size=%1$s&name=%2$s&type=symbol";
    
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
                
                String url = format(urlFmt, sizes[sizeIndex], symbol);
                
                return new DownloadJob(sym, fromURL(url), toFile(dst));
            }
        };
    }
}
