/**
 * MagicaPredicates.java
 * 
 * Created on 10.10.2009
 */

package net.slightlymagic.laterna.magica.util;


import static com.google.common.base.Suppliers.*;
import static java.util.Arrays.*;

import java.io.Serializable;
import java.util.List;

import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.card.CardObject;
import net.slightlymagic.laterna.magica.card.State.StateType;
import net.slightlymagic.laterna.magica.characteristic.ObjectCharacteristics;
import net.slightlymagic.laterna.magica.characteristics.CardType;
import net.slightlymagic.laterna.magica.characteristics.MagicColor;
import net.slightlymagic.laterna.magica.characteristics.SubType;
import net.slightlymagic.laterna.magica.characteristics.SuperType;
import net.slightlymagic.laterna.magica.player.Player;
import net.slightlymagic.laterna.magica.zone.Zone;
import net.slightlymagic.laterna.magica.zone.Zone.Zones;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;


/**
 * The class MagicaPredicates. Provides factory methods to quickly generating general-purpose predicates. For more
 * control, there are a few nested implementation classes in this class.
 * 
 * @version V0.0 10.10.2009
 * @author Clemens Koza
 */
public final class MagicaPredicates {
    private MagicaPredicates() {}
    
    /**
     * Returns a matcher for a whole {@link CardObject} based on a matcher for a single
     * {@link ObjectCharacteristics} .
     */
    public static Predicate<MagicObject> card(Predicate<? super ObjectCharacteristics> matcher) {
        return new CardPredicate(matcher);
    }
    
    
    /**
     * Returns a matcher that matches if a {@link ObjectCharacteristics} contains all of the given elements.
     */
    public static Predicate<ObjectCharacteristics> and(MagicColor... colors) {
        return Predicates.and(getPredicates(colors));
    }
    
    /**
     * Returns a matcher that matches if a {@link ObjectCharacteristics} contains all of the given elements.
     */
    public static Predicate<ObjectCharacteristics> and(SuperType... types) {
        return Predicates.and(getPredicates(types));
    }
    
    /**
     * Returns a matcher that matches if a {@link ObjectCharacteristics} contains all of the given elements.
     */
    public static Predicate<ObjectCharacteristics> and(CardType... types) {
        return Predicates.and(getPredicates(types));
    }
    
    /**
     * Returns a matcher that matches if a {@link ObjectCharacteristics} contains all of the given elements.
     */
    public static Predicate<ObjectCharacteristics> and(SubType... types) {
        return Predicates.and(getPredicates(types));
    }
    
    
    /**
     * Returns a matcher that matches if a {@link ObjectCharacteristics} contains one of the given elements.
     */
    public static Predicate<ObjectCharacteristics> or(MagicColor... colors) {
        return Predicates.or(getPredicates(colors));
    }
    
    /**
     * Returns a matcher that matches if a {@link ObjectCharacteristics} contains one of the given elements.
     */
    public static Predicate<ObjectCharacteristics> or(SuperType... types) {
        return Predicates.or(getPredicates(types));
    }
    
    /**
     * Returns a matcher that matches if a {@link ObjectCharacteristics} contains one of the given elements.
     */
    public static Predicate<ObjectCharacteristics> or(CardType... types) {
        return Predicates.or(getPredicates(types));
    }
    
    /**
     * Returns a matcher that matches if a {@link ObjectCharacteristics} contains one of the given elements.
     */
    public static Predicate<ObjectCharacteristics> or(SubType... types) {
        return Predicates.or(getPredicates(types));
    }
    
    private static List<Predicate<ObjectCharacteristics>> getPredicates(Object[] values) {
        return Lists.transform(asList(values),
                new SerializableFunction<Object, Predicate<ObjectCharacteristics>>() {
                    private static final long serialVersionUID = 2933215975132223534L;
                    
                    public Predicate<ObjectCharacteristics> apply(Object from) {
                        return getPredicate(from);
                    }
                });
    }
    
    private static Predicate<ObjectCharacteristics> getPredicate(Object value) {
        if(value instanceof MagicColor) return has((MagicColor) value);
        else if(value instanceof SuperType) return has((SuperType) value);
        else if(value instanceof CardType) return has((CardType) value);
        else if(value instanceof SubType) return has((SubType) value);
        else throw new AssertionError(value);
    }
    
    /**
     * Returns a matcher that matches if a {@link ObjectCharacteristics} contains the given element.
     */
    public static Predicate<ObjectCharacteristics> has(final MagicColor color) {
        return new SerializablePredicate<ObjectCharacteristics>() {
            private static final long serialVersionUID = 7394831706500727747L;
            
            public boolean apply(ObjectCharacteristics o) {
                return o.hasColor(color);
            }
        };
    }
    
    /**
     * Returns a matcher that matches if a {@link ObjectCharacteristics} contains the given element.
     */
    public static Predicate<ObjectCharacteristics> has(final SuperType type) {
        return new SerializablePredicate<ObjectCharacteristics>() {
            private static final long serialVersionUID = -2261337590833493881L;
            
            public boolean apply(ObjectCharacteristics o) {
                return o.hasSuperType(type);
            }
        };
    }
    
    /**
     * Returns a matcher that matches if a {@link ObjectCharacteristics} contains the given element.
     */
    public static Predicate<ObjectCharacteristics> has(final CardType type) {
        return new SerializablePredicate<ObjectCharacteristics>() {
            private static final long serialVersionUID = 5018348904026289824L;
            
            public boolean apply(ObjectCharacteristics o) {
                return o.hasType(type);
            }
        };
    }
    
    /**
     * Returns a matcher that matches if a {@link ObjectCharacteristics} contains the given element.
     */
    public static Predicate<ObjectCharacteristics> has(final SubType type) {
        return new SerializablePredicate<ObjectCharacteristics>() {
            private static final long serialVersionUID = 4210555047167238045L;
            
            public boolean apply(ObjectCharacteristics o) {
                return o.hasSubType(type);
            }
        };
    }
    
    public static Predicate<MagicObject> isIn(final Supplier<Zone.Zones> zone) {
        return new SerializablePredicate<MagicObject>() {
            private static final long serialVersionUID = -8732288206558924591L;
            
            public boolean apply(MagicObject o) {
                return o.getZone().getType() == zone.get();
            }
        };
    }
    
    public static Predicate<MagicObject> controller(final Supplier<Player> controller) {
        return new SerializablePredicate<MagicObject>() {
            private static final long serialVersionUID = -682324865643610734L;
            
            public boolean apply(MagicObject o) {
                return o.getController() == controller.get();
            }
        };
    }
    
    public static Predicate<MagicObject> owner(final Supplier<Player> owner) {
        return new SerializablePredicate<MagicObject>() {
            private static final long serialVersionUID = 1159225048829581498L;
            
            public boolean apply(MagicObject o) {
                return o.getOwner() == owner.get();
            }
        };
    }
    
    public static Predicate<MagicObject> is(final StateType state) {
        return new SerializablePredicate<MagicObject>() {
            private static final long serialVersionUID = 1159225048829581498L;
            
            public boolean apply(MagicObject o) {
                return (o instanceof CardObject) && ((CardObject) o).getState() != null
                        && ((CardObject) o).getState().getState(state);
            }
        };
    }
    
    public static final Predicate<MagicObject> summoningSick = SummoningSick.INSTANCE;
    
    private static enum SummoningSick implements SerializablePredicate<MagicObject> {
        INSTANCE;
        
        private static final Predicate<MagicObject> creature = Predicates.and(card(has(CardType.CREATURE)),
                                                                     isIn(ofInstance(Zones.BATTLEFIELD)));
        
        @Override
        public boolean apply(MagicObject input) {
            if(!creature.apply(input)) return false;
            //TODO check haste
            boolean haste = false;
            
            return !(input.getCounter("summoningSickness").getCount() == 0 || haste);
        }
    }
    
    /**
     * The class CardPredicate. This class performs a match operation on a card based on a
     * {@link ObjectCharacteristics} predicate, as specified in {@magic.ruleRef 20100716/R7086}.
     * 
     * @version V1.0 10.10.2009
     * @author Clemens Koza
     */
    private static class CardPredicate implements SerializablePredicate<MagicObject> {
        private static final long                        serialVersionUID = -6788854536917781953L;
        
        private Predicate<? super ObjectCharacteristics> predicate;
        
        public CardPredicate(Predicate<? super ObjectCharacteristics> predicate) {
            if(predicate == null) throw new IllegalArgumentException("predicate == null");
            this.predicate = predicate;
        }
        
        /**
         * Performs the match operation of {@link #predicate} on the {@link ObjectCharacteristics} of the
         * {@link MagicObject}, until one match succeeded. Returns true if at least one match operation returned
         * true. Throws an {@link IllegalStateException} if the matcher was not set.
         */
        public boolean apply(MagicObject o) {
            for(ObjectCharacteristics ch:o.getCharacteristics())
                if(predicate.apply(ch)) return true;
            return false;
        }
    }
    
    private static interface SerializablePredicate<T> extends Predicate<T>, Serializable {}
    
    private static interface SerializableFunction<F, T> extends Function<F, T>, Serializable {}
}
