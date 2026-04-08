import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Entry point for analyzing MiniBasic source files using the MiniBasic grammar.
 *
 * <p>Parses a {@code .bas} file and walks the resulting parse tree,
 * printing the tree structure via {@link MiniBTreePrinter}.</p>
 *
 * <p>Usage: {@code java MiniBAnalyzer [program.bas]}</p>
 *
 * @author Asier Alamo, Sergio Alonso Zarcero, Andrea Pascual Aguilera
 */
public class MiniBAnalyzer {

    /**
     * Parses the given MiniBasic source file (or reads from stdin) and
     * walks the parse tree with a tree-printing listener.
     *
     * @param args optional path to a {@code .bas} source file
     * @throws Exception if an I/O or parsing error occurs
     */
    public static void main(String[] args) throws Exception {
        // Determine input source: file argument or stdin
        String inputFile = (args.length > 0) ? args[0] : null;

        InputStream inputStream = System.in;
        if (inputFile != null) {
            inputStream = new FileInputStream(inputFile);
        }

        // Lexing and parsing pipeline
        CharStream input = CharStreams.fromStream(inputStream);
        MiniBasicLexer lexer = new MiniBasicLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MiniBasicParser parser = new MiniBasicParser(tokens);

        // Generate parse tree from the 'prog' axiom
        ParseTree tree = parser.prog();

        // Walk the tree with the pretty-printing listener
        ParseTreeWalker walker = new ParseTreeWalker();
        MiniBTreePrinter listener = new MiniBTreePrinter();
        walker.walk(listener, tree);
    }
}
