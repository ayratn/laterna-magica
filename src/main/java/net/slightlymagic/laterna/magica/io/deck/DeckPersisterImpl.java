/**
 * DeckPersisterImpl.java
 * 
 * Created on 29.07.2010
 */

package net.slightlymagic.laterna.magica.io.deck;


import static java.lang.Integer.*;
import static java.lang.String.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.slightlymagic.laterna.magica.LaternaMagica;
import net.slightlymagic.laterna.magica.card.Printing;
import net.slightlymagic.laterna.magica.deck.Deck;
import net.slightlymagic.laterna.magica.deck.DeckType;


/**
 * The class DeckPersisterImpl.
 * 
 * @version V0.0 29.07.2010
 * @author Clemens Koza
 */
public class DeckPersisterImpl implements DeckPersister {
    public Deck readDeck(InputStream is) throws IOException {
        Pattern poolP = Pattern.compile("\\s*(\\w+):\\s*");
        Pattern cardP = Pattern.compile("\\s*(\\d+)\\s+(\\d+)\\s*(\\s.*)?");
        BufferedReader r = new BufferedReader(new InputStreamReader(is));
        
        Deck d = new Deck();
        
        DeckType t = null;
        String s;
        while((s = r.readLine()) != null) {
            Matcher m;
            if((m = poolP.matcher(s)).matches()) {
                t = DeckType.valueOf(m.group(1));
                d.addPool(t);
            } else if((m = cardP.matcher(s)).matches()) {
                if(t == null) throw new IOException("Missing deck type");
                int num = parseInt(m.group(1));
                int mid = parseInt(m.group(2));
                
                d.getPool(t).put(LaternaMagica.CARDS().getCard(mid), num);
            }
        }
        is.close();
        return d;
    }
    
    public void writeDeck(Deck d, OutputStream os) throws IOException {
        BufferedWriter w = new BufferedWriter(new OutputStreamWriter(os));
        for(DeckType t:DeckType.values()) {
            Map<Printing, Integer> pool = d.getPool(t);
            if(pool != null) {
                w.write(format("%s:%n", t));
                for(Entry<Printing, Integer> e:pool.entrySet())
                    w.write(format("%2d %6d #%s%n", e.getValue(), e.getKey().getMultiverseID(),
                            e.getKey().getTemplate()));
                w.newLine();
            }
        }
        w.close();
    }
    
}
