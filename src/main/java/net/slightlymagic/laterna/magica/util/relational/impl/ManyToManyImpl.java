/**
 * EditableManyToManyImpl.java
 * 
 * Created on 16.08.2010
 */

package net.slightlymagic.laterna.magica.util.relational.impl;


import static java.util.Collections.*;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.slightlymagic.laterna.magica.util.relational.ManyToMany;

import com.google.common.collect.AbstractIterator;


/**
 * The class EditableManyToManyImpl.
 * 
 * @version V0.0 16.08.2010
 * @author Clemens Koza
 * 
 * @param M The type represented by this side of the relationship
 * @param N The type represented by the other side of the relationship
 * @param T The central type of the relationship: An object which belongs to a pair of M and N objects
 */
public class ManyToManyImpl<M, N, T> extends Entity<M> implements ManyToMany<M, N, T> {
    private Map<ManyToManyImpl<N, M, T>, T> otherSide;
    private Map<ManyToManyImpl<N, M, T>, T> otherSideView;
    private Map<N, T>                       otherSideValuesView;
    
    public ManyToManyImpl() {
        this(null);
    }
    
    public ManyToManyImpl(M value) {
        super(value);
        
        otherSide = new HashMap<ManyToManyImpl<N, M, T>, T>();
        otherSideView = unmodifiableMap(otherSide);
        otherSideValuesView = new AbstractMap<N, T>() {
            private Set<Entry<N, T>> entrySet = new AbstractSet<Entry<N, T>>() {
                                                  @Override
                                                  public int size() {
                                                      return otherSide.size();
                                                  }
                                                  
                                                  @Override
                                                  public Iterator<Entry<N, T>> iterator() {
                                                      return new AbstractIterator<Entry<N, T>>() {
                                                          private Iterator<Entry<ManyToManyImpl<N, M, T>, T>> delegate = otherSide.entrySet().iterator();
                                                          
                                                          @Override
                                                          protected Entry<N, T> computeNext() {
                                                              if(!delegate.hasNext()) return endOfData();
                                                              final Entry<ManyToManyImpl<N, M, T>, T> e = delegate.next();
                                                              return new Entry<N, T>() {
                                                                  public N getKey() {
                                                                      return e.getKey().getValue();
                                                                  }
                                                                  
                                                                  public T getValue() {
                                                                      return e.getValue();
                                                                  }
                                                                  
                                                                  public T setValue(T value) {
                                                                      throw new UnsupportedOperationException();
                                                                  }
                                                                  
                                                                  @Override
                                                                  public int hashCode() {
                                                                      Object key = getKey(), val = getValue();
                                                                      return (key == null? 0:key.hashCode())
                                                                              ^ (val == null? 0:val.hashCode());
                                                                  }
                                                                  
                                                                  @Override
                                                                  public boolean equals(Object o) {
                                                                      if(!(o instanceof Entry)) return false;
                                                                      Entry<?, ?> other = (Entry<?, ?>) o;
                                                                      
                                                                      Object key = getKey(), val = getValue();
                                                                      if(key == null? other.getKey() != null:!key.equals(other.getKey())) return false;
                                                                      if(val == null? other.getKey() != null:!val.equals(other.getKey())) return false;
                                                                      return true;
                                                                  }
                                                              };
                                                          }
                                                      };
                                                  }
                                              };
            
            @Override
            public Set<Entry<N, T>> entrySet() {
                return entrySet;
            }
        };
    }
    
    public void add(ManyToMany<N, M, T> entity) {
        add(entity, null);
    }
    
    public void add(ManyToMany<N, M, T> entity, T center) {
        if(contains(entity)) throw new IllegalArgumentException("entity does already exist");
        addOrSet(entity, center);
    }
    
    public T set(ManyToMany<N, M, T> entity, T center) {
        if(!contains(entity)) throw new IllegalArgumentException("entity does already exist");
        return addOrSet(entity, center);
    }
    
    public T addOrSet(ManyToMany<N, M, T> e, T center) {
        ManyToManyImpl<N, M, T> entity = (ManyToManyImpl<N, M, T>) e;
        otherSide.put(entity, center);
        return entity.otherSide.put(this, center);
    }
    
    public void remove(ManyToMany<N, M, T> e) {
        ManyToManyImpl<N, M, T> entity = (ManyToManyImpl<N, M, T>) e;
        otherSide.remove(entity);
        entity.otherSide.remove(this);
    }
    
    public boolean contains(ManyToMany<N, M, T> entity) {
        return otherSide.containsKey(entity);
    }
    
    public T get(ManyToMany<N, M, T> entity) {
        return otherSide.get(entity);
    }
    
    public Map<ManyToManyImpl<N, M, T>, T> getOtherSide() {
        return otherSideView;
    }
    
    public Map<N, T> getOtherSideValues() {
        return otherSideValuesView;
    }
}
