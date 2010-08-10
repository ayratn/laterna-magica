/**
 * FactoryFunction.java
 * 
 * Created on 20.04.2010
 */

package net.slightlymagic.laterna.magica.util;


import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import net.slightlymagic.laterna.magica.action.play.CastAction;


import com.google.common.base.Function;


/**
 * The class FactoryFunction. This class is intended to be used as a factory that takes a single object as a
 * parameter to instantiate the manufactured object. It assumes that there's a class that can be configured using
 * some static (per-factory) object, and another dynamic (per-instance) object. This class can work with classes
 * that provide a constructor which can take these two objects as its only parameters, or that only takes a dynamic
 * object.
 * 
 * <pre>
 * class Example {
 *     public Example(A a, B b) {}
 * }
 * </pre>
 * 
 * <pre>
 * FactoryFunction&lt;A, B, Example&gt; fun = FactoryFunction.getInstance(A.class, new A(), B.class, Example.class);
 * Example = fun.apply(new B());
 * //the produced object is equivalent to
 * new Example(new A(), new B());
 * </pre>
 * 
 * By encapsulating the needed arguments into the parameter classes as needed, any desired combination of static
 * and dynamic arguments is possible.
 * 
 * @param <C> The configuration class
 * @param <F> The input class
 * @param <T> The output class
 * 
 * @version V0.0 20.04.2010
 * @author Clemens Koza
 */
public class FactoryFunction<C, F, T> implements Function<F, T>, Serializable {
    private static final long serialVersionUID = 5397924959391135287L;
    
    /**
     * Shorter to write because you don't have to declare the type parameters yourself
     */
    public static <F, T> FactoryFunction<Object, F, T> getInstance(Class<F> f, Class<T> t) {
        return new FactoryFunction<Object, F, T>(f, t);
    }
    
    /**
     * Shorter to write because you don't have to declare the type parameters yourself
     */
    public static <C, F, T> FactoryFunction<C, F, T> getInstance(Class<C> c, C config, Class<F> f, Class<T> t) {
        return new FactoryFunction<C, F, T>(c, config, f, t);
    }
    
    private Class<C> c;
    private Class<F> f;
    private Class<T> t;
    private C        config;
    
    public FactoryFunction(Class<F> f, Class<T> t) throws SecurityException {
        this(null, null, f, t);
    }
    
    /**
     * @param clazz The class to construct objects of
     * 
     * @throws SecurityException if {@link Class#getConstructor(Class...)} throws it
     * @throws IllegalArgumentException if a constroctor for a {@link CastAction} parameter doesn't exist
     */
    public FactoryFunction(Class<C> c, C config, Class<F> f, Class<T> t) throws SecurityException {
        try {
            //check the class is instantiable
            int mod = t.getModifiers();
            if(Modifier.isAbstract(mod) || Modifier.isInterface(mod)) throw new IllegalArgumentException(
                    t.getName() + " is not instantiable");
            //check for existing constructor
            if(c != null) t.getConstructor(c, f);
            else t.getConstructor(f);
            
            this.c = c;
            this.f = f;
            this.t = t;
            this.config = config;
        } catch(NoSuchMethodException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
    
    
    public T apply(F from) {
        try {
            if(c != null) {
                return t.getConstructor(c, f).newInstance(config, from);
            } else {
                return t.getConstructor(f).newInstance(from);
            }
        } catch(NoSuchMethodException ex) {
            //was checked already
            throw new AssertionError(ex);
        } catch(InstantiationException ex) {
            throw new RuntimeException(ex);
        } catch(IllegalAccessException ex) {
            throw new RuntimeException(ex);
        } catch(InvocationTargetException ex) {
            if(ex.getCause() instanceof RuntimeException) throw (RuntimeException) ex.getCause();
            else throw new RuntimeException(ex.getCause());
        }
    }
}
