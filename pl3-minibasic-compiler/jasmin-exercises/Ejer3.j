.class public Ejer3
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
    ; Reserva de espacio en la pila.
    .limit stack 3
    .limit locals 3

    ; Cargar el flujo de salida estándar (System.out)
    getstatic java/lang/System/out Ljava/io/PrintStream;

    ; Se cargan los números involucrados en la multiplicación
    ldc 6               ; Cargar el primer número (6)
    ldc 4               ; Cargar el segundo número (4)
    imul                ; Multiplicar los valores anteriores

    ; LLamada a println para realizar la impresión del resultado
    invokevirtual java/io/PrintStream/println(I)V

    ; Finalizar el programa
    return
.end method
