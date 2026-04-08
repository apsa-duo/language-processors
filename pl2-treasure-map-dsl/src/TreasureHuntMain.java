import java.util.Scanner;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

/**
 * Entry point for the Treasure Hunt game.
 *
 * <p>This application:</p>
 * <ol>
 *   <li>Parses a map definition file using the Map DSL grammar</li>
 *   <li>Builds a {@link TreasureMap} via the {@link MapParserListener}</li>
 *   <li>Lets the player choose a difficulty level</li>
 *   <li>Runs an interactive dive loop until the game ends</li>
 * </ol>
 *
 * @author Asier Alamo, Sergio Alonso Zarcero, Andrea Pascual Aguilera
 */
public class TreasureHuntMain {

    /** Default path to the map definition file. */
    private static final String DEFAULT_MAP_PATH = "examples/treasure_island.txt";

    /**
     * Launches the Treasure Hunt game.
     *
     * @param args optional: first argument is an alternative map file path
     * @throws Exception if an I/O or parsing error occurs
     */
    public static void main(String[] args) throws Exception {
        // Determine the map file path
        String mapPath = (args.length > 0) ? args[0] : DEFAULT_MAP_PATH;

        // Parse the map definition file
        CharStream input = CharStreams.fromFileName(mapPath);
        MapLexer lexer = new MapLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MapParser parser = new MapParser(tokens);
        TreasureMap map = new TreasureMap();
        MapParserListener listener = new MapParserListener(map);

        // Walk the parse tree to populate the map
        ParseTree tree = parser.mapa();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(listener, tree);
        map = listener.getMap();

        Scanner scanner = new Scanner(System.in);
        int attempts = 0;

        // ── Difficulty Selection ─────────────────────────────────────────
        while (attempts == 0) {
            System.out.println("Select game difficulty:");
            System.out.println("Easy   : 1");
            System.out.println("Medium : 2");
            System.out.println("Hard   : 3");

            if (scanner.hasNextInt()) {
                int difficulty = scanner.nextInt();

                switch (difficulty) {
                    case 1:
                        attempts = 15;
                        System.out.println("Difficulty: Easy (15 attempts)");
                        break;
                    case 2:
                        attempts = 10;
                        System.out.println("Difficulty: Medium (10 attempts)");
                        break;
                    case 3:
                        attempts = 5;
                        System.out.println("Difficulty: Hard (5 attempts)");
                        break;
                    default:
                        System.out.println("Invalid choice. Please select 1, 2, or 3.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); // Clear invalid input
            }
        }

        // ── Game Loop ────────────────────────────────────────────────────
        TreasureHuntGame game = new TreasureHuntGame(map, attempts);

        while (!game.isGameOver()) {
            System.out.print("Enter coordinates to dive (x y): ");

            if (scanner.hasNextInt()) {
                int x = scanner.nextInt();

                if (scanner.hasNextInt()) {
                    int y = scanner.nextInt();
                    game.diveAt(x, y);
                } else {
                    System.out.println("Please enter a valid coordinate.");
                    scanner.next();
                }
            } else {
                System.out.println("Please enter a valid coordinate.");
                scanner.next();
            }
        }

        System.out.println("Game over! Final score: " + game.getScore());
    }
}
