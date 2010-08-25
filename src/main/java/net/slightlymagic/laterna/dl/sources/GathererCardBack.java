/**
 * GathererCards.java
 * 
 * Created on 25.08.2010
 */

package net.slightlymagic.laterna.dl.sources;


import static net.slightlymagic.laterna.magica.LaternaMagica.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import net.slightlymagic.laterna.dl.DownloadJob;

import com.google.common.collect.Iterators;


/**
 * The class GathererCards.
 * 
 * @version V0.0 25.08.2010
 * @author Clemens Koza
 */
public class GathererCardBack implements Iterable<DownloadJob> {
    private static final File   outDir = PROPS().getFile("/laterna/res/pics/cards");
    private static final String url    = "http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid=0&type=card";
    
    @Override
    public Iterator<DownloadJob> iterator() {
        try {
            return Iterators.singletonIterator(new DownloadJob("0 Card Back", new URL(url), new File(outDir,
                    "0.jpg")));
        } catch(MalformedURLException ex) {
            throw new AssertionError();
        }
    }
}
