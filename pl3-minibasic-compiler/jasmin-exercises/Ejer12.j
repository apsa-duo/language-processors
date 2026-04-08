.class public Ejer12
.super java/lang/Object

; Método principal
.method public static main([Ljava/lang/String;)V
    ; Reservar espacio en la pila
    .limit stack 2
    .limit locals 1

    ; Se carga en la pila el número entero que se usará como parámetro
    ldc 1                    

    ; Llamada al método "funcionEjemplo"
    invokestatic Ejer12/funcionEjemplo(I)V

    ; Finalizar el programa
    return
.end method

; Función que recibe un parámetro entero. Dicho parámetro es almacenado automáticamente en la variable local 0
.method public static funcionEjemplo(I)V
    ; Reservar espacio en la pila
    .limit stack 2

    ; Mostrar por pantalla el parámetro recibido
    getstatic java/lang/System/out Ljava/io/PrintStream;
    iload_0                   ; Cargar el parámetro 
    invokevirtual java/io/PrintStream/println(I)V 
    
    return
.end method
