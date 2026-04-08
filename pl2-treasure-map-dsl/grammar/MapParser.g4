/**
 * Map DSL Parser Grammar
 *
 * Defines the syntactic structure for treasure map definition files.
 * A map file starts with a title, followed by any number of declarations
 * (ship locations, treasures, forbidden zones, islands), and ends
 * with the 'fin' keyword.
 */
parser grammar MapParser;

options { tokenVocab=MapLexer; language=Java; }

mapa : tituloMapa (declaracion)* FIN;

tituloMapa : CADENA;

declaracion
    : puntuacionDeclaracion
    | ubicacionDeclaracion
    | casillaProhibidaDeclaracion
    | tesoroDeclaracion
    | islaDeclaracion
    | islaDeclaracionSimplificada
    ;

puntuacionDeclaracion : barco TE DA puntuacion PUNTOS;
ubicacionDeclaracion : barco ESTA ENTERRADO EN posicion;
casillaProhibidaDeclaracion: EN LA CASILLA posicion HAY UNA ZONA PROHIBIDA QUE REDUCE puntuacion PUNTOS;
islaDeclaracion : EN LA CASILLA posicion HAY UNA ISLA LOCALIZADA;
islaDeclaracionSimplificada : localizacion ESTA LOCALIZADA A distancia CASILLAS AL direccion DE barco;

tesoroDeclaracion : EN LA CASILLA posicion HAY UN TESORO QUE DA puntuacion PUNTOS;

// Primitive value rules
posicion : ENTERO COMA ENTERO;
puntuacion : ENTERO;
distancia : ENTERO;
barco : CADENA;
localizacion : CADENA;
direccion : DIRECCION;
