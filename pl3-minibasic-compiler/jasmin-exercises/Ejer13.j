.class public Ejer13
.super java/lang/Object

; Método principal
.method public static main([Ljava/lang/String;)V
    ; Reservar espacio en la pila
    .limit stack 3
    .limit locals 1

    ; Se cargan en la pila los números enteros que se usarán como parámetros
    ldc 1                     ; Primer parámetro
    ldc 2                     ; Segundo parámetro

    ; Llamada al método "funcionEjemplo"
    invokestatic Ejer13/funcionEjemplo(II)V

    ; Finalizar el programa
    return
.end method

; Función que recibe dos parámetros enteros. Dichos parámetros son almacenados automáticamente en las variables locales 0 y 1
.method public static funcionEjemplo(II)V
    ; Reservar espacio en la pila
    .limit stack 3
    .limit locals 2

    ; Mostrar por pantalla los parámetros recibidos
    getstatic java/lang/System/out Ljava/io/PrintStream;
    iload_0                   
    invokevirtual java/io/PrintStream/println(I)V 


    getstatic java/lang/System/out Ljava/io/PrintStream;
    iload_1                   
    invokevirtual java/io/PrintStream/println(I)V 

    return
.end method
