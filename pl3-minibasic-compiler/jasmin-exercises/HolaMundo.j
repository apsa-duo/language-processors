.class public HolaMundo
.super java/lang/Object

.method public static main([Ljava/lang/String;)V

   ; reservamos 2 sitios en la pila
   .limit stack 2
   
   ; ponemos a java.lang.System.out (type PrintStream) en la pila
   getstatic java/lang/System/out Ljava/io/PrintStream;
   
   ; hacemos un push en la pila de la cadena de caracteres deseada
   ldc "Hola Mundo!"
   
   ; invocamos la funcion println
   invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
   
   ; y cerramos el main
   return

.end method
