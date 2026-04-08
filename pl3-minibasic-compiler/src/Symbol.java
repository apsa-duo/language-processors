/**
 * Represents a symbol (variable) in the compiler's symbol table.
 *
 * <p>Each symbol tracks its name, inferred type, current value during
 * interpretation, and its assigned storage location name for Jasmin
 * bytecode generation.</p>
 *
 * @author Asier Alamo, Sergio Alonso Zarcero, Andrea Pascual Aguilera
 */
public class Symbol {

    /** The variable's identifier name. */
    private String name;

    /** The inferred type (e.g., "int", "string", "boolean", "array[int]"). */
    private String type;

    /** The current runtime value during interpretation. */
    private Object value;

    /** The JVM local variable storage name (e.g., "local_1"). */
    private String storageName;

    /**
     * Creates a new symbol with the given name and type.
     *
     * @param name the variable identifier
     * @param type the inferred type string
     */
    public Symbol(String name, String type) {
        this.name = name;
        this.type = type;
        this.value = null;
        this.storageName = null;
    }

    /**
     * Returns the symbol's identifier name.
     *
     * @return the variable name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the symbol's inferred type.
     *
     * @return the type string
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the symbol's current value.
     *
     * @return the runtime value, or {@code null} if unassigned
     */
    public Object getValue() {
        return value;
    }

    /**
     * Sets the symbol's runtime value.
     *
     * @param value the new value
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * Returns the JVM storage name used in Jasmin code generation.
     *
     * @return the storage name (e.g., "local_1")
     */
    public String getStorageName() {
        return storageName;
    }

    /**
     * Sets the JVM storage name for Jasmin code generation.
     *
     * @param storageName the local variable storage identifier
     */
    public void setStorageName(String storageName) {
        this.storageName = storageName;
    }

    @Override
    public String toString() {
        return String.format("Symbol{name='%s', type='%s', value=%s, storageName='%s'}",
                name, type, value, storageName);
    }
}
