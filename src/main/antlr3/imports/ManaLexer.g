lexer grammar ManaLexer;

import IntegerLexer;



COLOR: 'w' | 'u' | 'b' | 'r' | 'g'|
       'W' | 'U' | 'B' | 'R' | 'G';

SNOW: 's' | 'S';

VAR: 'x' | 'y' | 'z'
   | 'X' | 'Y' | 'Z';

WS: (' ' | '\t') { $channel=HIDDEN; };
