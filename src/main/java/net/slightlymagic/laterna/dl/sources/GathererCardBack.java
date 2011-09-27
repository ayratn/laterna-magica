/**
 * GathererCards.java
 * 
 * Created on 25.08.2010
 */

package net.slightlymagic.laterna.dl.sources;


import static net.slightlymagic.laterna.dl.DownloadJob.*;
import static net.slightlymagic.laterna.magica.LaternaMagica.*;

import java.io.File;
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
    private static final File   out = new File(PROPS().getPics().getCards(), "0.jpg");
    private static final String url = "http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid=0&type=card";
    
    @Override
    public Iterator<DownloadJob> iterator() {
        return Iterators.singletonIterator(new DownloadJob("0 Card Back", fromURL(url), toFile(out)));
    }
}
