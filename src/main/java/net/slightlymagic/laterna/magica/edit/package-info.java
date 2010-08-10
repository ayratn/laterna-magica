/**
 * <p>This package defines {@link net.slightlymagic.laterna.magica.edit.Edit Edit}s, actions that are taken during a game and may be
 * undone. Together with the initial state of the game (including the used random number generator's seed), the
 * sequence of edits fully describes the course of the game.</p>
 * <p>Edits are held in a tree structure that groups moves belonging together. This allows the program to identify
 * atomic actions that consist of multiple edits, as well as users to easily find the edit they're interested in.</p>
 * 
 * <p>The usual work with edits is the following: Every game has a unique
 * {@link net.slightlymagic.laterna.magica.edit.GameState GameState} that stores the tree structure. At its root, there's a
 * non-atomic {@link net.slightlymagic.laterna.magica.edit.CompoundEdit CompoundEdit}.</p>
 * <p>By creating an edit with a game, it is added to the tree. A compound edit additionally becomes the "active"
 * edit, which subsequent edits will be added to. There is the need to manually move out of the compound edit.</p>
 * 
 * <pre>
 * //Create the game, which contains the GameState
 * Game g = new GameImpl();
 * //MyEdit is some subclass of edit; the constructor adds it to the tree (in the beginning, the active edit
 * //is the root edit), and then it's executed.
 * new MyEdit(g).execute();
 * //Create a compound edit. It is added directly after the previous edit, and now is the active edit.
 * CompoundEdit m = new CompoundEdit(g, false);
 * //This move is not added after, but in the previously created compound edit
 * new MyEdit(g).execute();
 * //finishes the compound edit; the root edit is then active again
 * m.end();
 * //this edit is again added directly to the root, after the compound edit
 * new MyEdit(g).execute();
 * </pre>
 * 
 * <p>Note that all of the edits are executed after creation; storing the edits in the tree is automatic, so that
 * programmers can semantically replace the act of creating and executing the edit with the actual action in their
 * heads. Further note that it's not possible to automatically execute the edit at creation, since that would mean
 * execution happens with a not fully initialized edit. It is not recommended to call execute() at the end of the
 * constructor for subclassign purposes.</p>
 */

package net.slightlymagic.laterna.magica.edit;


