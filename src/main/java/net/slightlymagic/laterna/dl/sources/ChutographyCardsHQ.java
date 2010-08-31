/**
 * GathererCards.java
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
import net.slightlymagic.laterna.magica.card.Printing;

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
    private static final File   outDir = PROPS().getFile("/laterna/res/pics/cardsHQ");
    private static final String urlFmt = "http://mtgpics.chutography.com/%s/%s.full.jpg";
    
    @Override
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
                String cardName = p.getTemplate().getCardParts().get(0).getName();
                
                String name = id + " " + cardName + " " + p.getExpansion();
                File dst = new File(outDir, id + ".jpg");
                String url = format(urlFmt, p.getExpansion().getMwsName(), cardName);
                return new DownloadJob(name, fromURL(url), toFile(dst));
            }
        };
    }
}
