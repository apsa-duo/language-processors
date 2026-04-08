.class public Ejer11
.super java/lang/Object

; Método principal
.method public static main([Ljava/lang/String;)V
    ; Reservar espacio en la pila y variables locales
    .limit stack 2
    .limit locals 2

    ; Llamada a la función "funcionEjemplo" 
    invokestatic Ejer11/funcionEjemplo()I  
    istore_0                              ; Almacenar el resultado en la variable local 0

    ; Imprimir el resultado
    getstatic java/lang/System/out Ljava/io/PrintStream;
    iload_0                               ; Cargar el valor de la variable local 0
    invokevirtual java/io/PrintStream/println(I)V ; Imprimir el número entero obtenido en la llamada a la función

    ; Finalizar el programa
    return
.end method

; Función que devuelve un entero
.method public static funcionEjemplo()I
    ; Reservar espacio en la pila
    .limit stack 1

    ; Devolver un número entero
    ldc 1       ; Cargar el valor 42 en la pila
    ireturn      ; Retornar el valor entero
.end method
