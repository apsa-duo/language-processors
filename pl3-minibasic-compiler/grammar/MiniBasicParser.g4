/**
 * MiniBasic Parser Grammar (PL3 — Compiler version)
 *
 * Extended parser supporting all MiniBasic language constructs including:
 *   - User-defined functions with RETURN
 *   - Array expressions and access
 *   - Boolean and float literals
 *   - Labeled THEN/ELSE instruction lists for code generation
 */
parser grammar MiniBasicParser;

options {
    tokenVocab = MiniBasicLexer;
    language = Java;
}

// ── Program structure ────────────────────────────────────────────────────
prog       : instruccion+;

// ── Statement types ──────────────────────────────────────────────────────
instruccion    : asignacion
               | condicion
               | bucleWhile
               | imprimir
               | bucleFor
               | entrada
               | controlBucle
               | comentario
               | repetir
               | subrutinaDecl
               | llamadaSubrutina;

// ── Assignment ───────────────────────────────────────────────────────────
asignacion     : LET? ID IGUAL expr;

// ── Control flow ─────────────────────────────────────────────────────────
condicion      : IF expr THEN thenInstrs+=instruccion* (ELSE elseInstrs+=instruccion*)? END;
bucleWhile     : WHILE expr instruccion* END;
imprimir       : (INT MULTIPLICAR)? PRINT concatExpr;
entrada        : INPUT STRING ID;
controlBucle   : CONTINUE | EXIT;
comentario     : LINE_COMMENT | MULTILINE_COMMENT;
repetir        : REPEAT instruccion+ UNTIL ID IGUAL entero;

// ── FOR loop ─────────────────────────────────────────────────────────────
bucleFor       : FOR identificador=ID IGUAL indexInt=entero TO limitInt=entero instruccion* NEXT;

// ── Subroutines & Functions ──────────────────────────────────────────────
subrutinaDecl  : SUB ID (PARIZQ parametros? PARDER)? instruccion* END;
funcionDecl    : FUNCTION ID (PARIZQ parametros? PARDER)? instruccion* RETURN expr END;

llamadaSubrutina : CALL ID (PARIZQ argumentos? PARDER)?;
llamadaFuncion   : CALL ID (PARIZQ argumentos? PARDER)?;

// ── Parameters & Arguments ───────────────────────────────────────────────
parametros     : ID (COMA ID)*;
argumentos     : expr (COMA expr)*;

// ── Print concatenation ──────────────────────────────────────────────────
concatExpr     : expr (MAS expr)*;

// ── Expressions ──────────────────────────────────────────────────────────
expr           : expr operador expr
               | funcion
               | INT
               | STRING
               | FLOAT
               | booleano
               | ID
               | PARIZQ expr PARDER
               | arrayExpr;

// ── Boolean literals ─────────────────────────────────────────────────────
booleano        : TRUE | FALSE;

// ── Built-in functions ───────────────────────────────────────────────────
funcion        : VAL PARIZQ expr PARDER
               | LEN PARIZQ expr PARDER
               | ISNAN PARIZQ expr PARDER;

// ── Operators ────────────────────────────────────────────────────────────
operador       : IGUAL
               | MAS
               | MENOS
               | MULTIPLICAR
               | DIVIDIR
               | MOD
               | MENORQUE
               | MAYORQUE;

// ── Integer wrapper ──────────────────────────────────────────────────────
entero         : INT;

// ── Array expressions ────────────────────────────────────────────────────
arrayExpr      : ID LBRACKET expr RBRACKET              // Element access
               | ARRAY LBRACKET expr (COMA expr)* RBRACKET;  // Array literal
