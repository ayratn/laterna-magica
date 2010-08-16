/**
 * EditableOneSideImpl.java
 * 
 * Created on 16.08.2010
 */

package net.slightlymagic.laterna.magica.util.relational.editableImpl;


import static java.util.Collections.*;
import static net.slightlymagic.laterna.magica.util.MagicaCollections.*;

import java.util.AbstractSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.util.relational.ManySide;
import net.slightlymagic.laterna.magica.util.relational.OneSide;

import com.google.common.collect.AbstractIterator;


/**
 * The class EditableOneSideImpl.
 * 
 * @version V0.0 16.08.2010
 * @author Clemens Koza
 */
public class EditableOneSideImpl<O, M> extends EditableEntity<O> implements OneSide<O, M> {
    final Set<EditableManySideImpl<M, O>>         manySide;
    private final Set<EditableManySideImpl<M, O>> manySideView;
    private final Set<M>                          manySideValuesView;
    
    public EditableOneSideImpl(Game game) {
        this(game, null);
    }
    
    public EditableOneSideImpl(Game game, O value) {
        super(game, value);
        manySide = editableSet(getGame(), new HashSet<EditableManySideImpl<M, O>>());
        manySideView = unmodifiableSet(manySide);
        manySideValuesView = new AbstractSet<M>() {
            @Override
            public int size() {
                return manySide.size();
            }
            
            @Override
            public Iterator<M> iterator() {
                return new AbstractIterator<M>() {
                    private Iterator<EditableManySideImpl<M, O>> delegate = manySide.iterator();
                    
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
        EditableManySideImpl<M, O> entity = (EditableManySideImpl<M, O>) e;
        if(entity.oneSide.getValue() != null) throw new IllegalArgumentException(
                "Entity already has a relationship");
        entity.oneSide.setValue(this);
        manySide.add(entity);
    }
    
    public void remove(ManySide<M, O> e) {
        EditableManySideImpl<M, O> entity = (EditableManySideImpl<M, O>) e;
        if(manySide.remove(entity)) entity.oneSide.setValue(null);
    }
    
    public boolean contains(ManySide<M, O> entity) {
        return manySide.contains(entity);
    }
    
    public Set<EditableManySideImpl<M, O>> getManySide() {
        return manySideView;
    }
    
    public Set<M> getManySideValues() {
        return manySideValuesView;
    }
}
