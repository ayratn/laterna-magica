/**
 * MagicaFunctions.java
 * 
 * Created on 18.08.2010
 */

package net.slightlymagic.laterna.magica.util;


import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.player.Player;

import com.google.common.base.Function;


/**
 * The class MagicaFunctions.
 * 
 * @version V0.0 18.08.2010
 * @author Clemens Koza
 */
public class MagicaFunctions {
    public static final Function<MagicObject, Player> controller = new Function<MagicObject, Player>() {
                                                                     @Override
                                                                     public Player apply(MagicObject from) {
                                                                         return from.getController();
                                                                     }
                                                                 };
}
