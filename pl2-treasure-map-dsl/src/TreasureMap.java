import java.util.HashMap;
import java.util.Map;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Represents the game grid: a fixed-size map of {@link GridCell} objects
 * indexed by (x, y) coordinates.
 *
 * <p>The map can be populated either programmatically (via ANTLR parse
 * tree listeners) or by loading ship positions from a text file.</p>
 *
 * @author Asier Alamo, Sergio Alonso Zarcero, Andrea Pascual Aguilera
 */
public class TreasureMap {

    /** The width of the grid (number of columns). */
    static final int GRID_WIDTH = 5;

    /** The height of the grid (number of rows). */
    static final int GRID_HEIGHT = 5;

    /** Storage for cells, keyed by "x,y" coordinate strings. */
    private Map<String, GridCell> cells = new HashMap<>();

    /**
     * Returns the full map of cells.
     *
     * @return all cells indexed by coordinate string
     */
    public Map<String, GridCell> getCells() {
        return cells;
    }

    /**
     * Places a cell at the specified coordinates if within grid bounds.
     *
     * @param x    the column index (0-based)
     * @param y    the row index (0-based)
     * @param cell the cell to place
     */
    public void placeCell(int x, int y, GridCell cell) {
        if (x >= 0 && x < GRID_WIDTH && y >= 0 && y < GRID_HEIGHT) {
            String key = x + "," + y;
            cells.put(key, cell);
        }
    }

    /**
     * Retrieves the cell at the specified coordinates.
     *
     * @param x the column index
     * @param y the row index
     * @return the cell at (x, y), or {@code null} if empty
     */
    public GridCell getCell(int x, int y) {
        return cells.get(x + "," + y);
    }

    /**
     * Checks whether any undiscovered treasures remain on the map.
     *
     * @return {@code true} if at least one cell still has a treasure
     */
    public boolean hasRemainingTreasures() {
        for (GridCell cell : cells.values()) {
            if (cell.hasTreasure) {
                return true;
            }
        }
        return false;
    }

    /**
     * Loads ship (shipwreck) positions from a structured text file.
     *
     * <p>Expected line format: {@code "ShipName" esta enterrado en X,Y}</p>
     *
     * @param filePath the path to the map definition file
     */
    public void loadMapFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("esta enterrado en")) {
                    // Extract the ship name between quotes
                    int nameStart = line.indexOf("\"") + 1;
                    int nameEnd = line.indexOf("\"", nameStart);
                    String shipName = line.substring(nameStart, nameEnd);

                    // Extract the X,Y coordinates after "en"
                    int coordStart = line.indexOf("en") + 3;
                    String[] coordinates = line.substring(coordStart).split(",");
                    int x = Integer.parseInt(coordinates[0].trim());
                    int y = Integer.parseInt(coordinates[1].trim());

                    // Create a shipwreck cell and place it on the map
                    GridCell shipCell = new GridCell(false, false, 0, true, false);
                    shipCell.setShipName(shipName);
                    placeCell(x, y, shipCell);

                    System.out.println("Loaded ship: " + shipName + " at (" + x + "," + y + ")");
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading map file: " + e.getMessage());
        }
    }

    /**
     * Returns all cells that contain a shipwreck.
     *
     * @return a map of coordinate strings to shipwreck cells
     */
    public Map<String, GridCell> getShipwreckCells() {
        Map<String, GridCell> ships = new HashMap<>();
        for (Map.Entry<String, GridCell> entry : cells.entrySet()) {
            if (entry.getValue().isShipwreck) {
                ships.put(entry.getKey(), entry.getValue());
            }
        }
        return ships;
    }
}
