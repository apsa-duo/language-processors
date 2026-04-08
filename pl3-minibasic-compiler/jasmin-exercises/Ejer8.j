.class public Ejer8
.super java/lang/Object

; Para este ejemplo de bucle for, se van a imprimir por pantalla los números entre 1 y 3.
.method public static main([Ljava/lang/String;)V
    ; Reservar espacio en la pila y las variables locales
    .limit stack 2          
    .limit locals 2         

    ; Se inicializa el índice del bucle. En este caso, comienza en 1.
    ldc 1                   ; Cargar el valor 1
    istore_0                ; Se guarda el índice en la variable local 0.

    ; Se establece el límite superior del bucle. En este caso, finaliza en 3.
    ldc 3                   ; Cargar el valor 3
    istore_1                ; Se guarda el límte en la variable local 1.

bucle_inicio:
    ; Se comprueba si el índice es menor o igual que el límite establecido
    iload_0                 ; Cargar el índice actual (i)
    iload_1                 ; Cargar el límite superior (3)
    if_icmpgt bucle_fin     ; Si el índice es superior al límite, el bucle finaliza

    ; Imprimir el valor actual de i
    getstatic java/lang/System/out Ljava/io/PrintStream;
    iload_0                 ; Cargar el valor de i
    invokevirtual java/io/PrintStream/println(I)V

    ; Se suma una unidad al índice actual
    iload_0                 ; Cargar el valor actual de i
    ldc 1                   ; Cargar el valor 1
    iadd                    ; Sumar i + 1
    istore_0                ; Se almacena el valor actualizado del índice

    ; El bucle sigue, por lo que se vuelve al inicio del mismo.
    goto bucle_inicio

bucle_fin:
    return
.end method
