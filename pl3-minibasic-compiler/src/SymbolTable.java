import java.util.HashMap;
import java.util.Map;

/**
 * Global symbol table for the MiniBasic compiler.
 *
 * <p>Provides a flat (single-scope) mapping from variable names to
 * {@link Symbol} objects. Supports definition, assignment, and lookup
 * operations.</p>
 *
 * @author Asier Alamo, Sergio Alonso Zarcero, Andrea Pascual Aguilera
 */
public class SymbolTable {

    /** Internal storage of symbols keyed by variable name. */
    private Map<String, Symbol> symbols;

    /**
     * Creates a new empty symbol table.
     */
    public SymbolTable() {
        symbols = new HashMap<>();
    }

    /**
     * Defines a new variable in the symbol table.
     *
     * @param name the variable name
     * @param type the inferred type (e.g., "int", "string", "boolean")
     * @return {@code true} if the variable was defined successfully,
     *         {@code false} if a variable with this name already exists
     */
    public boolean define(String name, String type) {
        if (symbols.containsKey(name)) {
            return false;
        }
        Symbol symbol = new Symbol(name, type);
        symbol.setStorageName(generateStorageName(name));
        symbols.put(name, symbol);
        return true;
    }

    /**
     * Assigns a value to an existing variable.
     *
     * @param name  the variable name
     * @param value the value to assign
     * @return {@code true} if the assignment was successful,
     *         {@code false} if the variable is not defined
     */
    public boolean assign(String name, Object value) {
        Symbol symbol = resolve(name);
        if (symbol != null) {
            symbol.setValue(value);
            return true;
        }
        return false;
    }

    /**
     * Looks up a variable by name.
     *
     * @param name the variable name to resolve
     * @return the {@link Symbol} if found, or {@code null} if not defined
     */
    public Symbol resolve(String name) {
        return symbols.get(name);
    }

    /**
     * Generates a unique storage name for a variable. Used internally
     * to assign JVM-level identifiers before actual local indices are set.
     *
     * @param variableName the source-level variable name
     * @return a prefixed storage name
     */
    private String generateStorageName(String variableName) {
        return "storage_" + variableName;
    }

    /**
     * Checks whether a variable has been defined.
     *
     * @param name the variable name
     * @return {@code true} if the variable exists in the table
     */
    public boolean isDefined(String name) {
        return symbols.containsKey(name);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Global Scope:\n");
        for (Symbol symbol : symbols.values()) {
            sb.append("  ").append(symbol).append("\n");
        }
        return sb.toString();
    }
}
