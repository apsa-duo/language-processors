/**
 * MiniBasic Lexer Grammar (PL2 version)
 *
 * Defines the lexical tokens for a case-insensitive BASIC dialect called MiniBasic.
 * This version includes extensions for:
 *   - Subroutines (SUB/CALL) with parameters
 *   - Multi-line comments
 *   - Built-in functions: VAL, LEN, ISNAN
 */
lexer grammar MiniBasicLexer;

// ── Keywords ─────────────────────────────────────────────────────────────
IF       : [iI] [fF];
THEN     : [tT] [hH] [eE] [nN];
ELSE     : [eE] [lL] [sS] [eE];
WHILE    : [wW] [hH] [iI] [lL] [eE];
FOR      : [fF] [oO] [rR];
TO       : [tT] [oO];
NEXT     : [nN] [eE] [xX] [tT];
PRINT    : [pP] [rR] [iI] [nN] [tT];
INPUT    : [iI] [nN] [pP] [uU] [tT];
LET      : [lL] [eE] [tT];
REM      : [rR] [eE] [mM];
CONTINUE : [cC] [oO] [nN] [uU] [eE];
EXIT     : [eE] [xX] [iI] [tT];
END      : [eE] [nN] [dD];
REPEAT   : [rR] [eE] [pP] [eE] [aA] [tT];
UNTIL    : [uU] [nN] [tT] [iI] [lL];
SUB      : [sS] [uU] [bB];
CALL     : [cC] [aA] [lL] [lL];
DIM      : [dD] [iI] [mM];

// ── Operators & Symbols ──────────────────────────────────────────────────
IGUAL       : '=';
MAS         : '+';
MENOS       : '-';
MULTIPLICAR : '*';
DIVIDIR     : '/';
MOD         : [mM] [oO] [dD];
MENORQUE    : '<';
MAYORQUE    : '>';
PARIZQ      : '(';
PARDER      : ')';
COMA        : ',';

// ── Built-in Functions ───────────────────────────────────────────────────
VAL         : [vV] [aA] [lL];
LEN         : [lL] [eE] [nN];
ISNAN       : [iI] [sS] [nN] [aA] [nN];

// ── Identifiers & Literals ───────────────────────────────────────────────
ID          : [a-zA-Z]+ ('$' | '%')?;
INT         : [0-9]+;
STRING      : '"' (~["\r\n])* '"';

// ── Whitespace & Comments (skipped) ──────────────────────────────────────
WS          : [ \t\r\n]+ -> skip;
LINE_COMMENT : REM ~[\r\n]* -> skip;
MULTILINE_COMMENT : '/*' .*? '*/' -> skip;
