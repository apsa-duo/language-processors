/**
 * Manages the interactive treasure hunt game logic.
 *
 * <p>The player dives into grid cells to discover treasures, shipwrecks,
 * and islands while avoiding forbidden zones. The game ends when attempts
 * are exhausted or all treasures are found.</p>
 *
 * @author Asier Alamo, Sergio Alonso Zarcero, Andrea Pascual Aguilera
 */
public class TreasureHuntGame {

    /** The map being explored. */
    private final TreasureMap map;

    /** The player's current score. */
    private int score;

    /** The number of remaining dive attempts. */
    private int remainingAttempts;

    /**
     * Creates a new game session on the given map.
     *
     * @param map              the treasure map to explore
     * @param remainingAttempts the initial number of dive attempts
     */
    public TreasureHuntGame(TreasureMap map, int remainingAttempts) {
        this.map = map;
        this.score = 0;
        this.remainingAttempts = remainingAttempts;
    }

    /**
     * Performs a dive at the specified coordinates and processes the result.
     *
     * <p>Possible outcomes:</p>
     * <ul>
     *   <li><b>Empty cell</b> — no effect</li>
     *   <li><b>Treasure</b> — points awarded, treasure removed</li>
     *   <li><b>Forbidden zone</b> — points deducted</li>
     *   <li><b>Shipwreck</b> — points awarded on first visit only</li>
     *   <li><b>Island</b> — grants 3 extra attempts on first visit
     *       (only if treasures remain)</li>
     * </ul>
     *
     * @param x the column coordinate to dive at
     * @param y the row coordinate to dive at
     */
    public void diveAt(int x, int y) {
        GridCell cell = map.getCell(x, y);

        if (cell == null) {
            System.out.println("Empty cell. Nothing here.");
        } else if (cell.hasTreasure) {
            score += cell.points;
            cell.hasTreasure = false; // Mark treasure as collected
            System.out.println("You found a treasure! Points earned: " + cell.points
                    + ". Total score: " + score);
        } else if (cell.isForbiddenZone) {
            score -= cell.points;
            System.out.println("Forbidden zone! Points lost: " + cell.points
                    + ". Total score: " + score);
        } else if (cell.isShipwreck) {
            if (cell.isDiscovered()) {
                System.out.println("You already discovered the ship \""
                        + cell.getShipName() + "\". No additional points.");
            } else {
                score += cell.points;
                cell.markAsDiscovered();
                System.out.println("You found the ship \"" + cell.getShipName()
                        + "\"! Points earned: " + cell.points + ". Total score: " + score);
            }
        } else if (cell.isIsland) {
            System.out.println("You reached an island! You found a special place.");
            // Grant extra attempts only if treasures remain and island is unvisited
            if (map.hasRemainingTreasures() && !cell.isDiscovered()) {
                System.out.println("You gain 3 extra attempts to find the treasures!");
                remainingAttempts += 3;
                cell.markAsDiscovered();
            } else {
                System.out.println("But you had already discovered this island...");
            }
        } else {
            System.out.println("Nothing at this cell.");
        }

        remainingAttempts--;
    }

    /**
     * Checks whether the game has ended.
     *
     * @return {@code true} if attempts are exhausted or no treasures remain
     */
    public boolean isGameOver() {
        return remainingAttempts <= 0 || !map.hasRemainingTreasures();
    }

    /**
     * Returns the player's current score.
     *
     * @return the total accumulated score
     */
    public int getScore() {
        return score;
    }
}
