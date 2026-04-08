import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a Deterministic Finite Automaton (DFA) used to validate strings
 * against regular expressions and generate accepted languages.
 *
 * <p>The DFA is defined by a 5-tuple: (alphabet, states, initialState,
 * acceptingStates, transitionTable). Two pre-configured regular expressions
 * are supported:</p>
 * <ul>
 *   <li>Regex 1: {@code (b|c)*a(b|c)*}</li>
 *   <li>Regex 2: {@code a+(a|b|c)*}</li>
 * </ul>
 *
 * @author Sergio Alonso Zarcero
 */
public class DeterministicFiniteAutomaton {

    /** The set of valid input symbols for this automaton. */
    private List<Character> alphabet = new ArrayList<>();

    /** The set of all states in the automaton (identified by integer IDs). */
    private List<Integer> states = new ArrayList<>();

    /** The starting state of the automaton. */
    private Integer initialState = 0;

    /** The set of states that mark a string as accepted. */
    private List<Integer> acceptingStates = new ArrayList<>();

    /**
     * The transition table mapping (currentState, inputSymbol) → nextState.
     * Outer map key = current state, inner map key = input character,
     * inner map value = next state.
     */
    private Map<Integer, Map<Character, Integer>> transitionTable = new HashMap<>();

    // ── Getters & Setters ────────────────────────────────────────────────

    public List<Character> getAlphabet() {
        return alphabet;
    }

    public void setAlphabet(List<Character> alphabet) {
        this.alphabet = alphabet;
    }

    public List<Integer> getStates() {
        return states;
    }

    public void setStates(List<Integer> states) {
        this.states = states;
    }

    public Integer getInitialState() {
        return initialState;
    }

    public void setInitialState(Integer initialState) {
        this.initialState = initialState;
    }

    public List<Integer> getAcceptingStates() {
        return acceptingStates;
    }

    public void setAcceptingStates(List<Integer> acceptingStates) {
        this.acceptingStates = acceptingStates;
    }

    public Map<Integer, Map<Character, Integer>> getTransitionTable() {
        return transitionTable;
    }

    public void setTransitionTable(Map<Integer, Map<Character, Integer>> transitionTable) {
        this.transitionTable = transitionTable;
    }

    // ── Configuration Methods ────────────────────────────────────────────

    /**
     * Loads the default alphabet {a, b, c} into the automaton.
     */
    public void loadAlphabet() {
        alphabet.add('a');
        alphabet.add('b');
        alphabet.add('c');
    }

    /**
     * Initializes the automaton with states numbered 0 through 10.
     * This provides enough states for the pre-configured regular expressions.
     */
    public void loadStates() {
        for (int i = 0; i <= 10; i++) {
            states.add(i);
        }
    }

    /**
     * Sets accepting states for Regex 1: {@code (b|c)*a(b|c)*}.
     * State 1 is the only accepting state.
     */
    public void setAcceptingStatesForRegex1() {
        acceptingStates.add(1);
    }

    /**
     * Sets accepting states for Regex 2: {@code a+(a|b|c)*}.
     * State 1 is the only accepting state.
     */
    public void setAcceptingStatesForRegex2() {
        acceptingStates.add(1);
    }

    /**
     * Initializes the transition table with empty transition maps for each state.
     * Must be called before loading any specific transition configuration.
     */
    public void initializeTransitionTable() {
        for (int i = 0; i < states.size(); i++) {
            transitionTable.put(i, new HashMap<>());
        }
    }

    /**
     * Loads transitions for Regex 1: {@code (b|c)*a(b|c)*}.
     *
     * <p>Transition table:</p>
     * <pre>
     *   q0 --a--> q1,  q0 --b--> q0,  q0 --c--> q0
     *   q1 --b--> q1,  q1 --c--> q1
     * </pre>
     */
    public void loadTransitionsForRegex1() {
        initializeTransitionTable();
        transitionTable.get(0).put('a', 1);
        transitionTable.get(0).put('b', 0);
        transitionTable.get(0).put('c', 0);
        transitionTable.get(1).put('b', 1);
        transitionTable.get(1).put('c', 1);
    }

    /**
     * Loads transitions for Regex 2: {@code a+(a|b|c)*}.
     *
     * <p>Transition table:</p>
     * <pre>
     *   q0 --a--> q1
     *   q1 --a--> q1,  q1 --b--> q1,  q1 --c--> q1
     * </pre>
     */
    public void loadTransitionsForRegex2() {
        initializeTransitionTable();
        transitionTable.get(0).put('a', 1);
        transitionTable.get(1).put('a', 1);
        transitionTable.get(1).put('b', 1);
        transitionTable.get(1).put('c', 1);
    }

    /**
     * Returns the next state given a current state and input character.
     *
     * @param state     the current state
     * @param character the input character
     * @return the next state, or {@code null} if no transition is defined
     */
    public Integer getNextState(Integer state, Character character) {
        return transitionTable.get(state).get(character);
    }
}
