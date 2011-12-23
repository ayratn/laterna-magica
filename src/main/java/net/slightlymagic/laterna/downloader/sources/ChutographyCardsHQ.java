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
 * TODO don't know why, but this doesn't work at all
 * 
 * @version V0.0 25.08.2010
 * @author Clemens Koza
 */
public class ChutographyCardsHQ implements Iterable<DownloadJob> {
    private static final File   outDir = MAGICA_CONFIG().getPics().getCardsHQ();
    private static final String urlFmt = "http://mtgpics.chutography.com/%s/%s.full.jpg";
    
    public Iterator<DownloadJob> iterator() {
        return new AbstractIterator<DownloadJob>() {
            private Iterator<Printing> it = CARDS().getPrintings().iterator();
            
            @Override
            protected DownloadJob computeNext() {
                if(!it.hasNext()) return endOfData();
                Printing p;
                do {
                    p = it.next();
                } while(p.getExpansion() == null);
                
                int id = p.getMultiverseID();
                String cardName = p.getTemplate().getSimpleCardParts().get(0).getName();
                
                String name = id + " " + cardName + " " + p.getExpansion();
                File dst = new File(outDir, id + ".jpg");
                String url = format(urlFmt, p.getExpansion().getMwsName(), cardName);
                return new DownloadJob(name, fromURL(url), toFile(dst));
            }
        };
    }
}
