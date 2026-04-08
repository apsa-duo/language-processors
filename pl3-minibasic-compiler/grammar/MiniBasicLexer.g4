/**
 * MiniBasic Lexer Grammar (PL3 — Compiler version)
 *
 * Extended version of the MiniBasic lexer with additional tokens for:
 *   - FUNCTION/RETURN keywords (user-defined functions)
 *   - CONTINUE keyword (corrected spelling)
 *   - Float literals, boolean literals
 *   - Array syntax (brackets)
 *   - TRUE/FALSE constants
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
CONTINUE : [cC][oO][nN][tT][iI][nN][uU][eE];
EXIT     : [eE] [xX] [iI] [tT];
END      : [eE] [nN] [dD];
REPEAT   : [rR] [eE] [pP] [eE] [aA] [tT];
UNTIL    : [uU] [nN] [tT] [iI] [lL];
SUB      : [sS] [uU] [bB];
CALL     : [cC] [aA] [lL] [lL];
DIM      : [dD] [iI] [mM];
RETURN   : [rR] [eE] [tT] [uU] [rR] [nN];
FUNCTION : [fF] [uU] [nN] [cC] [tT] [iI] [oO] [nN];

// ── Built-in Functions ───────────────────────────────────────────────────
VAL         : [vV] [aA] [lL];
LEN         : [lL] [eE] [nN];
ISNAN       : [iI] [sS] [nN] [aA] [nN];

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
LBRACKET    : '[';
RBRACKET    : ']';

// ── Type Literals ────────────────────────────────────────────────────────
FLOAT       : [0-9]+ '.' [0-9]+;
TRUE        : 'true' | 'TRUE';
FALSE       : 'false' | 'FALSE';
ARRAY       : 'array' | 'ARRAY';

// ── Identifiers & Literals ───────────────────────────────────────────────
ID          : [a-zA-Z]+ ('$' | '%')?;
INT         : [0-9]+;
STRING      : '"' (~["\r\n])* '"';

// ── Whitespace & Comments (skipped) ──────────────────────────────────────
WS               : [ \t\r\n]+ -> skip;
LINE_COMMENT     : REM ~[\r\n]* -> skip;
MULTILINE_COMMENT: '/*' .*? '*/' -> skip;
