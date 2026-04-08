import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * Entry point for the DFA Simulator application.
 *
 * <p>Provides an interactive CLI that allows users to:</p>
 * <ol>
 *   <li>Select a regular expression (configures the DFA)</li>
 *   <li>Validate a string against the selected regex</li>
 *   <li>Generate accepted strings up to a given count and length</li>
 * </ol>
 *
 * @author Sergio Alonso Zarcero
 */
public class AutomatonMain {

    /**
     * Main method — launches the interactive DFA simulator.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {

        // Build the automaton with the shared alphabet and state set
        DeterministicFiniteAutomaton automaton = new DeterministicFiniteAutomaton();
        automaton.loadAlphabet();
        automaton.loadStates();

        // Prompt the user to choose a regular expression
        System.out.println("Select a regular expression (1/2):");
        System.out.println("1. RE1: (b|c)*a(b|c)*");
        System.out.println("2. RE2: a+(a|b|c)*");
        System.out.println("Press 0 to exit.");

        int option = readInteger();

        // Configure the DFA's transition table and accepting states
        switch (option) {
            case 1:
                automaton.setAcceptingStatesForRegex1();
                automaton.loadTransitionsForRegex1();
                break;
            case 2:
                automaton.setAcceptingStatesForRegex2();
                automaton.loadTransitionsForRegex2();
                break;
            case 0:
                System.out.println("Exiting program.");
                return;
            default:
                System.out.println("Invalid option.");
                return;
        }

        // Create a state machine bound to the configured DFA
        StateMachine machine = new StateMachine(automaton);

        // Prompt the user to choose an operation
        System.out.println("Select an option (1/2):");
        System.out.println("1. Validate a string.");
        System.out.println("2. Generate accepted strings.");
        System.out.println("Press 0 to exit.");

        option = readInteger();

        switch (option) {
            case 1:
                handleValidateOption(machine);
                break;
            case 2:
                handleGenerateOption(machine);
                break;
            case 0:
                System.out.println("Exiting program.");
                break;
            default:
                System.out.println("Invalid option.");
                break;
        }
    }

    /**
     * Reads an integer from standard input, retrying on invalid input.
     *
     * @return the integer entered by the user
     */
    private static int readInteger() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("ERROR: Invalid input. Please enter an integer.");
                scanner.next(); // Clear the invalid token from the buffer
            }
        }
    }

    /**
     * Prompts the user for a string and validates it against the DFA.
     *
     * @param machine the configured state machine
     */
    private static void handleValidateOption(StateMachine machine) {
        System.out.println("Enter a string of characters:");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.next();
        boolean isValid = machine.validateString(input);
        System.out.println("Is the string '" + input + "' valid? " + isValid);
    }

    /**
     * Prompts the user for generation parameters and prints the generated
     * strings that are accepted by the DFA.
     *
     * @param machine the configured state machine
     */
    private static void handleGenerateOption(StateMachine machine) {
        System.out.println("Enter the maximum number of strings to generate:");
        int maxCount = readInteger();

        System.out.println("Enter the maximum length of each string:");
        int maxLength = readInteger();

        List<String> generatedStrings = new ArrayList<>();
        machine.generateStrings(generatedStrings, maxCount, maxLength);
        System.out.println("Generated strings: " + generatedStrings);
    }
}
