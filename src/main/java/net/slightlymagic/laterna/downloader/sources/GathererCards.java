/**
 * GathererCards.java
 * 
 * Created on 25.08.2010
 */

package net.slightlymagic.laterna.downloader.sources;


import static java.lang.String.*;
import static net.slightlymagic.laterna.magica.LaternaMagica.*;
import static net.slightlymagic.utils.downloader.DownloadJob.*;

import java.io.File;
import java.util.Iterator;

import net.slightlymagic.laterna.magica.card.Printing;
import net.slightlymagic.utils.downloader.DownloadJob;

import com.google.common.collect.AbstractIterator;


/**
 * The class GathererCards.
 * 
 * @version V0.0 25.08.2010
 * @author Clemens Koza
 */
public class GathererCards implements Iterable<DownloadJob> {
    private static final File   outDir = MAGICA_CONFIG().getPics().getCards();
    private static final String urlFmt = "http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid=%d&type=card";
    
    @Override
    public Iterator<DownloadJob> iterator() {
        return new AbstractIterator<DownloadJob>() {
            private Iterator<Printing> it = CARDS().getPrintings().iterator();
            
            @Override
            protected DownloadJob computeNext() {
                if(!it.hasNext()) return endOfData();
                Printing p = it.next();
                
                int id = p.getMultiverseID();
                String name = id + " " + p.getTemplate().getSimpleCardParts().get(0).getName() + " "
                        + p.getExpansion();
                File dst = new File(outDir, id + ".jpg");
                String url = format(urlFmt, id);
                return new DownloadJob(name, fromURL(url), toFile(dst));
            }
        };
    }
}
