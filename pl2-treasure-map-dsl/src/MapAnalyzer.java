import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Entry point for analyzing map DSL files using the Map grammar.
 *
 * <p>Parses a map definition file and walks the resulting parse tree
 * using a default listener that prints the tree structure.</p>
 *
 * <p>Usage: {@code java MapAnalyzer [map-file.txt]}</p>
 *
 * @author Asier Alamo, Sergio Alonso Zarcero, Andrea Pascual Aguilera
 */
public class MapAnalyzer {

    /**
     * Parses the given map file (or reads from stdin) and walks the tree.
     *
     * @param args optional path to a map definition file
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
        MapLexer lexer = new MapLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MapParser parser = new MapParser(tokens);

        // Generate parse tree from the 'mapa' axiom
        ParseTree tree = parser.mapa();

        // Walk the tree with a default listener
        ParseTreeWalker walker = new ParseTreeWalker();
        Analizador1Listener listener = new Analizador1Listener();
        walker.walk(listener, tree);
    }
}
