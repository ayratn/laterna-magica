/**
 * ListenableCollections.java
 * 
 * Created on 25.04.2010
 */

package net.slightlymagic.laterna.magica.util;


import java.io.Serializable;
import java.util.AbstractList;
import java.util.AbstractSequentialList;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;


/**
 * The class ListenableCollections supports applications that need to listen for modifications on different
 * collections. Unlike most listener models, a listenable collection does not have public methods for adding or
 * removing listeners. Instead, the wrapping methods take exactly one listener, because listening to collection is
 * low-level. That single listener will usually s managing multiple high-level listeners.
 * 
 * @version V0.0 25.04.2010
 * @author Clemens Koza
 */
public final class ListenableCollections {
    private ListenableCollections() {}
    
    public static <E> List<E> listenableList(List<E> list, ListListener<E> listener) {
        if(list instanceof RandomAccess) return new ListenableList<E>(list, listener);
        else return new ListenableSequentialList<E>(list, listener);
    }
    
    public static interface ListListener<E> extends Serializable {
        /**
         * Notified after a value was added to the list.
         */
        public void add(int index, E newValue);
        
        /**
         * Notified after a value in the list was changed.
         */
        public void set(int index, E oldValue, E newValue);
        
        /**
         * Notified after a value was removed from the list.
         */
        public void remove(int index, E oldValue);
    }
    
    private static class ListenableList<E> extends AbstractList<E> implements RandomAccess, Serializable {
        private static final long serialVersionUID = 8622608480525537692L;
        
        private List<E>           delegate;
        private ListListener<E>   listener;
        
        public ListenableList(List<E> delegate, ListListener<E> listener) {
            this.delegate = delegate;
            this.listener = listener;
        }
        
        @Override
        public void add(int index, E e) {
            delegate.add(index, e);
            listener.add(index, e);
        }
        
        @Override
        public E set(int index, E element) {
            E e = delegate.set(index, element);
            listener.set(index, e, element);
            return e;
        }
        
        @Override
        public E remove(int index) {
            E e = delegate.remove(index);
            listener.remove(index, e);
            return e;
        }
        
        @Override
        public E get(int index) {
            return delegate.get(index);
        }
        
        @Override
        public int size() {
            return delegate.size();
        }
    }
    
    private static class ListenableSequentialList<E> extends AbstractSequentialList<E> implements Serializable {
        private static final long serialVersionUID = 3630474556578001885L;
        
        private List<E>           delegate;
        private ListListener<E>   listener;
        
        public ListenableSequentialList(List<E> delegate, ListListener<E> listener) {
            this.delegate = delegate;
            this.listener = listener;
        }
        
        @Override
        public ListIterator<E> listIterator(final int index) {
            return new ListIterator<E>() {
                private final ListIterator<E> it = delegate.listIterator(index);
                private int                   lastIndex;
                private E                     lastValue;
                
                public boolean hasNext() {
                    return it.hasNext();
                }
                
                public boolean hasPrevious() {
                    return it.hasPrevious();
                }
                
                public E next() {
                    lastIndex = it.nextIndex();
                    lastValue = it.next();
                    return lastValue;
                }
                
                public int nextIndex() {
                    return it.nextIndex();
                }
                
                public E previous() {
                    lastIndex = it.previousIndex();
                    lastValue = it.previous();
                    return lastValue;
                }
                
                public int previousIndex() {
                    return it.previousIndex();
                }
                
                public void add(E o) {
                    it.add(o);
                    listener.add(previousIndex(), o);
                }
                
                public void set(E o) {
                    it.set(o);
                    listener.set(lastIndex, lastValue, o);
                }
                
                public void remove() {
                    it.remove();
                    listener.remove(lastIndex, lastValue);
                }
            };
        }
        
        @Override
        public int size() {
            return delegate.size();
        }
    }
}
