/**
 * Map DSL Lexer Grammar
 *
 * Defines the lexical tokens for a custom Domain-Specific Language (DSL)
 * that describes treasure maps for an interactive treasure hunt game.
 * The DSL supports defining:
 *   - Ship locations with point values
 *   - Treasure positions with rewards
 *   - Forbidden zones with point penalties
 *   - Island locations (grant extra attempts)
 *   - Relative positioning of islands to ships
 */
lexer grammar MapLexer;

CADENA : '"' (~["])* '"';      // Quoted string literals (e.g., ship names)
ENTERO : [0-9]+;               // Integer literals
COMA : ',';
WS : [ \t\r\n]+ -> skip;       // Whitespace (ignored)

// Spanish-language keywords that form the DSL syntax
TE : 'te';
DA : 'da';
LA : 'la';
ESTA : 'esta';
ENTERRADO : 'enterrado';
EN : [Ee][Nn];
CASILLA : 'casilla';
HAY : 'hay';
UNA : 'una';
ZONA : 'zona';
PROHIBIDA : 'prohibida';
QUE : 'que';
REDUCE : 'reduce';
PUNTOS : 'puntos';
LOCALIZADA : 'localizada';
A : 'a';
CASILLAS : 'casillas';
AL : 'al';
DE : 'de';
DIRECCION : 'norte' | 'este' | 'sur' | 'oeste';
UN : 'un';
CON : 'con';

// Section markers
MAPA : 'Mapa';
TESORO : 'tesoro';
ZONAS_PROHIBIDAS : 'ZonasProhibidas';
FIN : 'fin';
BARCO : 'barco';
ISLA : 'isla';
