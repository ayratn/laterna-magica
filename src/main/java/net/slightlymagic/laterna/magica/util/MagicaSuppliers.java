/**
 * MagicaSuppliers.java
 * 
 * Created on 22.03.2010
 */

package net.slightlymagic.laterna.magica.util;


import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.GameContent;
import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.player.Player;
import net.slightlymagic.laterna.magica.zone.Zone;

import com.google.common.base.Supplier;


/**
 * The class MagicaSuppliers.
 * 
 * @version V0.0 22.03.2010
 * @author Clemens Koza
 */
public final class MagicaSuppliers {
    private MagicaSuppliers() {}
    
    /**
     * Returns a supplier that always returns the zone the supplied object currently is in.
     */
    public static Supplier<Zone> zone(final Supplier<? extends MagicObject> object) {
        return new Supplier<Zone>() {
            public Zone get() {
                return object.get().getZone();
            }
        };
    }
    
    /**
     * Returns a supplier that always returns the current owner of the supplied object
     */
    public static Supplier<Player> owner(final Supplier<? extends MagicObject> object) {
        return new Supplier<Player>() {
            public Player get() {
                return object.get().getOwner();
            }
        };
    }
    
    /**
     * Returns a supplier that always returns the current controller of the supplied object
     */
    public static Supplier<Player> controller(final Supplier<? extends MagicObject> object) {
        return new Supplier<Player>() {
            public Player get() {
                return object.get().getController();
            }
        };
    }
    
    /**
     * Returns a supplier that always returns the current "you" for the supplied object. That is, if the object has
     * a controller, its controller, or its owner otherwise.
     */
    public static Supplier<Player> you(final Supplier<? extends MagicObject> object) {
        return new Supplier<Player>() {
            public Player get() {
                return MagicaUtils.you(object.get());
            }
        };
    }
    
    public static Supplier<Game> game(final Supplier<? extends GameContent> object) {
        return new Supplier<Game>() {
            public Game get() {
                return object.get().getGame();
            }
        };
    }
    
    public static Supplier<Player> active(final Supplier<? extends Game> game) {
        return new Supplier<Player>() {
            public Player get() {
                return game.get().getTurnStructure().getActivePlayer();
            }
        };
    }
    
    public static Supplier<Player> prior(final Supplier<? extends Game> game) {
        return new Supplier<Player>() {
            public Player get() {
                return game.get().getPhaseStructure().getPriorPlayer();
            }
        };
    }
}
