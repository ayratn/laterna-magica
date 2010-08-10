/**
 * CardPoolModel.java
 * 
 * Created on 30.07.2010
 */

package net.slightlymagic.laterna.magica.gui.deckEditor;


import javax.swing.table.TableModel;

import net.slightlymagic.laterna.magica.card.Printing;



/**
 * The class CardPoolModel.
 * 
 * @version V0.0 30.07.2010
 * @author Clemens Koza
 */
public interface CardPoolModel extends TableModel {
    public void add(Printing p);
    
    public void remove(Printing p);
}
