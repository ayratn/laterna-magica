/**
 * OneToOne.java
 * 
 * Created on 16.08.2010
 */

package net.slightlymagic.laterna.magica.util.relational;


/**
 * The class OneToOne.
 * 
 * @version V0.0 16.08.2010
 * @author Clemens Koza
 * @param <A>
 * @param <B>
 */
public interface OneToOne<A, B> {
    public void setOtherSide(OneToOne<B, A> entity);
    
    public OneToOne<B, A> getOtherSide();
    
    public B getOtherSideValue();
}
