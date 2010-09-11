grammar Oracle;

import ManaLexer, ManaParser;

@parser::header {
package net.slightlymagic.laterna.magica.cards.text.antlr;

import net.slightlymagic.laterna.magica.characteristics.MagicColor;
import net.slightlymagic.laterna.magica.mana.*;
import net.slightlymagic.laterna.magica.mana.impl.*;
}



PERIOD: '\.';
COLON: ':';
COMMA: ',';



abilities
: spellAbility
| activatedAbility
| triggeredAbility
;

spellAbility
: effects
;

activatedAbility
: cost COLON effects
;

triggeredAbility
: trigger COMMA effects
;

effects
: (effect PERIOD)+
;

costs
: cost (COMMA cost)*
;


/*
*/
effect
: 'add' mana 'to' 'your' 'mana' 'pool'
| 'draw' 'a' 'card'
| 'draw' unsignedInt 'cards'
| '~' 'deals' unsignedInt 'damage' 'to' 'all' 'creatures'
| 'destroy' 'all' 'creatures' ('\.' 'they' 'can\'t' 'be' 'regenerated')?
;

cost
: mana
| '{T}'
| '{Q}'
| 'sacrifice' '~'
;

trigger
: 'when' '~' 'enters' 'the' 'battlefield'
;

WS: (' ')+ { $channel=HIDDEN; } ;
