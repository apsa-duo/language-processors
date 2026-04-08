import java.util.ArrayList;
import java.util.List;

/**
 * Executes a {@link DeterministicFiniteAutomaton} to validate input strings
 * and to generate strings that belong to the automaton's accepted language.
 *
 * <p>The machine maintains a {@code currentState} that advances through
 * the DFA's transition table as characters are consumed. A string is
 * accepted if, after processing all characters, the machine is in one
 * of the accepting states.</p>
 *
 * @author Sergio Alonso Zarcero
 */
public class StateMachine {

    /** The current state of the machine during string processing. */
    private Integer currentState;

    /** The underlying DFA that defines the transition rules. */
    private final DeterministicFiniteAutomaton automaton;

    /**
     * Creates a new state machine bound to the given automaton.
     * The machine starts in the automaton's initial state.
     *
     * @param automaton the DFA to execute
     */
    public StateMachine(DeterministicFiniteAutomaton automaton) {
        this.automaton = automaton;
        this.currentState = automaton.getInitialState();
    }

    public Integer getCurrentState() {
        return currentState;
    }

    public void setCurrentState(Integer currentState) {
        this.currentState = currentState;
    }

    public DeterministicFiniteAutomaton getAutomaton() {
        return automaton;
    }

    /**
     * Validates whether a given string is accepted by the automaton.
     *
     * <p>The machine processes each character sequentially, transitioning
     * between states. If any character has no valid transition, the string
     * is rejected immediately. Otherwise, the string is accepted only if
     * the final state is an accepting state.</p>
     *
     * @param input the string to validate
     * @return {@code true} if the string is accepted, {@code false} otherwise
     */
    public boolean validateString(String input) {
        List<Character> characters = new ArrayList<>();
        for (char c : input.toCharArray()) {
            characters.add(c);
        }

        // An empty string is not valid for these regular expressions
        boolean isValid = false;

        for (Character ch : characters) {
            isValid = validateCharacter(currentState, ch);
            if (!isValid) {
                break;
            }
        }

        // A string is accepted only if all characters were valid AND
        // the machine ends in an accepting state
        if (automaton.getAcceptingStates().contains(currentState)) {
            return isValid;
        }

        return false;
    }

    /**
     * Attempts to transition from the given state using the specified character.
     *
     * @param state     the current state
     * @param character the input character to process
     * @return {@code true} if a valid transition exists, {@code false} otherwise
     */
    private boolean validateCharacter(Integer state, Character character) {
        try {
            Integer nextState = automaton.getNextState(state, character);

            if (nextState != null) {
                currentState = nextState;
                return true;
            } else {
                // No transition defined — check if current state is accepting
                return automaton.getAcceptingStates().contains(state);
            }
        } catch (Exception e) {
            // Transition lookup failed (e.g., state not in table)
            return false;
        }
    }

    /**
     * Generates up to {@code maxCount} accepted strings with a maximum
     * length of {@code maxLength} characters.
     *
     * @param results   the list to populate with generated strings
     * @param maxCount  the maximum number of strings to generate
     * @param maxLength the maximum character length of each string
     */
    public void generateStrings(List<String> results, int maxCount, int maxLength) {
        Integer startState = automaton.getInitialState();
        generateStringsRecursively(startState, "", results, maxLength, maxCount);
    }

    /**
     * Recursively builds strings by exploring all valid transitions from
     * the current state, collecting any string that reaches an accepting state.
     *
     * @param state         the current state being explored
     * @param currentString the string built so far
     * @param results       the accumulator list for accepted strings
     * @param maxLength     the maximum allowed string length
     * @param maxCount      the maximum number of strings to collect
     */
    private void generateStringsRecursively(Integer state, String currentString,
                                            List<String> results, int maxLength, int maxCount) {
        if (results.size() >= maxCount) {
            return;
        }

        // If a string reaches an accepting state, add it even before reaching maxLength
        if (automaton.getAcceptingStates().contains(state)) {
            results.add(currentString);

            if (results.size() >= maxCount) {
                return;
            }
        }

        // Stop recursion when the string reaches maximum length
        if (currentString.length() == maxLength) {
            return;
        }

        // Explore all possible transitions from the current state
        for (Character c : automaton.getAlphabet()) {
            Integer nextState = automaton.getNextState(state, c);
            if (nextState != null) {
                generateStringsRecursively(nextState, currentString + c, results, maxLength, maxCount);

                if (results.size() >= maxCount) {
                    return;
                }
            }
        }
    }
}
