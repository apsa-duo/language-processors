.class public Ejer9
.super java/lang/Object

; Para este ejemplo de bucle while, se van a imprimir por pantalla los números entre un entero dado y 1.
.method public static main([Ljava/lang/String;)V
    ; Reservar espacio en la pila y las variables locales
    .limit stack 2          ; 
    .limit locals 1         ; Se hace uso de una variable local para el índice

    ; Se inicializa el índice del bucle con valor = 3
    ldc 3                   ; Cargar el valor inicial 3
    istore_0                ; Se almacena el valor del índice en la variable local

bucle_inicio:
    ; Se comprueba que el índice sea superior a 0.
    iload_0                 ; Se carga el valor del índice actual
    ifle bucle_fin          ; Se sale del bucle si el índice es menor o igual a 0.

    ; Se imprime el valor del índice
    getstatic java/lang/System/out Ljava/io/PrintStream;
    iload_0                 ; Se carga el valor del índice actual
    invokevirtual java/io/PrintStream/println(I)V

    ; Se decrementa el índice en una unidad.
    iload_0                 ; Se carga el valor del índice actual
    ldc 1                   ; Se carga el número entero 1
    isub                    ; Se le resta una unidad al índice
    istore_0                ; Se guarda el valor actualizado del índice en la variable local

    ; Se vuelve al comienzo del bucle
    goto bucle_inicio

bucle_fin:
    return
.end method

