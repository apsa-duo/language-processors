/**
 * MiniBasic Parser Grammar (PL2 version)
 *
 * Defines the syntactic structure for the MiniBasic language.
 * Supports: variable assignment, conditionals (IF/ELSE), loops
 * (WHILE, FOR, REPEAT/UNTIL), I/O (PRINT, INPUT), subroutines
 * with parameters, and built-in functions.
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
condicion      : IF expr THEN instruccion* (ELSE instruccion*)? END;
bucleWhile     : WHILE expr instruccion* END;
imprimir       : PRINT concatExpr;
entrada        : INPUT STRING ID;
controlBucle   : CONTINUE | EXIT;
comentario     : LINE_COMMENT | MULTILINE_COMMENT;
repetir        : REPEAT instruccion+ UNTIL expr;

// ── FOR loop ─────────────────────────────────────────────────────────────
bucleFor       : FOR ID IGUAL expr TO expr instruccion* NEXT;

// ── Subroutines ──────────────────────────────────────────────────────────
subrutinaDecl  : SUB ID (PARIZQ parametros? PARDER)? instruccion* END;
parametros     : ID (COMA ID)*;

llamadaSubrutina : CALL ID (PARIZQ argumentos? PARDER)?;
argumentos     : expr (COMA expr)*;

// ── Print concatenation ──────────────────────────────────────────────────
concatExpr     : expr (MAS expr)*;

// ── Expressions ──────────────────────────────────────────────────────────
expr           : expr operador expr
               | funcion
               | INT
               | STRING
               | ID
               | PARIZQ expr PARDER;

funcion        : VAL PARIZQ expr PARDER
               | LEN PARIZQ expr PARDER
               | ISNAN PARIZQ expr PARDER;

operador       : IGUAL
               | MAS
               | MENOS
               | MULTIPLICAR
               | DIVIDIR
               | MOD
               | MENORQUE
               | MAYORQUE;
