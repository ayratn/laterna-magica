/**
 * Relations.java
 * 
 * Created on 16.08.2010
 */

package net.slightlymagic.laterna.magica.util.relational;


import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.util.relational.editableImpl.EditableManySideImpl;
import net.slightlymagic.laterna.magica.util.relational.editableImpl.EditableManyToManyImpl;
import net.slightlymagic.laterna.magica.util.relational.editableImpl.EditableOneSideImpl;
import net.slightlymagic.laterna.magica.util.relational.editableImpl.EditableOneToOneImpl;
import net.slightlymagic.laterna.magica.util.relational.impl.ManySideImpl;
import net.slightlymagic.laterna.magica.util.relational.impl.ManyToManyImpl;
import net.slightlymagic.laterna.magica.util.relational.impl.OneSideImpl;
import net.slightlymagic.laterna.magica.util.relational.impl.OneToOneImpl;


/**
 * The class Relations.
 * 
 * @version V0.0 16.08.2010
 * @author Clemens Koza
 */
public final class Relations {
    private Relations() {}
    
    public static <M, N, T> ManyToMany<M, N, T> manyToMany(M value) {
        return new ManyToManyImpl<M, N, T>(value);
    }
    
    public static <O, M> OneSide<O, M> oneSide(O value) {
        return new OneSideImpl<O, M>(value);
    }
    
    public static <M, O> ManySide<M, O> manySide(M value) {
        return new ManySideImpl<M, O>(value);
    }
    
    public static <A, B> OneToOne<A, B> oneToOne(A value) {
        return new OneToOneImpl<A, B>(value);
    }
    
    public static <M, N, T> ManyToMany<M, N, T> manyToMany(Game game, M value) {
        return new EditableManyToManyImpl<M, N, T>(game, value);
    }
    
    public static <O, M> OneSide<O, M> oneSide(Game game, O value) {
        return new EditableOneSideImpl<O, M>(game, value);
    }
    
    public static <M, O> ManySide<M, O> manySide(Game game, M value) {
        return new EditableManySideImpl<M, O>(game, value);
    }
    
    public static <A, B> OneToOne<A, B> oneToOne(Game game, A value) {
        return new EditableOneToOneImpl<A, B>(game, value);
    }
}
