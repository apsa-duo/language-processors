import org.antlr.v4.runtime.tree.TerminalNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * ANTLR visitor that compiles MiniBasic source code into Jasmin assembly.
 *
 * <p>This code generator walks the parse tree produced by
 * {@link MiniBasicParser} and emits JVM bytecode instructions in Jasmin
 * format. It handles:</p>
 * <ul>
 *   <li>Variable assignment with type inference (int, string, boolean, array)</li>
 *   <li>Arithmetic and boolean expressions</li>
 *   <li>Control flow: IF/ELSE, WHILE, FOR, REPEAT/UNTIL</li>
 *   <li>PRINT with string concatenation and multiplier syntax</li>
 *   <li>INPUT for reading user input</li>
 *   <li>Subroutines (SUB/CALL) and functions (FUNCTION/RETURN)</li>
 *   <li>Built-in functions: VAL, LEN, ISNAN</li>
 *   <li>Array literals and element access</li>
 * </ul>
 *
 * @author Asier Alamo, Sergio Alonso Zarcero, Andrea Pascual Aguilera
 */
public class JasminCodeGenerator extends MiniBasicParserBaseVisitor<Void> {

    /** Symbol table tracking all declared variables and their metadata. */
    private SymbolTable symbolTable = new SymbolTable();

    /** Buffer for the main method's Jasmin bytecode instructions. */
    private StringBuilder mainMethodCode = new StringBuilder();

    /** Buffer for subroutine method Jasmin bytecode instructions. */
    private StringBuilder subroutineCode = new StringBuilder();

    /** Maps variable names to their JVM local variable indices. */
    private Map<String, Integer> variableIndex = new HashMap<>();

    /** The next available local variable index. Index 0 is reserved for args. */
    private int nextLocalIndex = 1;

    /** Counter for generating unique jump labels. */
    private int labelCounter = 0;

    /** Label for the start of the current loop (used by CONTINUE). */
    private String currentLoopStartLabel = "";

    /** Label for the end of the current loop (used by EXIT). */
    private String currentLoopEndLabel = "";

    /** Flag indicating the last expression was arithmetic (affects store generation). */
    private boolean isArithmeticExpression = false;

    /** Points to either mainMethodCode or subroutineCode depending on context. */
    private StringBuilder activeCodeBuffer = mainMethodCode;

    /** Flag indicating we are inside a WHILE loop body. */
    private boolean isInsideWhileLoop = false;

    /**
     * Creates a new Jasmin code generator.
     */
    public JasminCodeGenerator() {
        super();
        System.out.println("JasminCodeGenerator instantiated.");
    }

    /**
     * Returns the compiler's symbol table.
     *
     * @return the symbol table
     */
    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    // ═════════════════════════════════════════════════════════════════════
    // PROGRAM STRUCTURE
    // ═════════════════════════════════════════════════════════════════════

    @Override
    public Void visitProg(MiniBasicParser.ProgContext ctx) {
        System.out.println("Visiting program...");

        // Jasmin class header
        mainMethodCode.append(".class public MiniBProgram\n");
        mainMethodCode.append(".super java/lang/Object\n\n");

        // Main method declaration
        mainMethodCode.append(".method public static main([Ljava/lang/String;)V\n");
        mainMethodCode.append(".limit stack 100\n");
        mainMethodCode.append(".limit locals 100\n");

        visitChildren(ctx);

        // Close main method
        mainMethodCode.append("return\n");
        mainMethodCode.append(".end method\n");

        return null;
    }

    @Override
    public Void visitInstruccion(MiniBasicParser.InstruccionContext ctx) {
        System.out.println("Visiting instruction: " + ctx.getText());
        return visitChildren(ctx);
    }

    // ═════════════════════════════════════════════════════════════════════
    // ASSIGNMENT
    // ═════════════════════════════════════════════════════════════════════

    @Override
    public Void visitAsignacion(MiniBasicParser.AsignacionContext ctx) {
        System.out.println("Visiting assignment...");
        String varName = ctx.ID().getText();
        Symbol symbol = symbolTable.resolve(varName);

        if (symbol == null) {
            // First time seeing this variable — infer its type and define it
            String inferredType = inferType(ctx.expr());

            boolean defined = symbolTable.define(varName, inferredType);
            if (defined) {
                System.out.println("Defined variable: " + varName + " of type " + inferredType);
                if (!variableIndex.containsKey(varName)) {
                    variableIndex.put(varName, nextLocalIndex++);
                }
                symbol = symbolTable.resolve(varName);
                // Initialize arrays with empty array
                if (inferredType.startsWith("array")) {
                    symbol.setValue(new Object[0]);
                }
            } else {
                System.err.println("Error: Could not define variable '" + varName + "'.");
                return null;
            }
        }

        int index = variableIndex.get(varName);
        symbol.setStorageName("local_" + index);

        // Evaluate the right-hand side expression
        Object value;
        try {
            value = evaluateExpression(ctx.expr());
        } catch (ArithmeticException e) {
            System.err.println("Runtime error during assignment: " + e.getMessage());
            return null;
        }

        System.out.println("Value: " + value);

        // Generate the appropriate store instruction based on the value type
        if (value instanceof Integer) {
            if (!isArithmeticExpression) {
                // Literal was already loaded by evaluateExpression
            }
            isArithmeticExpression = false;
            emitIntStore(index);
        } else if (value instanceof String) {
            if (!mainMethodCode.toString().contains("ldc \"" + value + "\"")) {
                mainMethodCode.append("    ldc \"").append(value).append("\"\n");
            }
            emitObjectStore(index);
        } else if (value instanceof Boolean) {
            if (!mainMethodCode.toString().contains("ldc " + (Boolean.TRUE.equals(value) ? "1" : "0"))) {
                mainMethodCode.append("    ldc ").append(Boolean.TRUE.equals(value) ? "1" : "0").append("\n");
            }
            emitIntStore(index);
        } else if (value instanceof Object[]) {
            emitObjectStore(index);
        } else {
            System.err.println("Error: Unsupported value type for Jasmin generation.");
            return null;
        }

        boolean assigned = symbolTable.assign(varName, value);
        if (assigned) {
            System.out.println("Assigned variable: " + varName + " = " + value);
        } else {
            System.err.println("Error: Could not assign value to variable '" + varName + "'.");
        }

        return null;
    }

    // ═════════════════════════════════════════════════════════════════════
    // PRINT
    // ═════════════════════════════════════════════════════════════════════

    @Override
    public Void visitImprimir(MiniBasicParser.ImprimirContext ctx) {
        System.out.println("Visiting print...");

        List<MiniBasicParser.ExprContext> expressions = ctx.concatExpr().expr();

        // Check for multiplier syntax (e.g., "3*PRINT")
        boolean hasMultiplier = ctx.INT() != null && ctx.MULTIPLICAR() != null;
        int multiplier = hasMultiplier ? Integer.parseInt(ctx.INT().getText()) : 1;

        if (expressions.size() == 1) {
            // Single expression print
            MiniBasicParser.ExprContext singleExpr = expressions.get(0);
            Object value = evaluateExpressionForPrint(singleExpr);

            if (value instanceof String) {
                String strValue = (String) value;
                for (int i = 0; i < multiplier; i++) {
                    mainMethodCode.append("    ldc \"").append(strValue).append("\"\n");
                    emitPrintlnString();
                }
            } else if (value instanceof Integer) {
                mainMethodCode.append("    ldc ").append(value).append("\n");
                mainMethodCode.append("    invokestatic java/lang/Integer/toString(I)Ljava/lang/String;\n");
                for (int i = 0; i < multiplier; i++) {
                    emitPrintlnString();
                }
            } else if (value instanceof Boolean) {
                String boolStr = (Boolean) value ? "true" : "false";
                for (int i = 0; i < multiplier; i++) {
                    mainMethodCode.append("    ldc \"").append(boolStr).append("\"\n");
                    emitPrintlnString();
                }
            } else {
                System.err.println("Error: Unsupported value type in PRINT.");
                return null;
            }
        } else {
            // Concatenation of multiple expressions via StringBuilder
            mainMethodCode.append("    new java/lang/StringBuilder\n");
            mainMethodCode.append("    dup\n");
            mainMethodCode.append("    invokespecial java/lang/StringBuilder/<init>()V\n");

            for (MiniBasicParser.ExprContext expr : expressions) {
                Object value = evaluateExpressionForPrint(expr);

                if (value instanceof String) {
                    mainMethodCode.append("    ldc \"").append(value).append("\"\n");
                    mainMethodCode.append("    invokevirtual java/lang/StringBuilder/append(Ljava/lang/String;)Ljava/lang/StringBuilder;\n");
                } else if (value instanceof Integer) {
                    mainMethodCode.append("    ldc ").append(value).append("\n");
                    mainMethodCode.append("    invokevirtual java/lang/StringBuilder/append(I)Ljava/lang/StringBuilder;\n");
                } else if (value instanceof Boolean) {
                    String boolStr = (Boolean) value ? "true" : "false";
                    mainMethodCode.append("    ldc \"").append(boolStr).append("\"\n");
                    mainMethodCode.append("    invokevirtual java/lang/StringBuilder/append(Ljava/lang/String;)Ljava/lang/StringBuilder;\n");
                } else {
                    System.err.println("Error: Unsupported value type in PRINT.");
                    return null;
                }
            }

            mainMethodCode.append("    invokevirtual java/lang/StringBuilder/toString()Ljava/lang/String;\n");

            if (hasMultiplier) {
                mainMethodCode.append("    dup\n");
                mainMethodCode.append("    ldc ").append(multiplier).append("\n");
                mainMethodCode.append("    invokestatic java/lang/String/concat(Ljava/lang/String;I)Ljava/lang/String;\n");
            }

            emitPrintlnString();
        }

        return null;
    }

    // ═════════════════════════════════════════════════════════════════════
    // CONDITIONALS (IF / ELSE)
    // ═════════════════════════════════════════════════════════════════════

    @Override
    public Void visitCondicion(MiniBasicParser.CondicionContext ctx) {
        System.out.println("Visiting condition...");

        mainMethodCode.append("    ; Evaluate condition\n");

        if (ctx.expr() != null) {
            if (ctx.expr().INT() != null) {
                // Literal integer condition (e.g., IF 1 THEN ...)
                emitLiteralCondition(ctx);
            } else if (ctx.expr().ID() != null) {
                // Variable condition (e.g., IF x THEN ...)
                emitVariableCondition(ctx);
            } else if (ctx.expr().expr(0) != null && ctx.expr().expr(1) != null) {
                // Binary comparison (e.g., IF x < 5 THEN ...)
                emitBinaryCondition(ctx);
            } else {
                System.err.println("Error: Unsupported condition expression.");
                return null;
            }
        }
        return null;
    }

    /**
     * Emits Jasmin code for an IF condition with a literal integer value.
     * A non-zero literal is truthy; zero is falsy.
     */
    private void emitLiteralCondition(MiniBasicParser.CondicionContext ctx) {
        String falseLabel = generateUniqueLabel("comparisonFalse");
        String endLabel = generateUniqueLabel("endIf");

        int literalValue = Integer.parseInt(ctx.expr().INT().getText());
        mainMethodCode.append(literalValue != 0 ? "    iconst_1\n" : "    iconst_0\n");

        if (!ctx.elseInstrs.isEmpty()) {
            mainMethodCode.append("    ifeq ").append(falseLabel).append("\n");
        } else {
            mainMethodCode.append("    ifeq ").append(endLabel).append("\n");
        }

        // THEN block
        for (MiniBasicParser.InstruccionContext thenInstr : ctx.thenInstrs) {
            visit(thenInstr);
        }
        mainMethodCode.append("    goto ").append(endLabel).append("\n");

        // ELSE block (if present)
        if (!ctx.elseInstrs.isEmpty()) {
            mainMethodCode.append(falseLabel).append(":\n");
            for (MiniBasicParser.InstruccionContext elseInstr : ctx.elseInstrs) {
                visit(elseInstr);
            }
        }

        mainMethodCode.append(endLabel).append(":\n");
    }

    /**
     * Emits Jasmin code for an IF condition that tests a variable's value.
     */
    private void emitVariableCondition(MiniBasicParser.CondicionContext ctx) {
        String varName = ctx.expr().ID().getText();
        Symbol symbol = symbolTable.resolve(varName);
        if (symbol != null && symbol.getType().equals("int")) {
            int index = variableIndex.get(varName);
            emitIntLoad(index);
        } else {
            System.err.println("Error: Only integers are supported in conditions.");
        }
    }

    /**
     * Emits Jasmin code for an IF condition with a binary comparison operator.
     */
    private void emitBinaryCondition(MiniBasicParser.CondicionContext ctx) {
        MiniBasicParser.ExprContext left = ctx.expr().expr(0);
        MiniBasicParser.ExprContext right = ctx.expr().expr(1);

        emitCodeForExpression(left);
        emitCodeForExpression(right);

        String operator = ctx.expr().operador().getText();
        String falseLabel = generateUniqueLabel("comparisonFalse");
        String endLabel = generateUniqueLabel("endIf");

        // Emit the inverse comparison jump (jump when condition is FALSE)
        String jumpInstruction = getInverseComparisonJump(operator);
        if (jumpInstruction == null) {
            System.err.println("Error: Unsupported comparison operator in IF condition.");
            return;
        }

        if (!ctx.elseInstrs.isEmpty()) {
            mainMethodCode.append("    ").append(jumpInstruction).append(" ").append(falseLabel).append("\n");
        } else {
            mainMethodCode.append("    ").append(jumpInstruction).append(" ").append(endLabel).append("\n");
        }

        // THEN block
        for (MiniBasicParser.InstruccionContext thenInstr : ctx.thenInstrs) {
            visit(thenInstr);
        }
        mainMethodCode.append("    goto ").append(endLabel).append("\n");

        // ELSE block (if present)
        if (!ctx.elseInstrs.isEmpty()) {
            mainMethodCode.append(falseLabel).append(":\n");
            for (MiniBasicParser.InstruccionContext elseInstr : ctx.elseInstrs) {
                visit(elseInstr);
            }
        }

        mainMethodCode.append(endLabel).append(":\n");
    }

    // ═════════════════════════════════════════════════════════════════════
    // WHILE LOOP
    // ═════════════════════════════════════════════════════════════════════

    @Override
    public Void visitBucleWhile(MiniBasicParser.BucleWhileContext ctx) {
        System.out.println("Visiting while loop...");

        isInsideWhileLoop = true;

        String labelStart = generateUniqueLabel("WHILE_START");
        String labelEnd = generateUniqueLabel("WHILE_END");

        currentLoopStartLabel = labelStart;
        currentLoopEndLabel = labelEnd;

        // Loop start label
        mainMethodCode.append(labelStart).append(":\n");

        // Evaluate condition
        if (ctx.expr() != null) {
            emitWhileCondition(ctx.expr(), labelEnd);
        } else {
            System.err.println("Error: Missing condition in WHILE statement.");
            return null;
        }

        // Loop body
        for (MiniBasicParser.InstruccionContext instr : ctx.instruccion()) {
            visit(instr);
        }

        // Jump back to condition evaluation
        mainMethodCode.append("    goto ").append(labelStart).append("\n");

        // Loop end label
        mainMethodCode.append(labelEnd).append(":\n");

        isInsideWhileLoop = false;
        return null;
    }

    /**
     * Emits the condition evaluation code for a WHILE loop.
     * When the condition is false, jumps to the end label.
     */
    private void emitWhileCondition(MiniBasicParser.ExprContext expr, String labelEnd) {
        if (expr.ID() != null) {
            String varName = expr.ID().getText();
            Symbol symbol = symbolTable.resolve(varName);
            if (symbol != null) {
                int index = variableIndex.get(varName);
                emitIntLoad(index);
            } else {
                System.err.println("Error: Undefined variable '" + varName + "'.");
            }
        } else if (expr.INT() != null) {
            mainMethodCode.append("    ldc ").append(expr.INT().getText()).append("\n");
        } else if (expr.expr(0) != null && expr.expr(1) != null) {
            // Binary comparison in while condition
            emitWhileCondition(expr.expr(0), labelEnd);
            emitWhileCondition(expr.expr(1), labelEnd);

            String operator = expr.operador().getText();
            String jumpInstruction = getInverseComparisonJump(operator);
            if (jumpInstruction != null) {
                mainMethodCode.append("    ").append(jumpInstruction).append(" ").append(labelEnd).append("\n");
            } else {
                System.err.println("Error: Unsupported operator in condition.");
            }
        } else {
            System.err.println("Error: Unsupported expression type.");
        }
    }

    // ═════════════════════════════════════════════════════════════════════
    // FOR LOOP
    // ═════════════════════════════════════════════════════════════════════

    @Override
    public Void visitBucleFor(MiniBasicParser.BucleForContext ctx) {
        System.out.println("Visiting for loop...");

        String labelCondition = generateUniqueLabel("FOR_CONDITION");
        String labelEnd = generateUniqueLabel("FOR_END");

        currentLoopStartLabel = labelCondition;
        currentLoopEndLabel = labelEnd;

        // Initialize loop control variable
        String varName = ctx.ID().getText();
        Symbol symbol = symbolTable.resolve(varName);

        if (symbol == null) {
            symbolTable.define(varName, "int");
            symbol = symbolTable.resolve(varName);
            if (!variableIndex.containsKey(varName)) {
                variableIndex.put(varName, nextLocalIndex++);
            }
        }

        int index = variableIndex.get(varName);

        // Set initial value (start - 1 because increment happens at top of loop)
        int startValue = Integer.parseInt(ctx.indexInt.getText()) - 1;
        int endValue = Integer.parseInt(ctx.limitInt.getText());

        mainMethodCode.append("    ldc ").append(startValue).append("\n");
        emitIntStore(index);

        symbol.setValue(startValue + 1);
        symbol.setStorageName("local_" + index);

        // Condition label — also where increment happens
        mainMethodCode.append(labelCondition).append(":\n");

        // Increment the loop variable
        mainMethodCode.append("    ldc 1\n");
        emitIntLoad(index);
        mainMethodCode.append("    iadd\n");
        emitIntStore(index);

        // Check if loop variable exceeds the limit
        emitIntLoad(index);
        mainMethodCode.append("    ldc ").append(endValue).append("\n");
        mainMethodCode.append("    if_icmpgt ").append(labelEnd).append("\n");

        // Loop body
        for (MiniBasicParser.InstruccionContext instr : ctx.instruccion()) {
            visit(instr);
        }

        // Jump back to condition/increment
        mainMethodCode.append("    goto ").append(labelCondition).append("\n");

        // Loop end
        mainMethodCode.append(labelEnd).append(":\n");

        return null;
    }

    // ═════════════════════════════════════════════════════════════════════
    // REPEAT/UNTIL LOOP
    // ═════════════════════════════════════════════════════════════════════

    @Override
    public Void visitRepetir(MiniBasicParser.RepetirContext ctx) {
        System.out.println("Visiting REPEAT...UNTIL loop");

        String loopStart = generateUniqueLabel("LOOP_START");
        String conditionCheck = generateUniqueLabel("CONDITION_CHECK");
        String loopEnd = generateUniqueLabel("LOOP_END");

        // Loop body start
        mainMethodCode.append(loopStart).append(":\n");

        for (MiniBasicParser.InstruccionContext instrCtx : ctx.instruccion()) {
            visit(instrCtx);
        }

        // Condition check: UNTIL variable = targetValue
        mainMethodCode.append(conditionCheck).append(":\n");

        String varName = ctx.ID().getText();
        Symbol symbol = symbolTable.resolve(varName);
        if (symbol == null) {
            throw new RuntimeException("Variable '" + varName + "' is not defined.");
        }

        int index = variableIndex.get(varName);
        emitIntLoad(index);

        int targetValue = Integer.parseInt(ctx.entero().getText());
        mainMethodCode.append("    ldc ").append(targetValue).append("\n");

        // Continue looping while variable != target
        mainMethodCode.append("    if_icmpne ").append(loopStart).append("\n");

        mainMethodCode.append(loopEnd).append(":\n");

        return null;
    }

    // ═════════════════════════════════════════════════════════════════════
    // LOOP CONTROL (CONTINUE / EXIT)
    // ═════════════════════════════════════════════════════════════════════

    @Override
    public Void visitControlBucle(MiniBasicParser.ControlBucleContext ctx) {
        System.out.println("Visiting loop control...");
        String control = ctx.getText().toUpperCase();

        if (currentLoopStartLabel == null || currentLoopEndLabel == null) {
            System.err.println("Error: Loop control statement used outside of a loop.");
            return null;
        }

        if (control.equals("CONTINUE")) {
            mainMethodCode.append("; continue statement\n");
            mainMethodCode.append("    goto ").append(currentLoopStartLabel).append("\n");
        } else if (control.equals("EXIT")) {
            mainMethodCode.append("    goto ").append(currentLoopEndLabel).append("\n");
        } else {
            System.err.println("Error: Unknown loop control statement '" + control + "'.");
        }
        return null;
    }

    // ═════════════════════════════════════════════════════════════════════
    // SUBROUTINES & FUNCTIONS
    // ═════════════════════════════════════════════════════════════════════

    @Override
    public Void visitSubrutinaDecl(MiniBasicParser.SubrutinaDeclContext ctx) {
        System.out.println("Visiting subroutine declaration...");

        String subName = ctx.ID().getText();

        // Switch code output to the subroutine buffer
        activeCodeBuffer = subroutineCode;

        subroutineCode.append(".method public static ").append(subName).append("(");

        // Add parameter type signatures
        if (ctx.parametros() != null) {
            for (int i = 0; i < ctx.parametros().ID().size(); i++) {
                subroutineCode.append("Ljava/lang/String;");
            }
        }
        subroutineCode.append(")V\n");

        subroutineCode.append(".limit stack 100\n");
        subroutineCode.append(".limit locals 100\n");

        // Register parameters as local variables
        if (ctx.parametros() != null) {
            List<TerminalNode> params = ctx.parametros().ID();
            for (int i = 0; i < params.size(); i++) {
                String paramName = params.get(i).getText();
                if (!variableIndex.containsKey(paramName)) {
                    variableIndex.put(paramName, nextLocalIndex++);
                }
                int idx = variableIndex.get(paramName);
                Symbol symbol = new Symbol(paramName, "string");
                symbol.setStorageName("local_" + idx);
            }
        }

        // Generate code for the subroutine body
        for (MiniBasicParser.InstruccionContext instr : ctx.instruccion()) {
            visit(instr);
        }

        subroutineCode.append("    return\n");
        subroutineCode.append(".end method\n");

        // Restore code output to main method buffer
        activeCodeBuffer = mainMethodCode;

        return null;
    }

    @Override
    public Void visitLlamadaSubrutina(MiniBasicParser.LlamadaSubrutinaContext ctx) {
        System.out.println("Visiting subroutine call...");

        String subName = ctx.ID().getText();

        // Generate code for each argument
        if (ctx.argumentos() != null) {
            for (MiniBasicParser.ExprContext arg : ctx.argumentos().expr()) {
                visit(arg);
            }
        }

        // Invoke the subroutine
        mainMethodCode.append("    invokestatic MiniBProgram/").append(subName).append("(");
        if (ctx.argumentos() != null) {
            for (int i = 0; i < ctx.argumentos().expr().size(); i++) {
                mainMethodCode.append("Ljava/lang/String;");
            }
        }
        mainMethodCode.append(")V\n");

        return null;
    }

    @Override
    public Void visitFuncionDecl(MiniBasicParser.FuncionDeclContext ctx) {
        System.out.println("Visiting function declaration...");

        String funcName = ctx.ID().getText();

        mainMethodCode.append(".method public static ").append(funcName).append("(");
        if (ctx.parametros() != null) {
            for (int i = 0; i < ctx.parametros().ID().size(); i++) {
                mainMethodCode.append("Ljava/lang/String;");
            }
        }
        mainMethodCode.append(")I\n"); // Functions return an integer

        mainMethodCode.append(".limit stack 100\n");
        mainMethodCode.append(".limit locals 100\n");

        // Register parameters
        if (ctx.parametros() != null) {
            List<TerminalNode> params = ctx.parametros().ID();
            for (int i = 0; i < params.size(); i++) {
                String paramName = params.get(i).getText();
                if (!variableIndex.containsKey(paramName)) {
                    variableIndex.put(paramName, nextLocalIndex++);
                }
                int idx = variableIndex.get(paramName);
                Symbol symbol = new Symbol(paramName, "int");
                symbol.setStorageName("local_" + idx);
                symbolTable.define(symbol.getName(), symbol.getType());
            }
        }

        // Function body
        for (MiniBasicParser.InstruccionContext instr : ctx.instruccion()) {
            visit(instr);
        }

        // Return expression
        visit(ctx.expr());
        mainMethodCode.append("    ireturn\n");
        mainMethodCode.append(".end method\n");

        return null;
    }

    // ═════════════════════════════════════════════════════════════════════
    // INPUT
    // ═════════════════════════════════════════════════════════════════════

    @Override
    public Void visitEntrada(MiniBasicParser.EntradaContext ctx) {
        System.out.println("Visiting input...");

        String message = ctx.STRING().getText();
        String varName = ctx.ID().getText();

        // Define the variable if it hasn't been declared yet
        Symbol symbol = symbolTable.resolve(varName);
        if (symbol == null) {
            symbolTable.define(varName, "string");
            symbol = symbolTable.resolve(varName);
            if (!variableIndex.containsKey(varName)) {
                variableIndex.put(varName, nextLocalIndex++);
            }
        }
        int index = variableIndex.get(varName);
        symbol.setStorageName("local_" + index);

        // Print the prompt message
        mainMethodCode.append("    getstatic java/lang/System/out Ljava/io/PrintStream;\n");
        mainMethodCode.append("    ldc ").append(message).append("\n");
        mainMethodCode.append("    invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V\n");

        // Read a line from stdin via Scanner
        mainMethodCode.append("    new java/util/Scanner\n");
        mainMethodCode.append("    dup\n");
        mainMethodCode.append("    getstatic java/lang/System/in Ljava/io/InputStream;\n");
        mainMethodCode.append("    invokespecial java/util/Scanner/<init>(Ljava/io/InputStream;)V\n");
        mainMethodCode.append("    invokevirtual java/util/Scanner/nextLine()Ljava/lang/String;\n");

        // Store the result in the local variable
        emitObjectStore(index);

        System.out.println(getSymbolTable());

        return null;
    }

    // ═════════════════════════════════════════════════════════════════════
    // TYPE INFERENCE
    // ═════════════════════════════════════════════════════════════════════

    /**
     * Infers the type of an expression based on its AST structure.
     *
     * @param expr the expression to analyze
     * @return the inferred type string ("int", "string", "boolean", "array[...]", or "unknown")
     */
    private String inferType(MiniBasicParser.ExprContext expr) {
        if (expr.INT() != null) {
            return "int";
        } else if (expr.STRING() != null) {
            return "string";
        } else if (expr.booleano() != null) {
            return "boolean";
        } else if (expr.ID() != null) {
            Symbol symbol = symbolTable.resolve(expr.ID().getText());
            return (symbol != null) ? symbol.getType() : "unknown";
        } else if (expr.funcion() != null) {
            String funcName = expr.funcion().getChild(0).getText().toUpperCase();
            return switch (funcName) {
                case "VAL", "LEN" -> "int";
                case "ISNAN" -> "boolean";
                default -> "unknown";
            };
        } else if (expr.operador() != null && expr.expr().size() == 2) {
            String leftType = inferType(expr.expr(0));
            String rightType = inferType(expr.expr(1));

            if ("boolean".equals(leftType) && "boolean".equals(rightType)) {
                return "boolean";
            } else if ("int".equals(leftType) && "int".equals(rightType)) {
                return "int";
            } else if ("string".equals(leftType) && "string".equals(rightType)) {
                return "string";
            } else {
                return "unknown";
            }
        } else if (expr.arrayExpr() != null) {
            if (expr.arrayExpr().expr().isEmpty()) {
                return "unknown";
            }
            String elementType = inferType(expr.arrayExpr().expr(0));
            return "array[" + elementType + "]";
        } else {
            return "unknown";
        }
    }

    // ═════════════════════════════════════════════════════════════════════
    // EXPRESSION EVALUATION (with Jasmin code emission)
    // ═════════════════════════════════════════════════════════════════════

    /**
     * Evaluates an expression, emitting Jasmin bytecode and returning
     * the interpreted value. Used during assignment and arithmetic.
     */
    private Object evaluateExpression(MiniBasicParser.ExprContext expr) {
        if (expr.INT() != null) {
            int value = Integer.parseInt(expr.INT().getText());
            System.out.println("Evaluating INT: " + value);
            mainMethodCode.append("    ldc ").append(value).append("\n");
            return value;
        } else if (expr.STRING() != null) {
            String str = expr.STRING().getText();
            String value = str.substring(1, str.length() - 1);
            System.out.println("Evaluating STRING: " + value);
            mainMethodCode.append("    ldc \"").append(value).append("\"\n");
            return value;
        } else if (expr.funcion() != null) {
            System.out.println("Evaluating FUNCTION: " + expr.funcion().getText());
            return evaluateBuiltinFunction(expr.funcion());
        } else if (expr.ID() != null) {
            return evaluateVariableReference(expr);
        } else if (expr.operador() != null && expr.expr().size() == 2) {
            return evaluateBinaryOperation(expr);
        } else if (expr.PARIZQ() != null && expr.PARDER() != null) {
            // Parenthesized expression — unwrap and evaluate inner
            Object val = evaluateExpression(expr.expr(0));
            System.out.println("Evaluating parenthesized expression: " + val);
            return val;
        } else if (expr.booleano() != null) {
            boolean value = Boolean.parseBoolean(expr.booleano().getText());
            System.out.println("Evaluating BOOLEAN: " + value);
            mainMethodCode.append("    iconst_").append(value ? "1" : "0").append("\n");
            return value;
        } else {
            throw new RuntimeException("Error: Unsupported expression.");
        }
    }

    /**
     * Evaluates a variable reference, emitting the appropriate load instruction.
     */
    private Object evaluateVariableReference(MiniBasicParser.ExprContext expr) {
        String varName = expr.ID().getText();
        Symbol symbol = symbolTable.resolve(varName);
        if (symbol != null) {
            System.out.println("Evaluating ID: " + varName + " = " + symbol.getValue());
            int index = variableIndex.get(varName);
            if (symbol.getType().equals("int")) {
                emitIntLoad(index);
            } else if (symbol.getType().equals("string")) {
                emitObjectLoad(index);
            }
            return symbol.getValue();
        } else {
            System.err.println("Error: Variable '" + varName + "' is not defined.");
            return null;
        }
    }

    /**
     * Evaluates a binary operation, emitting Jasmin arithmetic/comparison instructions.
     */
    private Object evaluateBinaryOperation(MiniBasicParser.ExprContext expr) {
        Object left = evaluateExpression(expr.expr(0));
        Object right = evaluateExpression(expr.expr(1));
        String op = expr.operador().getText();

        switch (op) {
            case "+":
                if (left instanceof Boolean && right instanceof Boolean) {
                    // Boolean OR
                    mainMethodCode.append("    ifne L1\n    iconst_0\n    goto L2\nL1:\n    iconst_1\nL2:\n");
                    return (Boolean) left || (Boolean) right;
                }
                if (left instanceof Integer && right instanceof Integer) {
                    mainMethodCode.append("    iadd\n");
                    return (Integer) left + (Integer) right;
                } else if (left instanceof String && right instanceof String) {
                    emitStringConcatenation((String) left, (String) right);
                    return (String) left + (String) right;
                } else {
                    throw new RuntimeException("Error: '+' cannot be applied to "
                            + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName());
                }
            case "*":
                if (left instanceof Boolean && right instanceof Boolean) {
                    // Boolean AND
                    mainMethodCode.append("    ifeq L1\n    iconst_1\n    goto L2\nL1:\n    iconst_0\nL2:\n");
                    return (Boolean) left && (Boolean) right;
                }
                if (left instanceof Integer && right instanceof Integer) {
                    mainMethodCode.append("    imul\n");
                    return (Integer) left * (Integer) right;
                } else {
                    throw new RuntimeException("Error: '*' requires integer operands.");
                }
            case "-":
                if (left instanceof Integer && right instanceof Integer) {
                    mainMethodCode.append("    isub\n");
                    return (Integer) left - (Integer) right;
                } else {
                    throw new RuntimeException("Error: '-' requires integer operands.");
                }
            case "/":
                if (left instanceof Integer && right instanceof Integer) {
                    if ((Integer) right != 0) {
                        mainMethodCode.append("    idiv\n");
                        return (Integer) left / (Integer) right;
                    } else {
                        throw new RuntimeException("Error: Division by zero.");
                    }
                } else {
                    throw new RuntimeException("Error: '/' requires integer operands.");
                }
            case "mod":
            case "MOD":
                if (left instanceof Integer && right instanceof Integer) {
                    mainMethodCode.append("    irem\n");
                    return (Integer) left % (Integer) right;
                } else {
                    throw new RuntimeException("Error: 'mod' requires integer operands.");
                }
            case "=":
                if (left instanceof Integer && right instanceof Integer) {
                    mainMethodCode.append("    if_icmpeq L1\n    iconst_0\n    goto L2\nL1:\n    iconst_1\nL2:\n");
                    return left.equals(right) ? 1 : 0;
                } else if (left instanceof String && right instanceof String) {
                    mainMethodCode.append("    invokevirtual java/lang/String/equals(Ljava/lang/Object;)Z\n");
                    return left.equals(right) ? 1 : 0;
                } else {
                    throw new RuntimeException("Error: Cannot compare "
                            + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName());
                }
            default:
                throw new RuntimeException("Error: Unknown operator: " + op);
        }
    }

    /**
     * Evaluates an expression for PRINT without emitting load instructions
     * (used for string interpolation in the print visitor).
     */
    private Object evaluateExpressionForPrint(MiniBasicParser.ExprContext expr) {
        if (expr.INT() != null) {
            return Integer.parseInt(expr.INT().getText());
        } else if (expr.STRING() != null) {
            String str = expr.STRING().getText();
            return str.substring(1, str.length() - 1);
        } else if (expr.ID() != null) {
            String varName = expr.ID().getText();
            Symbol symbol = symbolTable.resolve(varName);
            if (symbol != null) {
                return symbol.getValue();
            } else {
                System.err.println("Error: Variable '" + varName + "' is not defined.");
                return "";
            }
        } else if (expr.operador() != null) {
            Object left = evaluateExpressionForPrint(expr.expr(0));
            Object right = evaluateExpressionForPrint(expr.expr(1));
            String op = expr.operador().getText();

            if (left instanceof Integer && right instanceof Integer) {
                int leftVal = (Integer) left;
                int rightVal = (Integer) right;
                return switch (op) {
                    case "+" -> leftVal + rightVal;
                    case "-" -> leftVal - rightVal;
                    case "*" -> leftVal * rightVal;
                    case "/" -> {
                        if (rightVal != 0) yield leftVal / rightVal;
                        System.err.println("Error: Division by zero.");
                        yield 0;
                    }
                    case "MOD", "mod" -> leftVal % rightVal;
                    default -> {
                        System.err.println("Unsupported operator: " + op);
                        yield 0;
                    }
                };
            } else if (op.equals("+")) {
                return left.toString() + right.toString();
            } else {
                System.err.println("Error: Operator '" + op + "' is not supported for non-numeric values.");
                return "";
            }
        } else if (expr.funcion() != null) {
            return evaluateBuiltinFunction(expr.funcion());
        }

        System.err.println("Error: Unsupported expression type in PRINT.");
        return "";
    }

    // ═════════════════════════════════════════════════════════════════════
    // ARRAYS
    // ═════════════════════════════════════════════════════════════════════

    /**
     * Evaluates an array expression — either element access or literal creation.
     */
    private Object evaluateArray(MiniBasicParser.ArrayExprContext arrayExpr) {
        String varName = arrayExpr.ID().getText();
        Symbol symbol = symbolTable.resolve(varName);

        if (symbol != null) {
            System.out.println("Evaluating array access: " + varName);

            if (arrayExpr.expr().size() == 1) {
                // Element access: array[index]
                Object indexObj = evaluateExpression(arrayExpr.expr(0));
                if (symbol.getType().equals("array")) {
                    Object[] array = (Object[]) symbol.getValue();
                    int idx = (Integer) indexObj;
                    if (idx >= 0 && idx < array.length) {
                        return array[idx];
                    } else {
                        throw new RuntimeException("Error: Array index out of bounds.");
                    }
                } else {
                    throw new RuntimeException("Error: Variable '" + varName + "' is not an array.");
                }
            } else if (arrayExpr.expr().size() > 1) {
                // Array literal creation: ARRAY[1, 2, 3]
                List<Object> elements = new ArrayList<>();
                for (MiniBasicParser.ExprContext elemExpr : arrayExpr.expr()) {
                    elements.add(evaluateExpression(elemExpr));
                }
                symbol.setValue(elements.toArray());
                return elements.toArray();
            }
        } else {
            System.err.println("Error: Variable '" + varName + "' is not defined.");
            return null;
        }
        return null;
    }

    // ═════════════════════════════════════════════════════════════════════
    // BUILT-IN FUNCTIONS (VAL, LEN, ISNAN)
    // ═════════════════════════════════════════════════════════════════════

    /**
     * Evaluates a built-in function call and returns its result.
     *
     * @param funcion the function parse context
     * @return the evaluated result
     */
    private Object evaluateBuiltinFunction(MiniBasicParser.FuncionContext funcion) {
        String funcName = funcion.getChild(0).getText().toUpperCase();
        Object arg = evaluateExpression(funcion.expr());

        return switch (funcName) {
            case "VAL" -> {
                if (arg instanceof String) {
                    try {
                        int result = Integer.parseInt((String) arg);
                        System.out.println("Evaluating FUNC VAL: " + arg + " -> " + result);
                        yield result;
                    } catch (NumberFormatException e) {
                        System.err.println("Error: VAL cannot convert string to integer.");
                        yield 0;
                    }
                }
                yield 0;
            }
            case "LEN" -> {
                if (arg instanceof String) {
                    int result = ((String) arg).length();
                    System.out.println("Evaluating FUNC LEN: " + arg + " -> " + result);
                    yield result;
                }
                yield 0;
            }
            case "ISNAN" -> {
                if (arg instanceof String) {
                    try {
                        Double.parseDouble((String) arg);
                        System.out.println("Evaluating FUNC ISNAN: " + arg + " -> false");
                        yield 0; // Not NaN
                    } catch (NumberFormatException e) {
                        System.out.println("Evaluating FUNC ISNAN: " + arg + " -> true");
                        yield 1; // Is NaN
                    }
                }
                yield 1;
            }
            default -> {
                System.err.println("Unknown function: " + funcName);
                yield null;
            }
        };
    }

    // ═════════════════════════════════════════════════════════════════════
    // HELPER METHODS — Jasmin instruction emission
    // ═════════════════════════════════════════════════════════════════════

    /**
     * Emits an integer load instruction, using the compact form for indices 0-3.
     */
    private void emitIntLoad(int index) {
        if (index >= 0 && index <= 3) {
            mainMethodCode.append("    iload_").append(index).append("\n");
        } else {
            mainMethodCode.append("    iload ").append(index).append("\n");
        }
    }

    /**
     * Emits an integer store instruction, using the compact form for indices 0-3.
     */
    private void emitIntStore(int index) {
        if (index >= 0 && index <= 3) {
            mainMethodCode.append("    istore_").append(index).append("\n");
        } else {
            mainMethodCode.append("    istore ").append(index).append("\n");
        }
    }

    /**
     * Emits an object (reference) load instruction.
     */
    private void emitObjectLoad(int index) {
        if (index >= 0 && index <= 3) {
            mainMethodCode.append("    aload_").append(index).append("\n");
        } else {
            mainMethodCode.append("    aload ").append(index).append("\n");
        }
    }

    /**
     * Emits an object (reference) store instruction.
     */
    private void emitObjectStore(int index) {
        if (index >= 0 && index <= 3) {
            mainMethodCode.append("    astore_").append(index).append("\n");
        } else {
            mainMethodCode.append("    astore ").append(index).append("\n");
        }
    }

    /**
     * Emits a System.out.println call for a String on top of the stack.
     */
    private void emitPrintlnString() {
        mainMethodCode.append("    getstatic java/lang/System/out Ljava/io/PrintStream;\n");
        mainMethodCode.append("    swap\n");
        mainMethodCode.append("    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V\n");
    }

    /**
     * Emits StringBuilder-based string concatenation for two string values.
     */
    private void emitStringConcatenation(String left, String right) {
        mainMethodCode.append("    new java/lang/StringBuilder\n");
        mainMethodCode.append("    dup\n");
        mainMethodCode.append("    invokespecial java/lang/StringBuilder/<init>()V\n");
        mainMethodCode.append("    ldc \"").append(left).append("\"\n");
        mainMethodCode.append("    invokevirtual java/lang/StringBuilder/append(Ljava/lang/String;)Ljava/lang/StringBuilder;\n");
        mainMethodCode.append("    ldc \"").append(right).append("\"\n");
        mainMethodCode.append("    invokevirtual java/lang/StringBuilder/append(Ljava/lang/String;)Ljava/lang/StringBuilder;\n");
        mainMethodCode.append("    invokevirtual java/lang/StringBuilder/toString()Ljava/lang/String;\n");
    }

    /**
     * Emits Jasmin code to load an expression value (variable or literal) onto the stack.
     * Used within condition evaluation.
     */
    private void emitCodeForExpression(MiniBasicParser.ExprContext expr) {
        if (expr.ID() != null) {
            String varName = expr.ID().getText();
            Symbol symbol = symbolTable.resolve(varName);
            if (symbol != null && symbol.getType().equals("int")) {
                int index = variableIndex.get(varName);
                emitIntLoad(index);
            } else {
                System.err.println("Error: Only integers are supported in comparisons.");
            }
        } else if (expr.INT() != null) {
            mainMethodCode.append("    ldc ").append(expr.INT().getText()).append("\n");
        } else {
            System.err.println("Error: Unsupported expression in condition.");
        }
    }

    /**
     * Returns the inverse JVM comparison jump instruction for the given operator.
     * The "inverse" is used because we jump when the condition is FALSE.
     *
     * @param operator the comparison operator string
     * @return the Jasmin jump instruction, or null if unsupported
     */
    private String getInverseComparisonJump(String operator) {
        return switch (operator) {
            case "<"  -> "if_icmpge";
            case ">"  -> "if_icmple";
            case "==" -> "if_icmpne";
            case "!=" -> "if_icmpeq";
            case "<=" -> "if_icmpgt";
            case ">=" -> "if_icmplt";
            default   -> null;
        };
    }

    /**
     * Generates a unique label for JVM jump targets.
     *
     * @param prefix a descriptive prefix for the label
     * @return a unique label string
     */
    private String generateUniqueLabel(String prefix) {
        return prefix + "_" + (labelCounter++);
    }

    // ── Public Accessors ─────────────────────────────────────────────────

    /**
     * Returns the generated Jasmin code for the main method.
     *
     * @return the Jasmin assembly string
     */
    public String getJasminCode() {
        return mainMethodCode.toString();
    }

    /**
     * Returns the generated Jasmin code for subroutine methods.
     *
     * @return the subroutine Jasmin assembly string
     */
    public String getJasminCodeSubroutine() {
        return subroutineCode.toString();
    }

    /**
     * Checks if a value is truthy (non-zero integer, true boolean, or non-null).
     *
     * @param value the value to test
     * @return {@code true} if the value is truthy
     */
    private boolean isTruthy(Object value) {
        if (value instanceof Integer) {
            return (Integer) value != 0;
        } else if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return value != null;
    }
}
