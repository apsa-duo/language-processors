.class public Ejer5
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
    ; Reserva de espacio para la pila y las variables locales
    .limit stack 3
    .limit locals 1

    getstatic java/lang/System/out Ljava/io/PrintStream;

    ; Se hace uso de la clase StringBuilder para poder concatenar la cadena de texto y el número
    new java/lang/StringBuilder     
    dup                               ; Duplicar la referencia al objeto anterior
    invokespecial java/lang/StringBuilder/<init>()V ; Llamar al constructor de StringBuilder y se consume una de las referencias anteriormente duplicadas

    ; Añadir la cadena inicial
    ldc "Numero de ejemplo: "              ; Cargar la cadena "Numero de ejemplo: " en la pila
    invokevirtual java/lang/StringBuilder/append(Ljava/lang/String;)Ljava/lang/StringBuilder; ; Concatenar la cadena (toma la cadena de la pila y la concatena)

    ; Añadir el número
    ldc 10                            ; Cargar el número seleccionado en la pila
    invokevirtual java/lang/StringBuilder/append(I)Ljava/lang/StringBuilder; ; Concatenar el número (toma el número de la pila y lo concatena al contenido de StringBuilder)

    ; Convertir a String
    invokevirtual java/lang/StringBuilder/toString()Ljava/lang/String;

    ; Mostrar por pantalla el resultado de la concatenación
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V

    ; Finalizar el programa
    return
.end method
