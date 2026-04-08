/**
 * Represents a single cell in the treasure map grid.
 *
 * <p>Each cell can hold one of the following special elements:</p>
 * <ul>
 *   <li><b>Treasure</b> — awards points when discovered</li>
 *   <li><b>Forbidden Zone</b> — deducts points when entered</li>
 *   <li><b>Shipwreck</b> — awards points (only on first discovery)</li>
 *   <li><b>Island</b> — grants extra attempts (only on first discovery)</li>
 * </ul>
 *
 * @author Asier Alamo, Sergio Alonso Zarcero, Andrea Pascual Aguilera
 */
public class GridCell {

    /** Whether this cell contains a treasure. */
    boolean hasTreasure;

    /** Whether this cell is a forbidden zone (penalty on entry). */
    boolean isForbiddenZone;

    /** The point value associated with this cell (reward or penalty). */
    int points;

    /** Whether this cell contains a shipwreck. */
    boolean isShipwreck;

    /** Whether this cell contains an island. */
    boolean isIsland;

    /** Whether this cell has already been discovered by the player. */
    private boolean discovered = false;

    /** The name of the ship at this cell (if it is a shipwreck). */
    private String shipName;

    /**
     * Creates a new grid cell with the specified properties.
     *
     * @param hasTreasure     whether the cell holds a treasure
     * @param isForbiddenZone whether the cell is a forbidden zone
     * @param points          the point value (positive = reward, negative context = penalty)
     * @param isShipwreck     whether the cell holds a shipwreck
     * @param isIsland        whether the cell holds an island
     */
    public GridCell(boolean hasTreasure, boolean isForbiddenZone, int points,
                    boolean isShipwreck, boolean isIsland) {
        this.hasTreasure = hasTreasure;
        this.isForbiddenZone = isForbiddenZone;
        this.points = points;
        this.isShipwreck = isShipwreck;
        this.isIsland = isIsland;
    }

    /**
     * Returns the name of the ship at this cell.
     *
     * @return the ship name, or {@code null} if this is not a shipwreck cell
     */
    public String getShipName() {
        return shipName;
    }

    /**
     * Sets the name of the ship at this cell.
     *
     * @param shipName the ship name to assign
     */
    public void setShipName(String shipName) {
        this.shipName = shipName;
    }

    /**
     * Marks this cell as discovered by the player.
     */
    public void markAsDiscovered() {
        discovered = true;
    }

    /**
     * Checks whether this cell has already been discovered.
     *
     * @return {@code true} if the cell was previously discovered
     */
    public boolean isDiscovered() {
        return discovered;
    }
}
