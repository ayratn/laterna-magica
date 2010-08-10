/**
 * PlayInformation.java
 * 
 * Created on 20.04.2010
 */

package net.slightlymagic.laterna.magica.action.play;


import net.slightlymagic.laterna.magica.GameContent;
import net.slightlymagic.laterna.magica.MagicObject;
import net.slightlymagic.laterna.magica.action.GameAction;


/**
 * The class PlayInformation. The class PlayInformation is used to encapsulate the data about playing a spell or
 * activated ability.
 * 
 * @version V0.0 20.04.2010
 * @author Clemens Koza
 */
public interface PlayInformation extends GameContent {
    /**
     * Returns the action that is used in playing.
     */
    public PlayAction getAction();
    
    /**
     * Returns the object that was played using this play information.
     */
    public MagicObject getObject();
    
    public void makeChoices();
    
    public void chooseTargets();
    
    public void distributeEffect();
    
    /**
     * This action is only defined for activated abilities and spells, but not for triggered abilities.
     */
    public GameAction getCost();
    
    public GameAction getEffect();
}
