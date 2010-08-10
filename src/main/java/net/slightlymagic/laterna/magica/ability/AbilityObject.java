/**
 * AbilityObject.java
 * 
 * Created on 21.04.2010
 */

package net.slightlymagic.laterna.magica.ability;


import java.util.List;

import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.characteristic.AbilityCharacteristics;



/**
 * The class AbilityObject.
 * 
 * @version V0.0 21.04.2010
 * @author Clemens Koza
 */
public interface AbilityObject extends MagicObject {
    /**
     * Returns the object the ability is on
     */
    public MagicObject getObject();
    
    /**
     * Returns the ability that is represented by this object
     */
    public NonStaticAbility getAbility();
    
    public List<? extends AbilityCharacteristics> getCharacteristics();
    
    /**
     * An ability object should return its ability's text
     */
    @Override
    public String toString();
}
