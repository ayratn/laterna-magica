/**
 * ImageCache.java
 * 
 * Created on 10.01.2010
 */

package net.slightlymagic.laterna.magica.gui.util;


import static java.lang.Integer.*;
import static java.lang.String.*;
import static net.slightlymagic.laterna.magica.LaternaMagica.*;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import net.slightlymagic.laterna.magica.card.CardObject;
import net.slightlymagic.laterna.magica.card.CardTemplate;
import net.slightlymagic.laterna.magica.card.Printing;
import net.slightlymagic.laterna.magica.characteristics.MagicColor;
import net.slightlymagic.laterna.magica.mana.ManaSymbol;
import net.slightlymagic.laterna.magica.mana.impl.ManaFactoryImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.ComputationException;
import com.google.common.collect.MapMaker;


/**
 * The class ImageCache.
 * 
 * @version V1.0 10.01.2010
 * @author Clemens Koza
 */
public final class ImageCache {
    private static final Logger log = LoggerFactory.getLogger(ImageCache.class);
    
    private static ImageCache   INSTANCE;
    
    public static ImageCache getInstance() {
        if(INSTANCE == null) INSTANCE = new ImageCache();
        return INSTANCE;
    }
    
    
    private final Map<URI, BufferedImage> images;
    
    private ImageCache() {
        images = new MapMaker().softValues().makeComputingMap(new ImageFunction());
    }
    
    
    public URI getSymbolURI(String symbol) {
        if("{T}".equals(symbol) || "{Q}".equals(symbol)) {
            File f = PROPS().getFile("/net.slightlymagic.laterna/res/pics/symbols");
            f = new File(f, symbol.substring(1, 2) + ".png");
            return f.toURI();
        } else return getSymbolURI(ManaFactoryImpl.INSTANCE.parseSymbol(symbol));
    }
    
    public URI getSymbolURI(MagicColor c) {
        File f = PROPS().getFile("/net.slightlymagic.laterna/res/pics/symbols");
        f = new File(f, (c == null? 'X':c.getShortChar()) + ".png");
        return f.toURI();
    }
    
    public URI getSymbolURI(ManaSymbol s) {
        File f = PROPS().getFile("/net.slightlymagic.laterna/res/pics/symbols");
        f = new File(f, s.toString().replaceAll("[{}/]", "") + ".png");
        return f.toURI();
    }
    
    public BufferedImage getSymbol(String symbol) {
        return getImage(getSymbolURI(symbol));
    }
    
    public BufferedImage getSymbol(MagicColor c) {
        return getImage(getSymbolURI(c));
    }
    
    public BufferedImage getSymbol(ManaSymbol s) {
        return getImage(getSymbolURI(s));
    }
    
    public URI getCardURI(CardObject card) {
        return getCardURI(card.getPrinting());
    }
    
    public URI getCardURI(CardTemplate card) {
        return getCardURI(card.getPrintings().get(card.getPrintings().size() - 1));
    }
    
    public URI getCardURI(Printing card) {
        return getCardURI(card.getMultiverseID());
    }
    
    public URI getCardURI(int multiverseID) {
        File f = PROPS().getFile("/net.slightlymagic.laterna/res/pics/cards");
        f = new File(f, multiverseID + ".jpg");
        return f.toURI();
    }
    
    public BufferedImage getCard(CardObject card) {
        return getImage(getCardURI(card));
    }
    
    public BufferedImage getCard(CardTemplate card) {
        return getImage(getCardURI(card));
    }
    
    public BufferedImage getCard(Printing card) {
        return getImage(getCardURI(card));
    }
    
    public BufferedImage getCard(int multiverseID) {
        return getImage(getCardURI(multiverseID));
    }
    
    
    public URI setSize(URI uri, int width, int height) {
        String scheme = uri.getScheme();
        String authority = uri.getAuthority();
        String path = uri.getPath();
        String query = uri.getQuery();
        String fragment = uri.getFragment();
        
        if(query == null) {
            query = format("width=%d&height=%d", width, height);
        } else {
            if(query.contains("width=")) query = query.replaceFirst("width=\\d+", "width=" + width);
            else query += "&width=" + width;
            
            if(query.contains("height=")) query = query.replaceFirst("height=\\d+", "height=" + width);
            else query += "&height=" + width;
        }
        
        try {
            return new URI(scheme, authority, path, query, fragment);
        } catch(URISyntaxException ex) {
            throw new AssertionError(ex);
        }
    }
    
    public BufferedImage getImage(URI uri) {
        try {
            return images.get(uri);
        } catch(NullPointerException ex) {
            return null;
        } catch(ComputationException ex) {
            return null;
        }
    }
    
    private class ImageFunction implements Function<URI, BufferedImage> {
        public BufferedImage apply(URI uri) {
            log.debug(format("Caching new image: %s", uri));
            
            boolean fromCache = false;
            int width = -1, height = -1;
            
            {//process query
                String query = uri.getQuery();
                if(query != null) {
                    String scheme = uri.getScheme();
                    String authority = uri.getAuthority();
                    String path = uri.getPath();
                    String fragment = uri.getFragment();
                    try {
                        uri = new URI(scheme, authority, path, null, fragment);
                    } catch(URISyntaxException ex) {
                        throw new AssertionError(ex);
                    }
                    Matcher w = Pattern.compile("width=(\\d+)").matcher(query);
                    Matcher h = Pattern.compile("width=(\\d+)").matcher(query);
                    if(w.find()) width = parseInt(w.group(1));
                    if(h.find()) height = parseInt(h.group(1));
                    
                    fromCache = true;
                }
            }
            
            BufferedImage im;
            
            if(fromCache) im = getImage(uri);
            else try {
                im = ImageIO.read(uri.toURL());
            } catch(IOException ex) {
                throw new ComputationException(ex);
            }
            
            if(width != -1 || height != -1) {
                //TODO use a decent scaling algorithm
                if(width == -1) width = im.getWidth();
                if(height == -1) height = im.getHeight();
                BufferedImage im2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = (Graphics2D) im2.getGraphics();
                g.drawImage(
                        im,
                        AffineTransform.getScaleInstance(width / (double) im.getWidth(),
                                height / (double) im.getHeight()), null);
                g.dispose();
                im = im2;
            }
            
            return im;
        }
    }
}
