.class public Ejer7
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
    ; Se reserva espacio en la pila
    .limit stack 2
    .limit locals 1

    ; Se comparan los dos números seleccionado, en este caso, 1 y 2. Se considera que el primer número cargado en la pila es "a" y, el segundo, "b"
    ldc 2                  ; Se carga el valor de 2 en la pila
    ldc 1                  ; Se carga el valor de 1 en la pila
    if_icmple else_block   ; Se comparan los dos números enteros de la pila. Si el segundo valor es menor o igual al primero, se salta a else_block.
                           ; En caso contario, se sigue con la siguiente instrucción.

    ; Si a > b, se imprime el texto: "a es mayor que b"
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "a es mayor que b"
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V

    ; Segundo if anidado: Si b > 0, se imprime el texto: "b es positivo"
    ldc 1                  ; Se vuelve a cargar b en la pila
    ifgt print_b_positive  ; Se compara el valor de la cima de la pila con 0. Si b > 0, se salta a print_b_positive
    goto end_block         ; Si b <= 0, se finaliza el programa

print_b_positive:
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "b es positivo"
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
    goto end_block         ; Finaliza el programa

else_block:
    ; Si a <= b, imprimir "a no es mayor que b"
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "a no es mayor que b"
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V

end_block:
    ; Finalizar el programa
    return
.end method
