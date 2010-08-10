/**
 * CopiableValues.java
 * 
 * Created on 31.07.2009
 */

package net.slightlymagic.laterna.magica.card.copy;


import net.slightlymagic.laterna.magica.card.CardParts;


/**
 * The class CopiableValues. Copiable values stores the values used in the copying process. CopiableValues are
 * inheritable. That means that there may be more than one CopiableValues per object. The one applied last contains
 * the information to use, but may inherit some of these values to an earlier one. The first CopiableValues is
 * derived from an object's {@link CardParts}.
 * 
 * @version V0.0 31.07.2009
 * @author Clemens Koza
 */
public interface CopiableValues {

}
