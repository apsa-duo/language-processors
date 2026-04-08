.class public Ejer4
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
    ; Se reserva espacio en la pila
    .limit stack 2       
    .limit locals 3

    ; Operación lógica: true && false. True se representa mediante "1", false mediante "0"
    iconst_1             ; Cargar true (1) en la pila
    iconst_0             ; Cargar false (0) en la pila
    iand                 ; Realizar AND lógico (1 & 0 = 0)

    ; Si el resultado no es cero, se imprime "true"
    ifne print_true      ; Si el resultado no es 0, se imprime "true", saltando a la etiqueta "print_true"

    ; Imprimir "false"
    getstatic java/lang/System/out Ljava/io/PrintStream; 
    ldc "false"          ; Cargar "false" en la pila
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
    goto end             ; Se salta a "end" para finalizar el programa

print_true:
    ; Imprimir "true"
    getstatic java/lang/System/out Ljava/io/PrintStream; 
    ldc "true"           ; Cargar "true" en la pila
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V

end:
    ; Finalizar el programa
    return
.end method




