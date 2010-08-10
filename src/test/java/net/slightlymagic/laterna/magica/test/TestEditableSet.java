/**
 * TestEditableSet.java
 * 
 * Created on 26.03.2010
 */

package net.slightlymagic.laterna.magica.test;


import static java.util.Arrays.*;
import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.edit.Edit;
import net.slightlymagic.laterna.magica.impl.GameImpl;
import net.slightlymagic.laterna.magica.util.MagicaCollections;

import org.junit.Test;


/**
 * The class TestEditableSet.
 * 
 * @version V0.0 26.03.2010
 * @author Clemens Koza
 */
public class TestEditableSet {
    private static final Set<String> ref0 = new HashSet<String>();
    private static final Set<String> ref1 = new HashSet<String>(asList("a"));
    private static final Set<String> ref2 = new HashSet<String>(asList("a", "b"));
    private static final Set<String> ref3 = ref1;
    private static final Set<String> ref4 = ref0;
    
    @Test
    public void testSet() {
        Game g = new GameImpl();
        Set<String> s = MagicaCollections.editableSet(g, new HashSet<String>());
        
        Edit e1 = g.getGameState().getCurrent();
        
        assertEquals(ref0, s);
        s.add("a");
        assertEquals(ref1, s);
        s.add("b");
        assertEquals(ref2, s);
        s.remove("b");
        assertEquals(ref3, s);
        Iterator<String> it = s.iterator();
        it.next();
        it.remove();
        assertEquals(ref4, s);
        
        Edit e2 = g.getGameState().getCurrent();
        
        g.getGameState().stepTo(e1);
        assertEquals(ref0, s);
        g.getGameState().stepTo(e2);
        assertEquals(ref4, s);
    }
}
