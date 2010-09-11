parser grammar IntegerParser;



anyInt returns [int value]
: signedInt
  { $value = $signedInt.value; }
| unsignedInt
  { $value = $unsignedInt.value; }
;

unsignedInt returns [int value]
: INT
  { $value = Integer.parseInt($INT.getText()); }
;

signedInt returns [int value]
: PLUS unsignedInt
  { $value = + $unsignedInt.value; }
| MINUS unsignedInt
  { $value = - $unsignedInt.value; }
;
