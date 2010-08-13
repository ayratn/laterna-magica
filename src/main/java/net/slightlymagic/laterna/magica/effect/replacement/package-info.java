/**
 * This package implements replacement effect. Replacement effect are a core part and interact closely with the
 * {@link net.slightlymagic.laterna.magica.edit.GameState GameState}.
 * 
 * In a nutshell, all changes to the game are encapsulated in
 * {@link net.slightlymagic.laterna.magica.edit.Edit Edit}s. Many changes, like gaining or losing life, are
 * replaceable. Thus, instead of executing such an edit directly, the <i>replacement engine</i> handles this, and
 * executes the Edit applicable after all replacement effects.
 * 
 * The replacement engine manages a list of all applicable replacement effects (i.e. the card generating the
 * replacement effects are in the appropriate zone). Whenever a replaceable event occurs it is replaced following
 * {@magic.ruleRef 20100716/R616}. The replacement engine looks for all replacement effects that apply to that event
 * and then, if there is more than one, lets the appropriate player choose one of them, as specified in {@magic.ruleRef 20100716/R6161}
 * and replaces the old event by applying the replacement effect. Then, the process starts again, but only
 * considering effects that weren't applied before.
 */

package net.slightlymagic.laterna.magica.effect.replacement;


