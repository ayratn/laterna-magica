/**
 * EditableOneSideImpl.java
 * 
 * Created on 16.08.2010
 */

package net.slightlymagic.laterna.magica.util.relational.impl;


import static java.util.Collections.*;

import java.util.AbstractSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.slightlymagic.laterna.magica.util.relational.ManySide;
import net.slightlymagic.laterna.magica.util.relational.OneSide;

import com.google.common.collect.AbstractIterator;


/**
 * The class EditableOneSideImpl.
 * 
 * @version V0.0 16.08.2010
 * @author Clemens Koza
 */
public class OneSideImpl<O, M> extends Entity<O> implements OneSide<O, M> {
    Set<ManySideImpl<M, O>>         manySide;
    private Set<ManySideImpl<M, O>> manySideView;
    private Set<M>                  manySideValuesView;
    
    public OneSideImpl() {
        this(null);
    }
    
    public OneSideImpl(O value) {
        super(value);
        manySide = new HashSet<ManySideImpl<M, O>>();
        manySideView = unmodifiableSet(manySide);
        manySideValuesView = new AbstractSet<M>() {
            @Override
            public int size() {
                return manySide.size();
            }
            
            @Override
            public Iterator<M> iterator() {
                return new AbstractIterator<M>() {
                    private Iterator<ManySideImpl<M, O>> delegate = manySide.iterator();
                    
                    @Override
                    protected M computeNext() {
                        if(!delegate.hasNext()) return endOfData();
                        return delegate.next().getValue();
                    }
                };
            }
        };
    }
    
    public void add(ManySide<M, O> e) {
        ManySideImpl<M, O> entity = (ManySideImpl<M, O>) e;
        if(entity.oneSide != null) throw new IllegalArgumentException("entity already has a relationship");
        entity.oneSide = this;
        manySide.add(entity);
    }
    
    public void remove(ManySide<M, O> e) {
        ManySideImpl<M, O> entity = (ManySideImpl<M, O>) e;
        if(manySide.remove(entity)) entity.oneSide = null;
    }
    
    public boolean contains(ManySide<M, O> entity) {
        return manySide.contains(entity);
    }
    
    public Set<ManySideImpl<M, O>> getManySide() {
        return manySideView;
    }
    
    public Set<M> getManySideValues() {
        return manySideValuesView;
    }
}
