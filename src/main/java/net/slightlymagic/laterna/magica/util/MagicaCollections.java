/**
 * MagicaCollections.java
 * 
 * Created on 25.03.2010
 */

package net.slightlymagic.laterna.magica.util;


import java.io.Serializable;
import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.AbstractSequentialList;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.RandomAccess;
import java.util.Set;

import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.GameContent;
import net.slightlymagic.laterna.magica.edit.Edit;
import net.slightlymagic.laterna.magica.edit.impl.EntrySetIteratorRemoveEdit;
import net.slightlymagic.laterna.magica.edit.impl.ListAddEdit;
import net.slightlymagic.laterna.magica.edit.impl.ListIteratorAddEdit;
import net.slightlymagic.laterna.magica.edit.impl.ListIteratorRemoveEdit;
import net.slightlymagic.laterna.magica.edit.impl.ListIteratorSetEdit;
import net.slightlymagic.laterna.magica.edit.impl.ListRemoveEdit;
import net.slightlymagic.laterna.magica.edit.impl.ListSetEdit;
import net.slightlymagic.laterna.magica.edit.impl.MapPutEdit;
import net.slightlymagic.laterna.magica.edit.impl.SetAddEdit;
import net.slightlymagic.laterna.magica.edit.impl.SetIteratorRemoveEdit;
import net.slightlymagic.laterna.magica.edit.impl.SetRemoveEdit;


/**
 * The class MagicaCollections. This class provides utility functions to implement collections that encapsulate
 * modifications id {@link Edit}s.
 * 
 * @version V0.0 25.03.2010
 * @author Clemens Koza
 */
public final class MagicaCollections {
    private MagicaCollections() {}
    
    /**
     * Returns a set that encapsulates all modifications to it into {@link Edit}s, therefore enabling undo on that
     * set. The set will be a {@link GameContent}, and will be {@link Serializable} if the wrapped set is.
     */
    public static <E> Set<E> editableSet(Game game, Set<E> set) {
        return new EditableSet<E>(game, set);
    }
    
    /**
     * Returns a list that encapsulates all modifications to it into {@link Edit}s, therefore enabling undo on that
     * list. Depending on if the list implements {@link RandomAccess}, an implementation is chosen. The list will
     * be a {@link GameContent}, and will be {@link Serializable} and {@link RandomAccess} if the wrapped list is.
     */
    public static <E> List<E> editableList(Game game, List<E> list) {
        if(list instanceof RandomAccess) {
            return new EditableList<E>(game, list);
        } else {
            return new EditableSequentialList<E>(game, list);
        }
    }
    
    /**
     * Returns a map that encapsulates all modifications to it into {@link Edit}s, therefore enabling undo on that
     * map. The map will be a {@link GameContent}, and will be {@link Serializable} if the wrapped map is.
     */
    public static <K, V> Map<K, V> editableMap(Game game, Map<K, V> map) {
        return new EditableMap<K, V>(game, map);
    }
    
    private static class EditableSet<E> extends AbstractSet<E> implements GameContent, Serializable {
        private static final long serialVersionUID = 7728087988927063221L;
        
        private Game              game;
        private Set<E>            delegate;
        
        public EditableSet(Game game, Set<E> set) {
            this.game = game;
            delegate = set;
        }
        
        public Game getGame() {
            return game;
        }
        
        @Override
        public boolean contains(Object o) {
            return delegate.contains(o);
        }
        
        @Override
        public boolean add(E o) {
            SetAddEdit<E> e = new SetAddEdit<E>(getGame(), delegate, o);
            e.execute();
            return e.isAdded();
        };
        
        @Override
        public boolean remove(Object o) {
            SetRemoveEdit<E> e = new SetRemoveEdit<E>(getGame(), delegate, o);
            e.execute();
            return e.isRemoved();
        }
        
        @Override
        public Iterator<E> iterator() {
            return new Iterator<E>() {
                private final Iterator<E> it = delegate.iterator();
                private boolean           hasLast;
                private E                 last;
                
                public boolean hasNext() {
                    return it.hasNext();
                }
                
                public E next() {
                    last = it.next();
                    hasLast = true;
                    return last;
                }
                
                public void remove() {
                    if(!hasLast) throw new IllegalStateException();
                    new SetIteratorRemoveEdit<E>(getGame(), delegate, it, last).execute();
                }
            };
        }
        
        @Override
        public int size() {
            return delegate.size();
        }
    }
    
    private static class EditableList<E> extends AbstractList<E> implements GameContent, RandomAccess, Serializable {
        private static final long serialVersionUID = -3504821893908696025L;
        
        private Game              game;
        private List<E>           delegate;
        
        public EditableList(Game game, List<E> list) {
            this.game = game;
            delegate = list;
        }
        
        public Game getGame() {
            return game;
        }
        
        @Override
        public E get(int index) {
            return delegate.get(index);
        }
        
        @Override
        public int size() {
            return delegate.size();
        }
        
        @Override
        public E set(int index, E element) {
            ListSetEdit<E> e = new ListSetEdit<E>(game, delegate, index, element);
            e.execute();
            return e.getOldValue();
        };
        
        @Override
        public void add(int index, E element) {
            new ListAddEdit<E>(game, delegate, index, element).execute();
        };
        
        @Override
        public E remove(int index) {
            ListRemoveEdit<E> e = new ListRemoveEdit<E>(game, delegate, index);
            e.execute();
            return e.getOldValue();
        }
    }
    
    private static class EditableSequentialList<E> extends AbstractSequentialList<E> implements GameContent, Serializable {
        private static final long serialVersionUID = -8997460304014797098L;
        
        private Game              game;
        private List<E>           delegate;
        
        public EditableSequentialList(Game game, List<E> list) {
            this.game = game;
            delegate = list;
        }
        
        public Game getGame() {
            return game;
        }
        
        @Override
        public ListIterator<E> listIterator(final int index) {
            return new ListIterator<E>() {
                private final ListIterator<E> it        = delegate.listIterator(index);
                private int                   lastIndex = -1;
                private E                     lastValue = null;
                
                public boolean hasNext() {
                    return it.hasNext();
                }
                
                public boolean hasPrevious() {
                    return it.hasPrevious();
                }
                
                public E next() {
                    lastIndex = nextIndex();
                    return lastValue = it.next();
                }
                
                public int nextIndex() {
                    return it.nextIndex();
                }
                
                public E previous() {
                    lastIndex = previousIndex();
                    return lastValue = it.previous();
                }
                
                public int previousIndex() {
                    return it.previousIndex();
                }
                
                public void add(E o) {
                    new ListIteratorAddEdit<E>(getGame(), delegate, it, o).execute();
                }
                
                public void remove() {
                    new ListIteratorRemoveEdit<E>(getGame(), delegate, it, lastIndex, lastValue).execute();
                }
                
                public void set(E o) {
                    new ListIteratorSetEdit<E>(getGame(), delegate, it, lastIndex, lastValue, o).execute();
                }
            };
        }
        
        @Override
        public int size() {
            return delegate.size();
        }
    }
    
    private static class EditableMap<K, V> extends AbstractMap<K, V> implements GameContent, Serializable {
        private static final long serialVersionUID = 4032087477448965103L;
        
        private Game              game;
        private Map<K, V>         delegate;
        private Set<Entry<K, V>>  entrySet;
        
        public EditableMap(Game game, Map<K, V> map) {
            this.game = game;
            delegate = map;
            entrySet = new EntrySet();
        }
        
        public Game getGame() {
            return game;
        }
        
        @Override
        public V put(K key, V value) {
            MapPutEdit<K, V> e = new MapPutEdit<K, V>(getGame(), delegate, key, value);
            e.execute();
            return e.getOldValue();
        }
        
        @Override
        public Set<java.util.Map.Entry<K, V>> entrySet() {
            return entrySet;
        }
        
        private final class EntrySet extends AbstractSet<Entry<K, V>> implements Serializable {
            private static final long      serialVersionUID = -780485106953107075L;
            private final Set<Entry<K, V>> delegate         = EditableMap.this.delegate.entrySet();
            
            @Override
            public int size() {
                return entrySet.size();
            }
            
            @Override
            public Iterator<Entry<K, V>> iterator() {
                return new Iterator<Entry<K, V>>() {
                    private final Iterator<Entry<K, V>> it = delegate.iterator();
                    private boolean                     hasLast;
                    private Entry<K, V>                 last;
                    
                    public boolean hasNext() {
                        return it.hasNext();
                    }
                    
                    public Entry<K, V> next() {
                        last = it.next();
                        hasLast = true;
                        return last;
                    }
                    
                    public void remove() {
                        if(!hasLast) throw new IllegalStateException();
                        hasLast = false;
                        new EntrySetIteratorRemoveEdit<K, V>(getGame(), EditableMap.this.delegate, it,
                                last.getKey(), last.getValue()).execute();
                    }
                };
            }
        }
    }
}
