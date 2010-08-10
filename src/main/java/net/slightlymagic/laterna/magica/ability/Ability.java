/**
 * Ability.java
 * 
 * Created on 14.04.2010
 */

package net.slightlymagic.laterna.magica.ability;


import java.io.Serializable;


/**
 * The class Ability. An ability is the meaning of rules text printed on a card. There are three types of
 * abilities: Static, triggered and activated.
 * 
 * Ability is only a marker interface, implementations use the type-specific subinterfaces.
 * 
 * @version V0.0 14.04.2010
 * @author Clemens Koza
 */
public interface Ability extends Serializable {
    /**
     * An ability's toString should return the ability's text
     */
    @Override
    public String toString();
}
