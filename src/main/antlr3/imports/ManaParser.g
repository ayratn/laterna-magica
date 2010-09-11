parser grammar ManaParser;

import IntegerParser;


color returns [MagicColor value]
: COLOR
  { $value = MagicColor.getColorByChar($COLOR.getText().charAt(0)); }
;

fragment symbol returns [ManaSymbol value]
: '{'
  ( nonhybrid
    { $value = $nonhybrid.value; }
  | hybrid
    { $value = $hybrid.value; }
  )
  '}'
;

fragment nonhybrid returns [ManaSymbol value]
: color
  { $value = new ColoredManaSymbol($color.value); }
| unsignedInt
  { $value = new NumeralManaSymbol($unsignedInt.value); }
| SNOW
  { $value = new SnowManaSymbol(); }
| VAR
  { $value = new VariableManaSymbol($VAR.getText().charAt(0)); }
;

fragment hybrid returns [ManaSymbol value]
@init { List<ManaSymbol> parts = new ArrayList<ManaSymbol>(); }
@after { $value = new HybridManaSymbol(parts); }
: h1=nonhybrid { parts.add($h1.value); } ('/' hn=nonhybrid { parts.add($hn.value); })+
;

mana returns [ManaSequence value]
@init { List<ManaSymbol> symbols = new ArrayList<ManaSymbol>(); }
@after { $value = new ManaSequenceImpl(symbols); }
: (symbol { symbols.add($symbol.value); })+
;
