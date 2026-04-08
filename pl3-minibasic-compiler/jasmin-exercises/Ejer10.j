.class public Ejer10
.super java/lang/Object

; Método principal
.method public static main([Ljava/lang/String;)V
    ; Reservar espacio en la pila
    .limit stack 1
    .limit locals 1

    ; Llamada a la función "funcionEjemplo"
    invokestatic Ejer10/funcionEjemplo()V

    ; Finalizar el programa
    return
.end method

; Función sin parámetros ni retorno
.method public static funcionEjemplo()V
    ; Reservar espacio en la pila
    .limit stack 2

    ; Imprimir mensaje por pantalla
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "Llamada realizada"
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V

    ; Finalizar el método
    return
.end method
