/**
 * PlayInformationFunction.java
 * 
 * Created on 28.04.2010
 */

package net.slightlymagic.laterna.magica.action.play.impl;


import static java.util.Arrays.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.slightlymagic.laterna.magica.action.play.PlayAction;
import net.slightlymagic.laterna.magica.action.play.PlayInformation;

import com.google.common.base.Function;


/**
 * The class PlayInformationFunction.
 * 
 * @version V0.0 28.04.2010
 * @author Clemens Koza
 */
public class PlayInformationFunction<T extends PlayAction> implements Function<T, PlayInformation>, Serializable {
    private static final long                                    serialVersionUID = -4638566991580083077L;
    
    private List<Function<? super T, ? extends PlayInformation>> delegates;
    
    public PlayInformationFunction() {
        delegates = new ArrayList<Function<? super T, ? extends PlayInformation>>();
    }
    
    public PlayInformationFunction(List<Function<? super T, ? extends PlayInformation>> delegates) {
        this.delegates = delegates;
    }
    
    public PlayInformationFunction(Function<? super T, ? extends PlayInformation>... delegates) {
        this();
        getDelegates().addAll(asList(delegates));
    }
    
    public List<Function<? super T, ? extends PlayInformation>> getDelegates() {
        return delegates;
    }
    
    public PlayInformation apply(T from) {
        List<PlayInformation> delegates = new ArrayList<PlayInformation>(this.delegates.size());
        for(Function<? super T, ? extends PlayInformation> delegate:this.delegates)
            delegates.add(delegate.apply(from));
        return new AbstractPlayInformation(delegates, from);
    }
}
