/**
 * TestEditableList.java
 * 
 * Created on 28.03.2010
 */

package net.slightlymagic.laterna.magica.test;


import static java.util.Arrays.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.LaternaInit;
import net.slightlymagic.laterna.magica.edit.Edit;
import net.slightlymagic.laterna.magica.impl.GameImpl;
import net.slightlymagic.laterna.magica.util.MagicaCollections;

import org.junit.Test;


/**
 * The class TestEditableList.
 * 
 * @version V0.0 28.03.2010
 * @author Clemens Koza
 */
public class TestEditableList {
    private static final List<String> ref0 = asList();
    private static final List<String> ref1 = asList("a");
    private static final List<String> ref2 = asList("a", "b");
    private static final List<String> ref3 = ref1;
    private static final List<String> ref4 = ref0;
    private static final List<String> ref5 = asList("c");
    private static final List<String> ref6 = asList("c", "b", "a");
    
    static {
        try {
            LaternaInit.init();
        } catch(Exception ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    @Test
    public void testRandomAccessList() {
        testList(new ArrayList<String>());
    }
    
    @Test
    public void testSequentialList() {
        testList(new LinkedList<String>());
    }
    
    private void testList(List<String> l) {
        Game g = new GameImpl();
        l = MagicaCollections.editableList(g, l);
        System.out.println(l.getClass());
        
        Edit e1 = g.getGameState().getCurrent();
        
        assertEquals(ref0, l);
        l.add("a");
        assertEquals(ref1, l);
        l.add("b");
        assertEquals(ref2, l);
        l.remove("b");
        assertEquals(ref3, l);
        ListIterator<String> it = l.listIterator();
        it.next();
        it.remove();
        assertEquals(ref4, l);
        it.add("c");
        assertEquals(ref5, l);
        l.add("a");
        l.add(1, "b");
        assertEquals(ref6, l);
        
        Edit e2 = g.getGameState().getCurrent();
        
        Collections.shuffle(l, g.getRandom());
        System.out.println(l);
        
        g.getGameState().stepTo(e2);
        assertEquals(ref6, l);
        g.getGameState().stepTo(e1);
        assertEquals(ref0, l);
        g.getGameState().stepTo(e2);
        assertEquals(ref6, l);
        System.out.println(g.getGameState());
    }
}
