/**
 * CompoundEdit.java
 * 
 * Created on 30.11.2009
 */

package net.slightlymagic.laterna.magica.edit;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import net.slightlymagic.laterna.magica.Game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The class CompoundEdit. This is used for edits that implement a "collection" of edits that are done together.
 * 
 * @version V1.0 17.01.2010
 * @author Clemens Koza
 */
public class CompoundEdit extends Edit {
    private static final Logger log              = LoggerFactory.getLogger(CompoundEdit.class);
    
    private static final long   serialVersionUID = 2293396706254539981L;
    private List<Edit>          children         = new ArrayList<Edit>();
    private String              name;
    private boolean             childrenToString;
    
    public CompoundEdit(Game game, boolean atomic) {
        this(game, atomic, null, true);
    }
    
    /**
     * Creates a compound edit with the given name. if the name is non-null, it's used in {@link #toString()}.
     */
    public CompoundEdit(Game game, boolean atomic, String name) {
        this(game.getGameState(), atomic, name, false);
    }
    
    /**
     * Creates a compound edit with the given name. if the name is non-null, it's used in {@link #toString()}.
     */
    public CompoundEdit(Game game, boolean atomic, String name, boolean childrenToString) {
        this(game.getGameState(), atomic, name, childrenToString);
    }
    
    //needed for the root edit, which is created during constructing the game. game.gameState is not initialized at
    //that point
    CompoundEdit(GameState gameState, boolean atomic, String name, boolean childrenToString) {
        super(gameState, atomic);
        this.name = name;
        this.childrenToString = name == null || childrenToString;
    }
    
    @Override
    protected List<Edit> getChildren() {
        return children;
    }
    
    public void end() {
        getGame().getGameState().end(this);
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    @Override
    public String deepToString() {
        StringBuffer buf = new StringBuffer();
        if(name != null) buf.append(name);
        if(childrenToString || log.isDebugEnabled()) {
            if(name != null) buf.append(" [\n ");
            else buf.append("[\n ");
            
            Iterator<Edit> i = getChildren().iterator();
            while(i.hasNext()) {
                Edit e = i.next();
                String s = e.deepToString();
                buf.append(s == null? null:s.replaceAll("\n", "\n "));
                if(i.hasNext()) buf.append(",\n ");
            }
            
            buf.append("\n]");
        }
        return buf.toString();
    }
}
